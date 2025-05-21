//go:build !solution

package gzep

import (
	"compress/gzip"
	"io"
	"sync"
)

var syncPool = sync.Pool{
	New: func() interface{} {
		return gzip.NewWriter(nil)
	},
}

func Encode(data []byte, w io.Writer) error {
	ww := syncPool.Get().(*gzip.Writer)
	if ww != nil {
		ww.Reset(w)
	}
	defer syncPool.Put(ww)
	defer func() { _ = ww.Close() }()
	if _, err := ww.Write(data); err != nil {
		return err
	}
	return ww.Flush()
}
