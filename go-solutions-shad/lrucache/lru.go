//go:build !solution

package lrucache

import (
	"container/list"
	"sync"
)

type pair struct {
	key   int
	value int
}

type lruCache struct {
	capacity   int
	linkedList *list.List
	pairs      map[int]*list.Element
	pool       *sync.Pool
}

func New(cap int) Cache {
	return &lruCache{
		capacity:   cap,
		linkedList: list.New(),
		pairs:      make(map[int]*list.Element, cap+1),
		pool: &sync.Pool{
			New: func() interface{} { return &pair{} },
		},
	}
}

func (c *lruCache) Get(key int) (int, bool) {
	elem, ok := c.pairs[key]
	if !ok {
		return 0, false
	}
	
	if elem != c.linkedList.Back() {
		c.linkedList.MoveToBack(elem)
	}
	
	return elem.Value.(*pair).value, true
}

func (c *lruCache) Set(key, value int) {
	elem, ok := c.pairs[key]
	if ok {
		p := elem.Value.(*pair)
		p.value = value
		if elem != c.linkedList.Back() {
			c.linkedList.MoveToBack(elem)
		}
		return
	}

	newPair := c.pool.Get().(*pair)
	newPair.key = key
	newPair.value = value
	elem = c.linkedList.PushBack(newPair)
	c.pairs[key] = elem

	if c.linkedList.Len() > c.capacity {
		oldest := c.linkedList.Front()
		c.linkedList.Remove(oldest)
		oldPair := oldest.Value.(*pair)
		delete(c.pairs, oldPair.key)
		c.pool.Put(oldPair)
	}
}

func (c *lruCache) Range(f func(key, value int) bool) {
	for elem := c.linkedList.Front(); elem != nil; elem = elem.Next() {
		p := elem.Value.(*pair)
		if !f(p.key, p.value) {
			break
		}
	}
}

func (c *lruCache) Clear() {
	for elem := c.linkedList.Front(); elem != nil; elem = elem.Next() {
		c.pool.Put(elem.Value.(*pair))
	}
	c.linkedList = list.New()
	c.pairs = make(map[int]*list.Element, c.capacity+1)
}
