package stats

import (
	"fmt"
	"gitlab.com/slon/shad-go/gitfame/internal/utils"
	"os"
	"os/exec"
	"regexp"
	"strconv"
	"strings"
	"sync"
)

var muGrep sync.Mutex
var muBlame sync.Mutex

type TrackedWaitGroup struct {
	sync.WaitGroup
	count int64
	mu    sync.Mutex
}

func (wg *TrackedWaitGroup) Add(delta int) {
	wg.mu.Lock()
	wg.count += int64(delta)
	wg.mu.Unlock()
	wg.WaitGroup.Add(delta)
}

func (wg *TrackedWaitGroup) Done() {
	wg.mu.Lock()
	wg.count--
	wg.mu.Unlock()
	wg.WaitGroup.Done()
}

func (wg *TrackedWaitGroup) Count() int {
	wg.mu.Lock()
	defer wg.mu.Unlock()
	return int(wg.count)
}

func isCommitLine(line *string) bool {
	pattern := `^[a-f0-9]{40} \d+ \d+ \d+$`
	re := regexp.MustCompile(pattern)
	return re.MatchString(*line)
}

func grep(info *[]string, file string, person *map[string]utils.Stats, useCommiter *bool) {
	currentPerson := ""
	previousCommit := ""
	i := 0
	for i < len(*info) {
		line := (*info)[i]
		if isCommitLine(&line) {
			currentCommit := line[:40]
			currentCount, _ := strconv.Atoi(line[strings.LastIndex(line, " ")+1:])
			if currentCommit != previousCommit {
				if *useCommiter {
					currentPerson = (*info)[i+5][10:]
				} else {
					currentPerson = (*info)[i+1][7:]
				}
				muGrep.Lock()
				if entry, ok := (*person)[currentPerson]; ok {
					entry.Commits[currentCommit] = struct{}{}
					entry.Files[file] = struct{}{}
					(*person)[currentPerson] = entry
				} else {
					(*person)[currentPerson] = utils.Stats{Files: make(map[string]struct{}), Commits: make(map[string]struct{})}
					(*person)[currentPerson].Commits[currentCommit] = struct{}{}
					(*person)[currentPerson].Files[file] = struct{}{}
				}
				muGrep.Unlock()
				i += 8
				previousCommit = currentCommit
			}
			muGrep.Lock()
			if entry, ok := (*person)[currentPerson]; ok {
				entry.Lines += currentCount
				(*person)[currentPerson] = entry
			} else {
				(*person)[currentPerson] = utils.Stats{Files: make(map[string]struct{}), Commits: make(map[string]struct{}), Lines: currentCount}
			}
			muGrep.Unlock()
		}
		i++
	}
}

func Blame(revision *string, file string, repository *string, tracker *TrackedWaitGroup, person *map[string]utils.Stats, useCommiter *bool, lenFiles int) {
	defer tracker.Done()
	cmd := exec.Command("git", "blame", "--incremental", *revision, "--", file)
	cmd.Dir = *repository
	out, err := cmd.Output()
	if err == nil {
		info := strings.Split(string(out), "\n")
		if string(out) == "" {
			cmd1 := exec.Command("git", "log", "-1", *revision, "--pretty=%H%n%an%n%cn", "--", file)
			cmd1.Dir = *repository
			inf, _ := cmd1.Output()
			out1 := string(inf)
			currentPerson := strings.Split(out1, "\n")[1]
			if *useCommiter {
				currentPerson = strings.Split(out1, "\n")[2]
			}
			currentCommit := strings.Split(out1, "\n")[0]
			muBlame.Lock()
			if entry, ok := (*person)[currentPerson]; ok {
				entry.Commits[currentCommit] = struct{}{}
				entry.Files[file] = struct{}{}
				(*person)[currentPerson] = entry
			} else {
				(*person)[currentPerson] = utils.Stats{Files: make(map[string]struct{}), Commits: make(map[string]struct{})}
				(*person)[currentPerson].Commits[currentCommit] = struct{}{}
				(*person)[currentPerson].Files[file] = struct{}{}
			}
			muBlame.Unlock()
		} else {
			grep(&info, file, person, useCommiter)
		}
	}
	percent := (lenFiles - tracker.Count() + 1) * 100 / lenFiles
	_, _ = fmt.Fprintf(os.Stderr, "\rProcessed files: %d/%d (%d%%) [%s]",
		lenFiles-tracker.Count()+1,
		lenFiles,
		percent,
		utils.Progress(percent, 50),
	)
}
