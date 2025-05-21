package main

import (
	"bufio"
	"context"
	"flag"
	"fmt"
	"log"
	"net/url"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/gorilla/websocket"
)

var addr string

func main() {
	flag.StringVar(&addr, "addr", "ws://localhost:8080", "адрес websocket сервера")
	flag.Parse()

	u, err := url.Parse(addr)
	if err != nil {
		log.Fatalf("Invalid URL: %v", err)
	}

	conn, _, err := websocket.DefaultDialer.Dial(u.String(), nil)
	if err != nil {
		log.Fatalf("Dial error: %v", err)
	}
	defer func(conn *websocket.Conn) {
		err := conn.Close()
		if err != nil {
			log.Fatalf("Error closing websocket connection: %v", err)
		}
	}(conn)

	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	stop := make(chan os.Signal, 1)
	signal.Notify(stop, syscall.SIGINT, syscall.SIGTERM)
	done := make(chan struct{})

	go func() {
		defer close(done)
		for {
			select {
			case <-ctx.Done():
				return
			default:
				_, message, err := conn.ReadMessage()
				if err != nil {
					if websocket.IsUnexpectedCloseError(err, websocket.CloseGoingAway, websocket.CloseNormalClosure) {
						log.Printf("Read error: %v", err)
					}
					return
				}
				fmt.Print(string(message))
			}
		}
	}()

	go func() {
		scanner := bufio.NewScanner(os.Stdin)
		for scanner.Scan() {
			select {
			case <-ctx.Done():
				return
			default:
				text := scanner.Bytes()
				err := conn.WriteMessage(websocket.TextMessage, text)
				if err != nil {
					log.Printf("Write error: %v", err)
					return
				}
			}
		}
	}()

	select {
	case <-stop:
		err := conn.WriteMessage(websocket.CloseMessage,
			websocket.FormatCloseMessage(websocket.CloseNormalClosure, ""))
		if err != nil {
			log.Printf("Error sending close message: %v", err)
		}
		cancel()
	case <-time.After(time.Second):
		log.Println("Timeout waiting for graceful shutdown")
	case <-done:
	}

	//select {
	//case <-done:
	//case <-time.After(time.Second):
	//	log.Println("Timeout waiting for graceful shutdown")
	//}

	os.Exit(0)
}
