package hackernews

import (
	"fmt"
	"net/http"

	"appengine"
)

func init() {
	http.HandleFunc("/top", getTopStories)
}

func getTopStories(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)

	client := NewClient(c)
	cache := NewCache(c)

	topStories, err := getTopStoryIds(c, cache, client)
	if err != nil {
		// http.Error(w, err.Error(), http.StatusInternalServerError)
		// return
		c.Debugf("Error getting top story ids %v", err)
		topStories = []int{1, 2, 3, 4, 5}
	}

	stories := getStories(c, cache, client, topStories)
	for _, story := range stories {
		c.Debugf("Got story: %v", story)
	}

	fmt.Fprintf(w, "Maaate")
}

func getTopStoryIds(context appengine.Context, cache *Cache, apiClient *HnApiClient) ([]int, error) {
	topStories := cache.GetTopStories()
	if topStories == nil {
		context.Debugf("No stories in cache")

		topStories, err := apiClient.GetTopStories()
		if err != nil {
			return topStories, err
		}

		context.Debugf("Got stories from network: %v", topStories)

		cache.SetTopStories(topStories)

		return topStories, nil
	} else {
		context.Debugf("Got stories from cache")
		return topStories, nil
	}
}

func getStories(context appengine.Context, cache *Cache, apiClient *HnApiClient, storyIds []int) (stories []Story) {
	storyMap := make(map[int]Story)
	chStories := make(chan Story)
	chFinished := make(chan bool)

	var fetchCount = 0
	for _, id := range storyIds {
		go getStory(id, &fetchCount, chStories, chFinished, context, cache, apiClient)
	}

	for size := len(storyIds); size > 0; {
		select {
		case story := <-chStories:
			storyMap[story.ID] = story
		case <-chFinished:
			size--
		}
	}

	for _, id := range storyIds {
		if story, ok := storyMap[id]; ok {
			stories = append(stories, story)
		} else {
			context.Warningf("No story found for id: %v", id)
		}
	}

	return
}

func getStory(id int, fetchCount *int, chStory chan Story, chFinished chan bool, context appengine.Context, cache *Cache, apiClient *HnApiClient) {
	defer func() {
		// Notify that we're done after this function
		chFinished <- true
	}()

	var story *Story = nil

	story = cache.GetStory(id)
	if story == nil {
		if *fetchCount < 5 {
			context.Debugf("Fetching %v", id)
			story, err := apiClient.GetStory(id)
			if err != nil {
				context.Errorf("%v", err)
			} else {
				cache.SetStory(story)
			}

			*fetchCount++
		} else {
			context.Debugf("Adding task to fetch %v", id)
		}
	} else {
		context.Debugf("Got story from cache")
	}

	if story != nil {
		chStory <- *story
	}
}
