//go:build !solution

package testequal

import (
	"bytes"
	"fmt"
	"reflect"
)

func AssertHelper(neg bool, t T, expected, actual interface{}, msgAndArgs ...interface{}) bool {
	t.Helper()
	equal := false
	if expected == nil || actual == nil {
		equal = (expected == actual)
	} else {
		switch value := expected.(type) {
		case int, uint, int8, uint8, int16, uint16, int64, uint64, bool, string:
			equal = (value == actual)
		case map[string]string:
			mapa, ok := actual.(map[string]string)
			if ok && (len(mapa) == len(value)) && len(mapa) != 0 {
				equal = true
				for k, v := range value {
					v2, ok := mapa[k]
					if !ok || v2 != v {
						equal = false
						break
					}
				}
			}
		case struct{}:
			equal = false
		default:
			expByte, ok1 := expected.([]byte)
			actByte, ok2 := actual.([]byte)
			if ok1 && ok2 {
				if expByte == nil || actByte == nil {
					equal = (expByte == nil && actByte == nil)
				} else {
					equal = bytes.Equal(expByte, actByte)
				}
			} else {
				equal = reflect.DeepEqual(expected, actual)
			}
		}
	}
	if (!neg && !equal) || (neg && equal) {
		f :=
			`
			expected: %v
			actual  : %v
			message : %v`
		mes := ``
		switch len(msgAndArgs) {
		case 0:
			break
		case 1:
			mes = msgAndArgs[0].(string)
		default:
			mes = fmt.Sprintf(msgAndArgs[0].(string), msgAndArgs[1:]...)
		}
		t.Errorf(f, expected, actual, mes)
	}
	return equal
}

func AssertEqual(t T, expected, actual interface{}, msgAndArgs ...interface{}) bool {
	t.Helper()
	equal := AssertHelper(false, t, expected, actual, msgAndArgs...)
	return equal
}

func AssertNotEqual(t T, expected, actual interface{}, msgAndArgs ...interface{}) bool {
	t.Helper()
	equal := AssertHelper(true, t, expected, actual, msgAndArgs...)
	return !equal
}

func RequireEqual(t T, expected, actual interface{}, msgAndArgs ...interface{}) {
	t.Helper()
	equal := AssertHelper(false, t, expected, actual, msgAndArgs...)
	if !equal {
		t.FailNow()
	}
}

func RequireNotEqual(t T, expected, actual interface{}, msgAndArgs ...interface{}) {
	t.Helper()
	equal := AssertHelper(true, t, expected, actual, msgAndArgs...)
	if equal {
		t.FailNow()
	}
}
