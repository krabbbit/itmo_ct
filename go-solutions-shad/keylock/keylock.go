//go:build !solution

package keylock

import "sync"

type KeyLock struct {
	mu    sync.Mutex
	locks map[string]chan struct{}
}

func New() *KeyLock {
	return &KeyLock{
		locks: make(map[string]chan struct{}),
	}
}

func (l *KeyLock) LockKeys(keys []string, cancel <-chan struct{}) (bool, func()) {
	l.mu.Lock()

	allFree := true
	for _, key := range keys {
		if _, locked := l.locks[key]; locked {
			allFree = false
			break
		}
	}

	if allFree {
		for _, key := range keys {
			l.locks[key] = make(chan struct{})
		}
		l.mu.Unlock()
		return false, func() { l.unlock(keys) }
	}

	waitChans := make([]<-chan struct{}, 0, len(keys))
	for _, key := range keys {
		if ch, exists := l.locks[key]; exists {
			waitChans = append(waitChans, ch)
		}
	}
	l.mu.Unlock()
	select {
	case <-waitFunc(waitChans):
		return l.LockKeys(keys, cancel)
	case <-cancel:
		return true, nil
	}
}

func (l *KeyLock) unlock(keys []string) {
	l.mu.Lock()
	defer l.mu.Unlock()

	for _, key := range keys {
		if ch, exists := l.locks[key]; exists {
			close(ch)
			delete(l.locks, key)
		}
	}
}

func waitFunc(chans []<-chan struct{}) <-chan struct{} {
	out := make(chan struct{})
	var wg sync.WaitGroup
	wg.Add(len(chans))

	for _, ch := range chans {
		go func(c <-chan struct{}) {
			<-c
			wg.Done()
		}(ch)
	}

	go func() {
		wg.Wait()
		close(out)
	}()

	return out
}
