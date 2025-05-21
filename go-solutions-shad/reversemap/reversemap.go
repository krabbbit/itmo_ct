//go:build !solution

package reversemap

import (
	"reflect"
)

func ReverseMap(forward any) any {
	v := reflect.ValueOf(forward)

	if v.Kind() != reflect.Map {
		panic("this is not map")
	}
	t := v.Type()

	reverse := reflect.MakeMap(reflect.MapOf(t.Elem(), t.Key()))

	for _, key := range v.MapKeys() {
		reverse.SetMapIndex(v.MapIndex(key), key)
	}
	return reverse.Interface()
}
