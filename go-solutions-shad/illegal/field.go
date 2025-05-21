//go:build !solution

package illegal

import (
	"reflect"
	"unsafe"
)

func SetPrivateField(obj any, name string, value any) {
	pt := reflect.ValueOf(obj).Elem().FieldByName(name)
	up := unsafe.Pointer(pt.UnsafeAddr())

	reflect.NewAt(pt.Type(), up).Elem().Set(reflect.ValueOf(value))
}
