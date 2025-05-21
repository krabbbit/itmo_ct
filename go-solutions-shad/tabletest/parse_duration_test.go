package tabletest

import (
	// "fmt"
	"math/rand"
	"testing"
	"time"
)

var parseDurationTests = []struct {
	input    string
	duration time.Duration
	ok       bool
}{
	{"0s", 0, true},
	{"5s", 5 * time.Second, true},
	{"42s", 42 * time.Second, true},
	{"1234s", 1234 * time.Second, true},
	{"-7s", -7 * time.Second, true},
	{"+8s", 8 * time.Second, true},
	{"-0s", 0, true},
	{"+0s", 0, true},
	{"3.5s", 3*time.Second + 500*time.Millisecond, true},
	{"15.75s", 15*time.Second + 750*time.Millisecond, true},
	{"7.s", 7 * time.Second, true},
	{".9s", 900 * time.Millisecond, true},
	{"2.000s", 2 * time.Second, true},
	{"1.001s", 1*time.Second + 1*time.Millisecond, true},
	{"100.010s", 100*time.Second + 10*time.Millisecond, true},
	{"20ns", 20 * time.Nanosecond, true},
	{"6us", 6 * time.Microsecond, true},
	{"9µs", 9 * time.Microsecond, true},
	{"13μs", 13 * time.Microsecond, true},
	{"30ms", 30 * time.Millisecond, true},
	{"8s", 8 * time.Second, true},
	{"45m", 45 * time.Minute, true},
	{"20h", 20 * time.Hour, true},
	{"4h15m", 4*time.Hour + 15*time.Minute, true},
	{"12.5s6m", 6*time.Minute + 12*time.Second + 500*time.Millisecond, true},
	{"-3m4.5s", -(3*time.Minute + 4*time.Second + 500*time.Millisecond), true},
	{"2h3m4s5ms6us7ns", 2*time.Hour + 3*time.Minute + 4*time.Second + 5*time.Millisecond + 6*time.Microsecond + 7*time.Nanosecond, true},
	{"48h10m15.500s", 48*time.Hour + 10*time.Minute + 15*time.Second + 500*time.Millisecond, true},
	{"123456789000ns", 123456789000 * time.Nanosecond, true},
	{"0.75h", 45 * time.Minute, true},
	{"9007199254740992ns", (1 << 53) * time.Nanosecond, true},
	{"9223372036854775806ns", (1<<63 - 2) * time.Nanosecond, true},
	{"9223372036854775.806us", (1<<63 - 2) * time.Nanosecond, true},
	{"9223372036s854ms775us806ns", (1<<63 - 2) * time.Nanosecond, true},
	{"-9223372036854775806ns", -1<<63 + 2*time.Nanosecond, true},
	{"0.20000000000000000h", 12 * time.Minute, true},
	{"0.3333333333333333333h", 20 * time.Minute, true},
	{"0.100000000000000000000h", 6 * time.Minute, true},

	{"0.9223372036854775807h", 55*time.Minute + 20*time.Second + 413933267*time.Nanosecond, true},
	{"1234567890123ns", 1234567890123 * time.Nanosecond, true},
	{"", 0, false},
	{"123456", 0, false},
	{"+", 0, false},
	{"mmm", 0, false},
	{"..", 0, false},
	{"-..", 0, false},
	{"..s", 0, false},
	{"+.sxyz", 0, false},
	{"4000000h", 0, false},
	{"999999999999999999999999999999999ns", 0, false},
	{"999999999999999999999999999999999.999us", 0, false},
	{"999999999999999999999999999999999h999m999s999ms999us999ns", 0, false},
	{"-999999999999999999999999999999999ns", 0, false},
	{"-9223372036854775809ns", 0, false},
	{"invalidinput", 0, false},
	{"0h", 0, true},
	{"0m", 0, true},
	{"0s", 0, true},
	{"0.000001s", 1 * time.Microsecond, true},
	{"0.000000001s", 1 * time.Nanosecond, true},
	{"1.000000001s", 1*time.Second + 1*time.Nanosecond, true},
	{"1.999999999s", 1*time.Second + 999999999*time.Nanosecond, true},
	{"10m10", 0, false},
	{"10m10x", 0, false},
	{"10m10s10", 0, false},
	{"1.5h", 90 * time.Minute, true},
	{"1.5m", 90 * time.Second, true},
	{"1.5s", 1500 * time.Millisecond, true},
	{"1.5ms", 1500 * time.Microsecond, true},
	{"1.5us", 1500 * time.Nanosecond, true},
	{"1.5ns", 1 * time.Nanosecond, true},
	{"1.5xs", 0, false},
	{"1.5", 0, false},
	{"3000000h", 0, false},
	{"4238947902435442430543ns", 0, false},
	{"9223372036854ms1000μs1000ns", 0, false},
}

func TestParseDuration(t *testing.T) {
	for _, tt := range parseDurationTests {
		// fmt.Print(tt)
		res, err := ParseDuration(tt.input)
		if tt.ok && (err != nil || res != tt.duration) {
			t.Errorf("ParseDuration(%q): got (%v, %v), want (%v, nil)", tt.input, res, err, tt.duration)
		} else if !tt.ok && err == nil {
			t.Errorf("ParseDuration(%q): got (_, nil), want (_, non-nil)", tt.input)
		}
	}
}

func TestParseDurationRoundTrip(t *testing.T) {
	for i := 0; i < 100; i++ {
		a := time.Duration(rand.Int31()) * time.Millisecond
		s := a.String()
		// fmt.Print(s)
		b, err := ParseDuration(s)
		if err != nil || a != b {
			t.Errorf("round-trip failed: %d => %q => %d, %v", a, s, b, err)
		}
	}
}
