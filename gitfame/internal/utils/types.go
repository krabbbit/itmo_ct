package utils

import (
	"fmt"
	"os"
)

type Stats struct {
	Files   map[string]struct{}
	Commits map[string]struct{}
	Lines   int
}

type SimpleStats struct {
	Name    string `json:"name"`
	Lines   int    `json:"lines"`
	Commits int    `json:"commits"`
	Files   int    `json:"files"`
}

type SimpleStatsList []SimpleStats

func (s SimpleStatsList) Len() int {
	return len(s)
}

type ProgressBar struct {
	Current int
	Length  int
	Percent int
}

var order *string

func (s SimpleStatsList) Less(i, j int) bool {
	switch *order {
	case "lines":
		if s[i].Lines == s[j].Lines {
			if s[i].Commits == s[j].Commits {
				if s[i].Files == s[j].Files {
					return s[i].Name < s[j].Name
				} else {
					return s[i].Files > s[j].Files
				}
			} else {
				return s[i].Commits > s[j].Commits
			}
		} else {
			return s[i].Lines > s[j].Lines
		}
	case "commits":
		if s[i].Commits == s[j].Commits {
			if s[i].Lines == s[j].Lines {
				if s[i].Files == s[j].Files {
					return s[i].Name < s[j].Name
				} else {
					return s[i].Files > s[j].Files
				}
			} else {
				return s[i].Lines > s[j].Lines
			}
		} else {
			return s[i].Commits > s[j].Commits
		}
	case "files":
		if s[i].Files == s[j].Files {
			if s[i].Lines == s[j].Lines {
				if s[i].Commits == s[j].Commits {
					return s[i].Name < s[j].Name
				} else {
					return s[i].Commits > s[j].Commits
				}
			} else {
				return s[i].Lines > s[j].Lines
			}
		} else {
			return s[i].Files > s[j].Files
		}
	}
	_, err := fmt.Fprintf(os.Stderr, "Unknown sort type\n")
	if err != nil {
		return false
	}
	os.Exit(1)
	return false
}

func (s SimpleStatsList) Swap(i, j int) {
	tmp := s[i]
	s[i] = s[j]
	s[j] = tmp
}
