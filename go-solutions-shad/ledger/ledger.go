//go:build !solution

package ledger

import (
	"context"
	"database/sql"
	"errors"
	"fmt"
	"strings"

	_ "github.com/jackc/pgx/v5/stdlib"
)

type ledger struct {
	db *sql.DB
}

func New(ctx context.Context, dsn string) (Ledger, error) {
	db, err := sql.Open("pgx", dsn)
	if err != nil {
		return nil, fmt.Errorf("open db: %w", err)
	}

	if err := db.PingContext(ctx); err != nil {
		db.Close()
		return nil, fmt.Errorf("ping db: %w", err)
	}

	l := &ledger{db: db}

	_, err = l.db.ExecContext(ctx, `
		CREATE TABLE IF NOT EXISTS accounts (
			id VARCHAR(255) PRIMARY KEY,
			balance BIGINT NOT NULL DEFAULT 0,
			CHECK (balance >= 0)
		)`)
	if err != nil {
		db.Close()
		return nil, fmt.Errorf("create table: %w", err)
	}

	return l, nil
}

func (l *ledger) CreateAccount(ctx context.Context, id ID) error {
	if strings.TrimSpace(string(id)) == "" {
		return errors.New("empty account id")
	}

	result, err := l.db.ExecContext(ctx, `
        INSERT INTO accounts (id, balance)
        VALUES ($1, 0)
        ON CONFLICT (id) DO NOTHING
    `, string(id))
	if err != nil {
		return fmt.Errorf("create account: %w", err)
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return fmt.Errorf("check rows affected: %w", err)
	}
	if rowsAffected == 0 {
		return fmt.Errorf("account already exists")
	}

	return nil
}

func (l *ledger) GetBalance(ctx context.Context, id ID) (Money, error) {
	var balance Money
	err := l.db.QueryRowContext(ctx, `
		SELECT balance FROM accounts WHERE id = $1
	`, string(id)).Scan(&balance)

	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return 0, fmt.Errorf("account not found")
		}
		return 0, fmt.Errorf("get balance: %w", err)
	}
	return balance, nil
}

func (l *ledger) Deposit(ctx context.Context, id ID, amount Money) error {
	if amount <= 0 {
		return ErrNegativeAmount
	}

	tx, err := l.db.BeginTx(ctx, nil)
	if err != nil {
		return fmt.Errorf("begin tx: %w", err)
	}
	defer func() {
		if err := tx.Rollback(); err != nil && !errors.Is(err, sql.ErrTxDone) {
			fmt.Printf("failed to rollback transaction: %v", err)
		}
	}()

	var exists bool
	err = tx.QueryRowContext(ctx, `
		SELECT EXISTS(SELECT 1 FROM accounts WHERE id = $1)
	`, string(id)).Scan(&exists)
	if err != nil {
		return fmt.Errorf("check account: %w", err)
	}
	if !exists {
		return fmt.Errorf("account not found")
	}

	_, err = tx.ExecContext(ctx, `
		UPDATE accounts SET balance = balance + $1 
		WHERE id = $2
	`, amount, string(id))
	if err != nil {
		return fmt.Errorf("deposit: %w", err)
	}

	return tx.Commit()
}

func (l *ledger) Withdraw(ctx context.Context, id ID, amount Money) error {
	if amount <= 0 {
		return ErrNegativeAmount
	}

	tx, err := l.db.BeginTx(ctx, nil)
	if err != nil {
		return fmt.Errorf("begin tx: %w", err)
	}
	defer func() {
		if err := tx.Rollback(); err != nil && !errors.Is(err, sql.ErrTxDone) {
			fmt.Printf("failed to rollback transaction: %v", err)
		}
	}()

	var balance Money
	err = tx.QueryRowContext(ctx, `
		SELECT balance FROM accounts WHERE id = $1 FOR UPDATE
	`, string(id)).Scan(&balance)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return fmt.Errorf("account not found")
		}
		return fmt.Errorf("get balance: %w", err)
	}

	if balance < amount {
		return ErrNoMoney
	}

	_, err = tx.ExecContext(ctx, `
		UPDATE accounts SET balance = balance - $1 
		WHERE id = $2
	`, amount, string(id))
	if err != nil {
		return fmt.Errorf("withdraw: %w", err)
	}

	return tx.Commit()
}

func (l *ledger) Transfer(ctx context.Context, from, to ID, amount Money) error {
	if amount <= 0 {
		return ErrNegativeAmount
	}

	if from == to {
		return errors.New("cannot transfer to same account")
	}

	first, second := string(from), string(to)
	if first > second {
		first, second = second, first
	}

	tx, err := l.db.BeginTx(ctx, nil)
	if err != nil {
		return fmt.Errorf("begin tx: %w", err)
	}
	defer func() {
		if err := tx.Rollback(); err != nil && !errors.Is(err, sql.ErrTxDone) {
			fmt.Printf("failed to rollback transaction: %v", err)
		}
	}()

	_, err = tx.ExecContext(ctx, `
		SELECT 1 FROM accounts WHERE id = $1 FOR UPDATE
	`, first)
	if err != nil {
		return fmt.Errorf("lock first account: %w", err)
	}

	_, err = tx.ExecContext(ctx, `
		SELECT 1 FROM accounts WHERE id = $1 FOR UPDATE
	`, second)
	if err != nil {
		return fmt.Errorf("lock second account: %w", err)
	}

	var fromBalance Money
	err = tx.QueryRowContext(ctx, `
		SELECT balance FROM accounts WHERE id = $1
	`, string(from)).Scan(&fromBalance)
	if err != nil {
		return fmt.Errorf("get from balance: %w", err)
	}

	if fromBalance < amount {
		return ErrNoMoney
	}

	var toExists bool
	err = tx.QueryRowContext(ctx, `
		SELECT EXISTS(SELECT 1 FROM accounts WHERE id = $1)
	`, string(to)).Scan(&toExists)
	if err != nil {
		return fmt.Errorf("check to account: %w", err)
	}
	if !toExists {
		return fmt.Errorf("recipient account not found")
	}

	_, err = tx.ExecContext(ctx, `
		UPDATE accounts SET balance = balance - $1 
		WHERE id = $2
	`, amount, string(from))
	if err != nil {
		return fmt.Errorf("update from: %w", err)
	}

	_, err = tx.ExecContext(ctx, `
		UPDATE accounts SET balance = balance + $1 
		WHERE id = $2
	`, amount, string(to))
	if err != nil {
		return fmt.Errorf("update to: %w", err)
	}

	return tx.Commit()
}

func (l *ledger) Close() error {
	return l.db.Close()
}
