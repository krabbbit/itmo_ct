package utils

import (
	"encoding/csv"
	"encoding/json"
	"fmt"
	"os"
	"sort"
	"strconv"
	"strings"
	"text/tabwriter"
)

func Printer(format string, simplePerson map[string]SimpleStats) {
	fmt.Println()
	tmp := SimpleStatsList{}
	for _, v := range simplePerson {
		tmp = append(tmp, v)
	}
	sort.Sort(tmp)
	switch format {
	case "tabular":
		writer := tabwriter.NewWriter(os.Stdout, 0, 0, 1, ' ', tabwriter.TabIndent)
		defer func(writer *tabwriter.Writer) {
			_ = writer.Flush()
		}(writer)
		_, _ = fmt.Fprintf(writer, "Name\tLines\tCommits\tFiles\n")
		for _, v := range tmp {
			_, _ = fmt.Fprintf(writer, "%s\t%d\t%d\t%d\n", v.Name, v.Lines, v.Commits, v.Files)
		}
	case "csv":
		writer := csv.NewWriter(os.Stdout)
		defer writer.Flush()
		head := []string{"Name", "Lines", "Commits", "Files"}
		_ = writer.Write(head)
		for _, v := range tmp {
			if err := writer.Write([]string{v.Name, strconv.Itoa(v.Lines), strconv.Itoa(v.Commits), strconv.Itoa(v.Files)}); err != nil {
				_, _ = fmt.Fprintf(os.Stderr, "error while write csv\n")
				os.Exit(1)
			}
		}
	case "json":
		info, _ := json.Marshal(tmp)
		fmt.Print(string(info))
	case "json-lines":
		for _, v := range tmp {
			info, _ := json.Marshal(v)
			fmt.Printf("%s\n", string(info))
		}
	}

}

func InitOrder(s *string) {
	order = s
}

func Progress(percent, width int) string {
	filled := percent * width / 100
	empty := width - filled
	return fmt.Sprintf("%s%s",
		strings.Repeat("+", filled),
		strings.Repeat("_", empty),
	)
}
