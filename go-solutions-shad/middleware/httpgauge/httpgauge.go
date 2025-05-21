//go:build !solution

package httpgauge

import (
	"fmt"
	"net/http"
	"sort"
	"strconv"
	"strings"
	"sync"
)

// Gauge for push
type Gauge struct {
	metrics map[string]int
	mu      sync.RWMutex
}

func New() *Gauge {
	return &Gauge{
		metrics: make(map[string]int),
		mu:      sync.RWMutex{},
	}
}

func (g *Gauge) Snapshot() map[string]int {
	g.mu.RLock()
	defer g.mu.RUnlock()
	return g.metrics
}

func (g *Gauge) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	keys := make([]string, 0, len(g.metrics))
	for k := range g.metrics {
		keys = append(keys, k)
	}
	sort.Strings(keys)
	s := ""
	for _, k := range keys {
		g.mu.RLock()
		s += k + " " + strconv.Itoa(g.metrics[k]) + "\n"
		g.mu.RUnlock()
	}
	_, _ = fmt.Fprintf(w, "%s", s)
}

func (g *Gauge) Wrap(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		s := r.URL.Path
		if strings.Contains(s, "user") {
			s = "/user/{userID}"
		}

		defer func() {
			if s != "" {
				g.mu.Lock()
				g.metrics[s]++
				g.mu.Unlock()
			}
		}()
		next.ServeHTTP(w, r)
	})
}
