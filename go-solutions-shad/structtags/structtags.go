//go:build !solution

package structtags

import (
	"fmt"
	"net/http"
	"reflect"
	"strconv"
	"strings"
	"sync"
)

var fieldCache sync.Map

func Unpack(req *http.Request, ptr interface{}) error {
	if err := req.ParseForm(); err != nil {
		return err
	}

	v := reflect.ValueOf(ptr).Elem()
	t := v.Type()

	cached, ok := fieldCache.Load(t)
	if !ok {
		fields := make(map[string]int)
		for i := 0; i < t.NumField(); i++ {
			field := t.Field(i)
			name := field.Tag.Get("http")
			if name == "" {
				name = strings.ToLower(field.Name)
			}
			fields[name] = i
		}
		cached, _ = fieldCache.LoadOrStore(t, fields)
	}

	fields := cached.(map[string]int)

	for name, values := range req.Form {
		if idx, ok := fields[name]; ok {
			field := v.Field(idx)

			if field.Kind() == reflect.Slice {
				elemType := field.Type().Elem()
				slice := reflect.MakeSlice(field.Type(), 0, len(values))
				for _, value := range values {
					elem := reflect.New(elemType).Elem()
					if err := populate(elem, value); err != nil {
						return fmt.Errorf("%s: %v", name, err)
					}
					slice = reflect.Append(slice, elem)
				}
				field.Set(slice)
			} else if len(values) > 0 {
				if err := populate(field, values[len(values)-1]); err != nil {
					return fmt.Errorf("%s: %v", name, err)
				}
			}
		}
	}
	return nil
}

func populate(v reflect.Value, value string) error {
	switch v.Kind() {
	case reflect.String:
		v.SetString(value)
	case reflect.Int:
		i, err := strconv.ParseInt(value, 10, 64)
		if err != nil {
			return err
		}
		v.SetInt(i)
	case reflect.Bool:
		b, err := strconv.ParseBool(value)
		if err != nil {
			return err
		}
		v.SetBool(b)
	default:
		return fmt.Errorf("unsupport %s", v.Type())
	}
	return nil
}
