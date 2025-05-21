//go:build !solution

package waitgroup

type WaitGroup struct {
	counter int
	wait    chan struct{}
	mu      chan struct{}
}

func New() *WaitGroup {
	wg := &WaitGroup{
		wait: make(chan struct{}),
		mu:   make(chan struct{}, 1),
	}
	wg.mu <- struct{}{}
	close(wg.wait)
	return wg
}

func (wg *WaitGroup) Add(delta int) {
	<-wg.mu
	defer func() { wg.mu <- struct{}{} }()

	if wg.counter+delta < 0 {
		panic("negative WaitGroup counter")
	}

	old := wg.counter
	wg.counter += delta

	if old == 0 && wg.counter > 0 {
		wg.wait = make(chan struct{})
	} else if old > 0 && wg.counter == 0 {
		close(wg.wait)
	}
}

func (wg *WaitGroup) Done() {
	wg.Add(-1)
}

func (wg *WaitGroup) Wait() {
	<-wg.mu
	wait := wg.wait
	wg.mu <- struct{}{}

	<-wait
}
