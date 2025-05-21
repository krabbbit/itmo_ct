//go:build !solution

package main

import (
	"encoding/json"
	"flag"
	"fmt"
	"io"
	"log"
	"net/http"
	"os"
	"sort"
	"strconv"
	"strings"
)

type MedalCount struct {
	Gold   int `json:"gold"`
	Silver int `json:"silver"`
	Bronze int `json:"bronze"`
	Total  int `json:"total"`
}

type AthleteInfo struct {
	Athlete      string                `json:"athlete"`
	Country      string                `json:"country"`
	Medals       MedalCount            `json:"medals"`
	MedalsByYear map[string]MedalCount `json:"medals_by_year"`
}

type Winner struct {
	Athlete string `json:"athlete"`
	Age     int    `json:"age"`
	Country string `json:"country"`
	Year    int    `json:"year"`
	Date    string `json:"date"`
	Sport   string `json:"sport"`
	Gold    int    `json:"gold"`
	Silver  int    `json:"silver"`
	Bronze  int    `json:"bronze"`
	Total   int    `json:"total"`
}

var winners []Winner

func main() {
	port := flag.Int("port", 8080, "port to listen on")
	dataFile := flag.String("data", "", "path to olympicWinners.json")
	flag.Parse()

	if *dataFile == "" {
		log.Fatal("data file parameter is required")
	}

	loadData(*dataFile)

	http.HandleFunc("/athlete-info", athleteInfoHandler)
	http.HandleFunc("/top-athletes-in-sport", topAthletesHandler)
	http.HandleFunc("/top-countries-in-year", topCountriesHandler)

	addr := fmt.Sprintf(":%d", *port)
	log.Printf("Starting server on %s", addr)
	log.Fatal(http.ListenAndServe(addr, nil))
}

func loadData(path string) {
	file, err := os.Open(path)
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()

	data, err := io.ReadAll(file)
	if err != nil {
		log.Fatal(err)
	}

	if err := json.Unmarshal(data, &winners); err != nil {
		log.Fatal(err)
	}
}

func athleteInfoHandler(w http.ResponseWriter, r *http.Request) {
	name := r.URL.Query().Get("name")
	if name == "" {
		http.Error(w, "name parameter is required", http.StatusBadRequest)
		return
	}

	var athleteWins []Winner
	countries := make(map[string]bool)
	medalsByYear := make(map[string]MedalCount)
	total := MedalCount{}

	for _, winner := range winners {
		if winner.Athlete == name {
			athleteWins = append(athleteWins, winner)
			countries[winner.Country] = true

			yearStr := strconv.Itoa(winner.Year)
			if _, exists := medalsByYear[yearStr]; !exists {
				medalsByYear[yearStr] = MedalCount{}
			}

			yearMedals := medalsByYear[yearStr]
			yearMedals.Gold += winner.Gold
			yearMedals.Silver += winner.Silver
			yearMedals.Bronze += winner.Bronze
			yearMedals.Total += winner.Total
			medalsByYear[yearStr] = yearMedals

			total.Gold += winner.Gold
			total.Silver += winner.Silver
			total.Bronze += winner.Bronze
			total.Total += winner.Total
		}
	}

	if len(athleteWins) == 0 {
		http.Error(w, fmt.Sprintf("athlete %s not found", name), http.StatusNotFound)
		return
	}

	country := athleteWins[0].Country
	response := AthleteInfo{
		Athlete:      name,
		Country:      country,
		Medals:       total,
		MedalsByYear: medalsByYear,
	}

	w.Header().Set("Content-Type", "application/json")
	_ = json.NewEncoder(w).Encode(response)
}

func topAthletesHandler(w http.ResponseWriter, r *http.Request) {
	sport := r.URL.Query().Get("sport")
	if sport == "" {
		http.Error(w, "sport parameter is required", http.StatusBadRequest)
		return
	}

	limitStr := r.URL.Query().Get("limit")
	limit := 3
	if limitStr != "" {
		var err error
		limit, err = strconv.Atoi(limitStr)
		if err != nil {
			http.Error(w, "invalid limit parameter", http.StatusBadRequest)
			return
		}
	}

	athleteStats := make(map[string]*AthleteInfo)

	for _, winner := range winners {
		if strings.EqualFold(winner.Sport, sport) {
			if _, exists := athleteStats[winner.Athlete]; !exists {
				athleteStats[winner.Athlete] = &AthleteInfo{
					Athlete:      winner.Athlete,
					Country:      winner.Country,
					MedalsByYear: make(map[string]MedalCount),
				}
			}

			stats := athleteStats[winner.Athlete]
			stats.Medals.Gold += winner.Gold
			stats.Medals.Silver += winner.Silver
			stats.Medals.Bronze += winner.Bronze
			stats.Medals.Total += winner.Total

			yearStr := strconv.Itoa(winner.Year)
			if _, exists := stats.MedalsByYear[yearStr]; !exists {
				stats.MedalsByYear[yearStr] = MedalCount{}
			}

			yearMedals := stats.MedalsByYear[yearStr]
			yearMedals.Gold += winner.Gold
			yearMedals.Silver += winner.Silver
			yearMedals.Bronze += winner.Bronze
			yearMedals.Total += winner.Total
			stats.MedalsByYear[yearStr] = yearMedals
		}
	}

	if len(athleteStats) == 0 {
		http.Error(w, fmt.Sprintf("sport '%s' not found", sport), http.StatusNotFound)
		return
	}

	athletes := make([]*AthleteInfo, 0, len(athleteStats))
	for _, athlete := range athleteStats {
		athletes = append(athletes, athlete)
	}

	sort.Slice(athletes, func(i, j int) bool {
		if athletes[i].Medals.Gold != athletes[j].Medals.Gold {
			return athletes[i].Medals.Gold > athletes[j].Medals.Gold
		}
		if athletes[i].Medals.Silver != athletes[j].Medals.Silver {
			return athletes[i].Medals.Silver > athletes[j].Medals.Silver
		}
		if athletes[i].Medals.Bronze != athletes[j].Medals.Bronze {
			return athletes[i].Medals.Bronze > athletes[j].Medals.Bronze
		}
		return athletes[i].Athlete < athletes[j].Athlete
	})

	if limit > len(athletes) {
		limit = len(athletes)
	}

	w.Header().Set("Content-Type", "application/json")
	_ = json.NewEncoder(w).Encode(athletes[:limit])
}

func topCountriesHandler(w http.ResponseWriter, r *http.Request) {
	yearStr := r.URL.Query().Get("year")
	if yearStr == "" {
		http.Error(w, "year parameter is required", http.StatusBadRequest)
		return
	}

	year, err := strconv.Atoi(yearStr)
	if err != nil {
		http.Error(w, "invalid year parameter", http.StatusBadRequest)
		return
	}

	limitStr := r.URL.Query().Get("limit")
	limit := 3
	if limitStr != "" {
		limit, err = strconv.Atoi(limitStr)
		if err != nil {
			http.Error(w, "invalid limit parameter", http.StatusBadRequest)
			return
		}
	}

	countryStats := make(map[string]MedalCount)
	found := false

	for _, winner := range winners {
		if winner.Year == year {
			found = true
			stats := countryStats[winner.Country]
			stats.Gold += winner.Gold
			stats.Silver += winner.Silver
			stats.Bronze += winner.Bronze
			stats.Total += winner.Total
			countryStats[winner.Country] = stats
		}
	}

	if !found {
		http.Error(w, fmt.Sprintf("year %d not found", year), http.StatusNotFound)
		return
	}

	type CountryResult struct {
		Country string `json:"country"`
		Gold    int    `json:"gold"`
		Silver  int    `json:"silver"`
		Bronze  int    `json:"bronze"`
		Total   int    `json:"total"`
	}

	results := make([]CountryResult, 0, len(countryStats))
	for country, stats := range countryStats {
		results = append(results, CountryResult{
			Country: country,
			Gold:    stats.Gold,
			Silver:  stats.Silver,
			Bronze:  stats.Bronze,
			Total:   stats.Total,
		})
	}

	sort.Slice(results, func(i, j int) bool {
		if results[i].Gold != results[j].Gold {
			return results[i].Gold > results[j].Gold
		}
		if results[i].Silver != results[j].Silver {
			return results[i].Silver > results[j].Silver
		}
		if results[i].Bronze != results[j].Bronze {
			return results[i].Bronze > results[j].Bronze
		}
		return results[i].Country < results[j].Country
	})

	if limit > len(results) {
		limit = len(results)
	}

	w.Header().Set("Content-Type", "application/json")
	_ = json.NewEncoder(w).Encode(results[:limit])
}
