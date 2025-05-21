//go:build !solution

package varfmt

import (
	"fmt"
	"strconv"
	"strings"
)

func Sprintf(format string, args ...interface{}) string {
	var b strings.Builder
	var autoIndex = 0

	index := 0
	for index < len(format) {
		if format[index] == '{' {
			index++
			if index < len(format) && format[index] == '}' {
				b.WriteString(fmt.Sprint(args[autoIndex]))
				autoIndex++
				index++
				continue
			}

			start := index
			for index < len(format) && format[index] != '}' {
				index++
			}

			numStr := format[start:index]
			num, err := strconv.Atoi(numStr)
			if err != nil {
				b.WriteString(fmt.Sprint(args[autoIndex]))
			} else {
				if num >= 0 && num < len(args) {
					b.WriteString(fmt.Sprint(args[num]))
				} else {
					b.WriteString("")
				}
			}
			autoIndex++
			index++
		} else {
			b.WriteByte(format[index])
			index++
		}
	}

	return b.String()
}
