//go:build !solution

package otp

import (
	"io"
)

type Cipher struct {
	r    io.Reader
	w    io.Writer
	prng io.Reader
}

func (c *Cipher) Read(p []byte) (n int, err error) {
	n, err = c.r.Read(p)
	if err != nil && n == 0 {
		return n, err
	}
	randoms := make([]byte, n)
	n, err = c.prng.Read(randoms)
	if err != nil {
		return n, err
	}
	for i := range n {
		p[i] ^= randoms[i]
	}
	return n, err
}

func (c *Cipher) Write(p []byte) (n int, err error) {
	data := make([]byte, len(p))
	copy(data, p)
	randoms := make([]byte, len(p))
	_, _ = c.prng.Read(randoms)
	for i := range len(p) {
		data[i] ^= randoms[i]
	}
	return c.w.Write(data)
}

func NewReader(r io.Reader, prng io.Reader) io.Reader {
	return &Cipher{r: r, prng: prng}
}

func NewWriter(w io.Writer, prng io.Reader) io.Writer {
	return &Cipher{w: w, prng: prng}
}
