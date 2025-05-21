//go:build !solution

package hotelbusiness

import (
	"sort"
)

type Guest struct {
	CheckInDate  int
	CheckOutDate int
}

type Load struct {
	StartDate  int
	GuestCount int
}

type Guests []Guest

func (s Guests) Len() int {
	return len(s)
}

func (s Guests) Swap(i, j int) {
	s[i], s[j] = s[j], s[i]
}

func (s Guests) Less(i, j int) bool {
	return s[i].CheckInDate < s[j].CheckInDate
}

func ComputeLoad(guests []Guest) []Load {
	tmp := make(map[int]int)
	for _, guest := range guests {
		tmp[guest.CheckInDate]++
		tmp[guest.CheckOutDate]--
	}
	keys := make([]int, 0, len(tmp))
	for k := range tmp {
		keys = append(keys, k)
	}
	sort.Ints(keys)

	var result []Load
	currentGuests := 0
	prevDate := -1

	for _, date := range keys {
		if prevDate != -1 && date > prevDate && tmp[prevDate] != 0 {
			result = append(result, Load{prevDate, currentGuests})
		}

		currentGuests += tmp[date]
		prevDate = date
	}

	if prevDate != -1 {
		result = append(result, Load{prevDate, currentGuests})
	}

	return result
}

// func main() {
// 	guests := []Guest{{4, 5}, {2, 1}, {1, 10}, {5, 5}}
// 	sort.Sort(Guests(guests))
// 	fmt.Print(guests)
// }
