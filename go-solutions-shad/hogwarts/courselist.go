//go:build !solution

package hogwarts

func dfs(prereqs map[string][]string, v string, colors map[string]int, answer *[]string) {
	colors[v] = 1
	for _, neig := range prereqs[v] {
		if colors[neig] == 0 {
			dfs(prereqs, neig, colors, answer)
		} else if colors[neig] == 1 {
			panic("Cycle graph!")
		}
	}
	colors[v] = 2
	// _, exist := prereqs[v]
	// if exist {
	*answer = append(*answer, v)
	// }
}

func GetCourseList(prereqs map[string][]string) []string {
	colors := make(map[string]int)
	answer := make([]string, 0)
	for k := range prereqs {
		if colors[k] == 0 {
			dfs(prereqs, k, colors, &answer)
		}
	}
	// reverse(answer)
	return answer
}
