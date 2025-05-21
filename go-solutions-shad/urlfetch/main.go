//go:build !solution

package main

import (
	"fmt"
	"io"
	"net/http"
	"os"
)

func check(err error) {
	if err != nil {
		panic(err)
	}
}

func main() {
	//resp, err := http.Get("http://example.com/")
	args := os.Args[1:]
	for _, arg := range args {
		resp, err := http.Get(arg)
		check(err)
		data, err := io.ReadAll(resp.Body)
		check(err)
		defer resp.Body.Close()
		fmt.Printf("%s\n", data)
	}
}
