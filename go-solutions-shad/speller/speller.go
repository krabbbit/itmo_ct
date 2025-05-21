//go:build !solution

package speller

import "unicode"

var digits = map[int]string{
	1: "one",
	2: "two",
	3: "three",
	4: "four",
	5: "five",
	6: "six",
	7: "seven",
	8: "eight",
	9: "nine",
}

var teens = map[int]string{
	11: "eleven",
	12: "twelve",
	13: "thirteen",
	14: "fourteen",
	15: "fifteen",
	16: "sixteen",
	17: "seventeen",
	18: "eighteen",
	19: "nineteen",
}

var tens = map[int]string{
	10: "ten",
	20: "twenty",
	30: "thirty",
	40: "forty",
	50: "fifty",
	60: "sixty",
	70: "seventy",
	80: "eighty",
	90: "ninety",
}

func hundredToString(x int) string {
	var h = x / 100
	var res string
	if h > 0 {
		res += digits[h] + " hundred "
	}
	var t = x % 100
	if t%10 == 0 && t != 0 {
		res += tens[t] + " "
	} else if t > 10 && t < 20 {
		res += teens[t] + " "
	} else if t < 10 && t != 0 {
		res += digits[t] + " "
	} else if t != 0 {
		res += tens[t/10*10] + "-" + digits[t%10] + " "
	}
	return res
}

func Spell(n int64) string {
	var answer string
	if n == 0 {
		return "zero"
	}
	if n < 0 {
		answer = "minus "
		n = -n
	}
	if n/1000000000 > 0 {
		answer += hundredToString(int(n/1000000000)) + "billion "
	}
	n %= 1000000000
	if n/1000000 > 0 {
		answer += hundredToString(int(n/1000000)) + "million "
	}
	n %= 1000000
	if n/1000 > 0 {
		answer += hundredToString(int(n/1000)) + "thousand "
	}
	n %= 1000
	answer += hundredToString(int(n))
	lastRune := []rune(answer)[len([]rune(answer))-1]
	if unicode.IsSpace(lastRune) {
		return string([]rune(answer)[:len([]rune(answer))-1])
	}
	return answer
}
