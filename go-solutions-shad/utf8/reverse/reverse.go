//go:build !solution

package reverse

import (
	"strings"
	"unicode/utf8"
)

func Reverse(input string) string {
	var b strings.Builder
	b.Grow(len(input))
	for len(input) > 0 {
		r, size := utf8.DecodeLastRune([]byte(input))
		b.WriteRune(r)
		input = input[:len(input)-size]
	}
	return b.String()
}
