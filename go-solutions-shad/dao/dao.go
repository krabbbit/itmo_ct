//go:build !solution

package dao

import (
	"context"
	"database/sql"
	"errors"

	_ "github.com/jackc/pgx/v5/stdlib"
)

type dao struct {
	db *sql.DB
}

// Close implements Dao.
func (d *dao) Close() error {
	if d.db != nil {
		return d.db.Close()
	}
	return nil
}

// Create implements Dao.
func (d *dao) Create(ctx context.Context, u *User) (UserID, error) {
	var id UserID
	err := d.db.QueryRowContext(ctx, `
		INSERT INTO users (name) 
		VALUES ($1)
		RETURNING id
	`, u.Name).Scan(&id)
	if err != nil {
		return -1, err
	}
	return id, nil
}

func (d *dao) baseRequest(ctx context.Context, query string, args ...any) error {
	result, err := d.db.ExecContext(ctx, query, args...)
	if err != nil {
		return err
	}
	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return err
	}

	if rowsAffected == 0 {
		return errors.New("no rows affected")
	}

	return nil
}

// Delete implements Dao.
func (d *dao) Delete(ctx context.Context, id UserID) error {
	return d.baseRequest(ctx, `
		DELETE FROM users
		WHERE id = $1
	`, id)
}

// List implements Dao.
func (d *dao) List(ctx context.Context) ([]User, error) {
	query := `
        SELECT id, name
        FROM users
        ORDER BY id
    `

	rows, err := d.db.QueryContext(ctx, query)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var users []User
	for rows.Next() {
		var user User
		if err := rows.Scan(&user.ID, &user.Name); err != nil {
			return nil, err
		}
		users = append(users, user)
	}

	if err := rows.Err(); err != nil {
		return nil, err
	}

	return users, nil
}

// Lookup implements Dao.
func (d *dao) Lookup(ctx context.Context, id UserID) (User, error) {
	user := User{}
	err := d.db.QueryRowContext(ctx, `
        SELECT id, name 
        FROM users
        WHERE id = $1
    `, id).Scan(&user.ID, &user.Name)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return User{}, sql.ErrNoRows
		}
		return User{}, err
	}
	return user, nil
}

// Update implements Dao.
func (d *dao) Update(ctx context.Context, u *User) error {
	return d.baseRequest(ctx, `
		UPDATE users
		SET name = $1
		WHERE id = $2
	`, u.Name, u.ID)
}

func createConn(ctx context.Context, dsn string) (*sql.DB, error) {
	db, err := sql.Open("pgx", dsn)
	if err != nil {
		return nil, err
	}
	if err := db.PingContext(ctx); err != nil {
		db.Close()
		return nil, err
	}
	return db, nil
}

func CreateDao(ctx context.Context, dsn string) (Dao, error) {
	db, err := createConn(ctx, dsn)
	if err != nil {
		return nil, err
	}
	dao := &dao{db: db}
	_, err = dao.db.ExecContext(ctx, `
		CREATE TABLE users(
			id BIGSERIAL PRIMARY KEY,
			name varchar(50)
		);
		`)
	if err != nil {
		dao.db.Close()
		return nil, err
	}
	return dao, nil
}
