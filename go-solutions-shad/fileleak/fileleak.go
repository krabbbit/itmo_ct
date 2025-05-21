//go:build !solution

package fileleak

import (
	"io/fs"
	"os"
	"reflect"
)

type testingT interface {
	Errorf(msg string, args ...interface{})
	Cleanup(func())
}

func getFiles(pairs []fs.DirEntry) []fs.FileInfo {
	var files []fs.FileInfo
	for _, pair := range pairs {
		data, err := pair.Info()
		if err == nil {
			files = append(files, data)
		}
	}
	return files
}

func VerifyNone(t testingT) {
	pairs, err := os.ReadDir("/proc/self/fd")
	if err != nil {
		t.Errorf("error while readDir")
		os.Exit(1)
	}
	files := getFiles(pairs)
	f := func() {
		pairs, err := os.ReadDir("/proc/self/fd")
		if err != nil {
			t.Errorf("error while readDir")
			os.Exit(1)
		}
		files2 := getFiles(pairs)
		if !reflect.DeepEqual(files, files2) {
			t.Errorf("file leak!")
		}
	}
	t.Cleanup(f)
}
