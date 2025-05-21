//go:build !solution

package tparallel

type T struct {
	start     bool
	end       chan struct{}
	barrier   chan struct{}
	parent    *T
	childrens []*T
}

func newT(p *T) *T {
	t := &T{false, make(chan struct{}), make(chan struct{}), p, make([]*T, 0)}
	return t
}

func (t *T) Parallel() {
	t.start = true
	t.parent.childrens = append(t.parent.childrens, t)
	t.end <- struct{}{}
	<-t.parent.barrier
}

func (t *T) helper(subtest func(t *T)) {
	subtest(t)
	if len(t.childrens) > 0 {
		close(t.barrier)
		for _, v := range t.childrens {
			<-v.end
		}
	}
	if t.start {
		t.parent.end <- struct{}{}
	}
	t.end <- struct{}{}
}

func (t *T) Run(subtest func(t *T)) {
	childrens := newT(t)
	go childrens.helper(subtest)
	<-childrens.end
}

func Run(topTests []func(t *T)) {
	gigaParent := newT(nil)
	for _, v := range topTests {
		gigaParent.Run(v)
	}
	close(gigaParent.barrier)
	if len(gigaParent.childrens) >= 1 {
		<-gigaParent.end
	}
}
