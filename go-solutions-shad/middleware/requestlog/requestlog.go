//go:build !solution

package requestlog

import (
	"github.com/felixge/httpsnoop"
	"github.com/gofrs/uuid"
	"net/http"
	"time"

	"go.uber.org/zap"
)

func Log(l *zap.Logger) func(next http.Handler) http.Handler {
	return func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			start := time.Now()
			id, _ := uuid.NewV4()

			defer func() {
				if err := recover(); err != nil {
					l.Info("request panicked",
						zap.String("path", r.URL.Path),
						zap.String("method", r.Method),
						zap.String("request_id", id.String()))
					panic(err)
				}
			}()

			l.Info("request started",
				zap.String("path", r.URL.Path),
				zap.String("method", r.Method),
				zap.String("request_id", id.String()),
			)

			snoop := httpsnoop.CaptureMetrics(next, w, r)
			end := time.Now()
			duration := end.Sub(start)
			l.Info("request finished",
				zap.String("path", r.URL.Path),
				zap.String("method", r.Method),
				zap.String("request_id", id.String()),
				zap.Duration("duration", duration),
				zap.Int("status_code", snoop.Code))
		})
		//for push
	}
}
