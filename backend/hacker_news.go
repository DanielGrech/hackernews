package hackernews

import (
	"encoding/json"
	"fmt"
	"net/http"

	"appengine"
)

func init() {
	var tasks Tasks

	http.HandleFunc("/top", getTopStories)
	http.HandleFunc("/tasks/top_stories", tasks.getTopStories)
}

func getTopStories(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)

	client := NewClient(c)
	datastore := NewDb(c)
	cache := NewCache(c)

	storyIds, err := getTopStoryIds(c, cache, client)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	stories, err := datastore.GetStories(storyIds)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	jsonData, err := json.Marshal(stories)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	fmt.Fprintf(w, string(jsonData))
}

func getTopStoryIds(context appengine.Context, cache *Cache, apiClient *HnApiClient) ([]int, error) {
	topStories := cache.GetTopStories()
	if topStories != nil {
		return topStories, nil
	}

	topStories, err := apiClient.GetTopStories()
	if err == nil {
		cache.SetTopStories(topStories)
	}

	return topStories, err
}
