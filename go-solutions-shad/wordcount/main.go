package main

import (
	"fmt"
	"os"
	"strings"
)

func check(e error) {
	if e != nil {
		panic(e)
	}
}

func main() {
	files := os.Args[1:]
	// fmt.Println(files)
	mapa := make(map[string]int)
	for _, file := range files {
		data, err := os.ReadFile(file)
		check(err)
		lines := strings.Split(string(data), "\n")
		// fmt.Println(lines)
		for _, line := range lines {
			// if line != "" {
			mapa[line]++
			// }
		}
	}
	for k, v := range mapa {
		if v > 1 {
			fmt.Printf("%d\t%s\n", v, k)
		}
	}

}
