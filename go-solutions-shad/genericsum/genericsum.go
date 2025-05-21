//go:build !solution

package genericsum

import (
	"slices"
	"sort"
	"sync"

	"golang.org/x/exp/constraints"
)

type channel[T any] chan T

type SliceType[T constraints.Ordered] []T

func (s SliceType[T]) Len() int           { return len(s) }
func (s SliceType[T]) Less(i, j int) bool { return s[i] < s[j] }
func (s SliceType[T]) Swap(i, j int)      { s[i], s[j] = s[j], s[i] }

func Min[T constraints.Ordered](a, b T) T {
	if a < b {
		return a
	}
	return b
}

func SortSlice[T constraints.Ordered](a []T) {
	sort.Sort(SliceType[T](a))
}

func MapsEqual[T comparable, K comparable](a, b map[T]K) bool {
	if len(a) != len(b) {
		return false
	}
	for k, v1 := range a {
		v2, ok := b[k]
		if !ok {
			return false
		}
		if v1 != v2 {
			return false
		}
	}
	return true
}

func SliceContains[T comparable](s []T, v T) bool {
	return slices.Contains(s, v)
}

func MergeChans[T any](chs ...<-chan T) <-chan T {
	out := make(channel[T])
	var wg sync.WaitGroup
	wg.Add(len(chs))

	for _, ch := range chs {
		go func(c <-chan T) {
			for v := range c {
				out <- v
			}
			wg.Done()
		}(ch)
	}

	go func() {
		wg.Wait()
		close(out)
	}()

	return out
}

func IsHermitianMatrix[T constraints.Complex | constraints.Integer | constraints.Float](m [][]T) bool {
	n := len(m)
	if n == 0 {
		return true
	}

	for i := 0; i < n; i++ {
		for j := i; j < n; j++ {
			switch any(m[i][j]).(type) {
			case complex128:
				a := any(m[i][j]).(complex128)
				b := any(m[j][i]).(complex128)
				if real(a) != real(b) || imag(a) != -imag(b) {
					return false
				}
			case complex64:
				a := any(m[i][j]).(complex64)
				b := any(m[j][i]).(complex64)
				if real(a) != real(b) || imag(a) != -imag(b) {
					return false
				}
			default:
				if m[i][j] != m[j][i] {
					return false
				}

			}

		}
	}
	return true
}

//for git
