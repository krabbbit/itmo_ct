//go:build !solution

package spacecollapse

import (
	"strings"
	"unicode"
	"unicode/utf8"
)

func CollapseSpaces(input string) string {
	var b strings.Builder
	var last rune = -1
	b.Grow(len(input))
	for len(input) > 0 {
		r, size := utf8.DecodeRune([]byte(input))
		if last == -1 || !(unicode.IsSpace(r) && unicode.IsSpace(last)) {
			if unicode.IsSpace(r) {
				b.WriteRune(' ')
			} else {
				b.WriteRune(r)
			}
		}
		input = input[size:]
		last = r
	}
	return b.String()
}
