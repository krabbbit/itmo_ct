//go:build !solution

package main

import (
	"fmt"
	"net/http"
	"os"
	"time"
)

func fetchall(url string, ch chan<- string, start time.Time) {
	resp, err := http.Get(url)
	if err != nil {
		ch <- fmt.Sprintf("error in url: %s", url)
		return
	}
	defer resp.Body.Close()
	seconds := time.Since(start).Seconds()
	ch <- fmt.Sprintf("%.2fs %s", seconds, url)
}

func main() {
	args := os.Args[1:]
	start := time.Now()
	ch := make(chan string)
	for _, arg := range args {
		go fetchall(arg, ch, start)
	}
	for range args {
		mes := <-ch
		fmt.Println(mes)
	}
	fmt.Printf("%.2fs elapsed", time.Since(start).Seconds())
}
