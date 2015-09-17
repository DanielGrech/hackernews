package hackernews

import (
	"net/http"

	"appengine"
)

type Tasks struct{}

func (tasks *Tasks) getTopStories(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)

	client := NewClient(c)
	datastore := NewDb(c)
	cache := NewCache(c)

	topStories, err := cacheTopStoryIds(c, cache, client)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	for _, id := range topStories {
		_, err := fetchStoryIfNeeded(id, c, cache, datastore, client)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
		}
	}
}

func cacheTopStoryIds(context appengine.Context, cache *Cache, apiClient *HnApiClient) ([]int, error) {
	topStories, err := apiClient.GetTopStories()
	if err != nil {
		return topStories, err
	}

	context.Debugf("Got stories from network: %v", topStories)

	cache.SetTopStories(topStories)

	return topStories, nil
}

func fetchStoryIfNeeded(id int, context appengine.Context, cache *Cache, datastore *Db, apiClient *HnApiClient) (Story, error) {
	story, err := cache.GetStory(id)

	// We have a copy in memcache
	if err == nil {
		return story, nil
	}

	// We have a copy in our datastore
	story, err = datastore.GetStory(id)
	if err == nil {
		return story, nil
	}

	// S.O.L .. Will need to fetch the story
	story, err = apiClient.GetStory(id)
	if err != nil {
		context.Errorf("%v", err)
	} else {
		err = datastore.SaveStory(story)
		if err != nil {
			context.Errorf("Error saving story to datastore: %v", err)
		} else {
			context.Debugf("Got story: %v", id)
			cache.SetStory(story)
		}
	}

	return story, err
}
