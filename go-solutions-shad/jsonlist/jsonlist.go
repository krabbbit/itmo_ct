//go:build !solution

package jsonlist

import (
	"bufio"
	"encoding/json"
	"io"
	"reflect"
)

func Marshal(w io.Writer, slice any) error {
	if reflect.ValueOf(slice).Kind() != reflect.Slice {
		return &json.UnsupportedTypeError{Type: reflect.TypeOf(slice)}
	}
	bw := bufio.NewWriter(w)
	defer bw.Flush()
	encoder := json.NewEncoder(bw)

	v := reflect.ValueOf(slice)

	for i := 0; i < v.Len(); i++ {
		elem := v.Index(i).Interface()
		err := encoder.Encode(elem)
		if err != nil {
			return err
		}
	}

	return nil
}

func Unmarshal(r io.Reader, slice any) error {
	if reflect.TypeOf(slice).Kind() != reflect.Ptr {
		return &json.UnsupportedTypeError{Type: reflect.TypeOf(slice)}
	}
	decoder := json.NewDecoder(r)
	v := reflect.ValueOf(slice).Elem()
	//var v := reflect.Type()
	var errorr error
	errorr = nil
	for {
		elem := reflect.New(v.Type().Elem()).Interface()
		err := decoder.Decode(elem)

		if err != nil {
			if err == io.EOF {
				break
			}
			errorr = err
			break
		}
		elemValue := reflect.ValueOf(elem).Elem()
		v.Set(reflect.Append(v, elemValue))
	}

	return errorr
}
