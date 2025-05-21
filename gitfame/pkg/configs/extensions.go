package configs

import (
	"embed"
	"encoding/json"
	"fmt"
	"os"
)

//go:embed language_extensions.json
var FileExtension embed.FS

type Language struct {
	Name       string   `json:"name"`
	Type       string   `json:"type"`
	Extensions []string `json:"extensions"`
}

func ParseExtensions() map[string]string {
	data, err := FileExtension.ReadFile("language_extensions.json")
	if err != nil {
		_, _ = fmt.Fprintf(os.Stderr, "error while read language_extensions.json")
		os.Exit(1)
	}
	var languages []Language
	if err := json.Unmarshal(data, &languages); err != nil {
		_, _ = fmt.Fprintf(os.Stderr, "error while parsing data in language_extensions.json")
		os.Exit(1)
	}
	extensions := make(map[string]string)
	for _, v := range languages {
		for _, ext := range v.Extensions {
			extensions[ext] = v.Name
		}
	}

	return extensions
}
