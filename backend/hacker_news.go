package hackernews

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"

	"appengine"

	"github.com/gorilla/mux"
)

func init() {
	router := mux.NewRouter()

	var tasks Tasks

	router.HandleFunc("/top", getTopStories)
	router.HandleFunc("/story/{story:[0-9]+}", getStory)
	router.HandleFunc("/story/{story:[0-9]+}/comments", getCommentsForStory)
	router.HandleFunc("/comment/{comment:[0-9]+}", getComment)
	router.HandleFunc("/comment/{comment:[0-9]+}/comments", getCommentsForComment)
	router.HandleFunc("/tasks/top_stories", tasks.getTopStories)

	http.Handle("/", router)
}

func getStory(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	storyId, _ := strconv.Atoi(vars["story"])

	c := appengine.NewContext(r)

	datastore := NewDb(c)
	client := NewClient(c)

	story, err := datastore.GetStory(storyId)
	if err != nil {
		story, err = client.GetStory(storyId)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		datastore.SaveStory(story)
	}

	jsonData, err := json.Marshal(story)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	fmt.Fprintf(w, string(jsonData))
}

func getComment(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	commentId, _ := strconv.Atoi(vars["comment"])

	c := appengine.NewContext(r)

	datastore := NewDb(c)
	client := NewClient(c)

	comment, err := datastore.GetComment(commentId)
	if err != nil {
		c.Debugf("No comment in data store!")
		comment, err = client.GetComment(commentId)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		c.Debugf("Saving %v", comment)
		if err = datastore.SaveComment(comment); err != nil {
			c.Errorf("Error saving comment to data store: %v", err)
		}
	}

	jsonData, err := json.Marshal(comment)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	fmt.Fprintf(w, string(jsonData))
}

func getCommentsForStory(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	storyId, _ := strconv.Atoi(vars["story"])

	c := appengine.NewContext(r)

	datastore := NewDb(c)

	story, err := datastore.GetStory(storyId)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	commentIds := story.Kids
	if len(commentIds) == 0 {
		fmt.Fprintf(w, "[]")
		return
	}

	jsonData, err := json.Marshal(commentIds)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	fmt.Fprintf(w, string(jsonData))
}

func getCommentsForComment(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	commentId, _ := strconv.Atoi(vars["comment"])

	c := appengine.NewContext(r)

	datastore := NewDb(c)

	comment, err := datastore.GetComment(commentId)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	commentIds := comment.Kids
	if len(commentIds) == 0 {
		fmt.Fprintf(w, "[]")
		return
	}

	jsonData, err := json.Marshal(commentIds)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	fmt.Fprintf(w, string(jsonData))
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
