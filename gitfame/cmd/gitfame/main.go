//go:build !solution

package main

import (
	"fmt"
	"gitlab.com/slon/shad-go/gitfame/internal/stats"
	"gitlab.com/slon/shad-go/gitfame/internal/utils"
	"gitlab.com/slon/shad-go/gitfame/pkg/configs"
	"os"
	"os/exec"
	"path/filepath"
	"slices"
	"strings"

	"github.com/spf13/pflag"
)

func main() {
	person := make(map[string]utils.Stats)
	simplePerson := make(map[string]utils.SimpleStats)
	langExt := configs.ParseExtensions()

	repository := pflag.String("repository", ".", "path to Git repository; default is current directory")
	revision := pflag.String("revision", "HEAD", "commit pointer; HEAD by default")
	order := pflag.String("order-by", "lines", "result sort key; one of lines (default), commits, files")
	useCommiter := pflag.Bool("use-committer", false, "boolean flag that replaces the author (default) with the committer in calculations")
	format := pflag.String("format", "tabular", "output format; one of tabular (default), csv, json, json-lines")
	extensions := pflag.StringSlice("extensions", nil, "list of extensions that narrows the list of files in the calculation")
	languages := pflag.StringSlice("languages", nil, "a list of languages that narrows the list of files in the calculation")
	exclude := pflag.StringSlice("exclude", nil, "a set of Glob patterns that exclude files from the calculation, for example 'foo/*,bar/*'")
	restrict := pflag.StringSlice("restrict-to", nil, "a set of Glob patterns that excludes all files that do not match any of the patterns in the set")
	pflag.Parse()
	utils.InitOrder(order)
	if !slices.Contains([]string{"tabular", "json", "json-lines", "csv"}, *format) {
		_, _ = fmt.Fprintf(os.Stderr, "Unknown format type\n")
		os.Exit(1)
	}
	cmd := exec.Command("git", "ls-tree", "-r", "--name-only", *revision)
	cmd.Dir = *repository
	out, err := cmd.Output()
	if err != nil {
		_, _ = fmt.Fprintf(os.Stderr, "error while get repository's tree\n")
		os.Exit(1)
	}
	tmp := strings.Split(string(out), "\n")
	var files []string

	for _, v := range tmp {
		lastDot := strings.LastIndex(v, ".")
		var ext string
		if lastDot == -1 {
			ext = ""
		} else {
			ext = v[lastDot:]
		}

		counter := 0
		if *extensions == nil || slices.Contains(*extensions, ext) {
			counter++
		}
		if lang, ok := langExt[ext]; ok {
			if *languages == nil || slices.Contains(*languages, strings.ToLower(lang)) {
				counter++
			}
		} else if *languages == nil {
			counter++
		}
		flag := true
		for _, pattern := range *exclude {
			match, _ := filepath.Match(pattern, v)
			if match {
				flag = false
			}
		}
		if flag {
			counter++
		}
		for _, pattern := range *restrict {
			match, _ := filepath.Match(pattern, v)
			if match {
				counter++
				break
			}
		}
		if *restrict == nil {
			counter++
		}
		if counter >= 4 {
			files = append(files, v)
		}
	}

	tracker := &stats.TrackedWaitGroup{}
	_, _ = fmt.Fprintf(os.Stdout, "\rОбработано файлов: %d/%d (%d%%) [%s]",
		0,
		len(files),
		0,
		utils.Progress(0, 50),
	)
	for _, file := range files {
		if file == "" {
			continue
		}
		tracker.Add(1)
		go stats.Blame(revision, file, repository, tracker, &person, useCommiter, len(files))
	}
	tracker.Wait()

	for k, v := range person {
		simplePerson[k] = utils.SimpleStats{Name: k, Lines: v.Lines, Commits: len(v.Commits), Files: len(v.Files)}
	}

	utils.Printer(*format, simplePerson)
}
