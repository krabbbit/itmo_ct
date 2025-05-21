//go:build !solution

package dupcall

import (
	"context"
	"sync"
)

type Call struct {
	mu    sync.Mutex
	group *group
}

type group struct {
	done   chan struct{}
	result any
	err    error
}

func (o *Call) Do(
	ctx context.Context,
	cb func(context.Context) (any, error),
) (any, error) {
	o.mu.Lock()

	g := o.group

	if g == nil {
		g = &group{done: make(chan struct{})}
		o.group = g
		o.mu.Unlock()

		go func() {
			g.result, g.err = cb(ctx)
			close(g.done)

			o.mu.Lock()
			o.group = nil
			o.mu.Unlock()
		}()
	} else {
		o.mu.Unlock()
	}

	select {
	case <-g.done:
		return g.result, g.err
	case <-ctx.Done():
		return nil, ctx.Err()
	}
}
