//go:build !solution

package main

import (
	"encoding/json"
	"flag"
	"io"
	"log"
	"math/rand"
	"net/http"
	"strconv"
	"strings"
	"sync"
)

type response struct {
	URL string `json:"url"`
	Key string `json:"key"`
}

type request struct {
	URL string `json:"url"`
}

var (
	mu sync.Mutex
	db = make(map[string]string)
)

func main() {
	http.HandleFunc("/shorten", handlerPost)
	http.HandleFunc("/go/", handlerGet)
	port := flag.Int("port", 8080, "number of port for server")
	flag.Parse()
	log.Fatal(http.ListenAndServe(":"+strconv.Itoa(*port), nil))
}

func handlerGet(w http.ResponseWriter, r *http.Request) {
	parts := strings.Split(r.URL.Path, "/")
	if len(parts) < 3 {
		http.Error(w, "unexpected error", http.StatusNotFound)
		return
	}
	key := parts[2]
	mu.Lock()
	redirect, ok := db[key]
	mu.Unlock()
	if !ok {
		http.Error(w, "key not found", http.StatusNotFound)
		return
	}
	w.Header().Set("Location", redirect)
	http.Redirect(w, r, redirect, http.StatusFound)
}

func handlerPost(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "method not allowed", http.StatusMethodNotAllowed)
		return
	}

	byteArray, err := io.ReadAll(r.Body)
	if err != nil {
		http.Error(w, "invalid request", http.StatusBadRequest)
		return
	}

	var req request
	err = json.Unmarshal(byteArray, &req)
	if err != nil {
		http.Error(w, "invalid request", http.StatusBadRequest)
		return
	}

	key := strconv.FormatInt(rand.Int63(), 16)
	mu.Lock()
	for k, v := range db {
		if v == req.URL {
			key = k
			break
		}
	}
	db[key] = req.URL
	mu.Unlock()

	resp := response{URL: req.URL, Key: key}
	jsonResponse, err := json.Marshal(resp)
	if err != nil {
		http.Error(w, "internal server error", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	_, _ = w.Write(jsonResponse)
}
