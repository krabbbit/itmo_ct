//go:build !solution

package rwmutex

type RWMutex struct {
	writeMutex chan struct{}
	readMutex  chan struct{}
	count      int
}

func New() *RWMutex {
	return &RWMutex{
		writeMutex: make(chan struct{}, 1),
		readMutex:  make(chan struct{}, 1),
		count:      0,
	}
}

func (rw *RWMutex) Lock() {
	rw.writeMutex <- struct{}{}
}

func (rw *RWMutex) Unlock() {
	<-rw.writeMutex
}

func (rw *RWMutex) RLock() {
	rw.readMutex <- struct{}{}
	if rw.count == 0 {
		rw.writeMutex <- struct{}{}
	}
	rw.count++
	<-rw.readMutex
}

func (rw *RWMutex) RUnlock() {
	rw.readMutex <- struct{}{}
	rw.count--
	if rw.count == 0 {
		<-rw.writeMutex
	}
	<-rw.readMutex
}
