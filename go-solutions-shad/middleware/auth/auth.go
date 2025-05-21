//go:build !solution

package auth

import (
	"context"
	"errors"
	"net/http"
	"strings"
)

type User struct {
	Name  string
	Email string
}

type myString string

const userString myString = "user"

func ContextUser(ctx context.Context) (*User, bool) {
	user := ctx.Value(userString).(*User)
	return user, user != nil
}

var ErrInvalidToken = errors.New("invalid token")

type TokenChecker interface {
	CheckToken(ctx context.Context, token string) (*User, error)
}

func isValid(token string) bool {
	array := strings.Split(token, " ")
	return len(array) == 2 && array[0] == "Bearer"
}

func CheckAuth(checker TokenChecker) func(next http.Handler) http.Handler {
	next := func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			token := r.Header.Get("Authorization")
			if isValid(token) {
				user, err := checker.CheckToken(r.Context(), strings.Split(token, " ")[1])
				if err != nil {
					if errors.Is(err, ErrInvalidToken) {
						w.WriteHeader(http.StatusUnauthorized)
					} else {
						w.WriteHeader(http.StatusInternalServerError)
					}
				} else {
					ctx := context.WithValue(r.Context(), userString, user)
					next.ServeHTTP(w, r.WithContext(ctx))
				}

			} else {
				w.WriteHeader(http.StatusUnauthorized)
			}
		})
	}
	return next

	//for push
}
