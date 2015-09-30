package hackernews

import (
	"sync"
)

type Tasks struct{}

type FetchStoryResult struct {
	story *Story
	err   error
}

func (tasks *Tasks) clearOldData(handler *Handler) ([]byte, *ApiError) {
	handler.cleanupOldData()
	return nil, nil
}

func (tasks *Tasks) getTopStories(handler *Handler) ([]byte, *ApiError) {
	defer func() { handler.cache.ClearStories(cacheKeyTopStoriesList) }()
	return tasks.getStoryIds(handler, handler.apiClient.GetTopStories, handler.cache.SetTopStories, handler.dataStore.SaveTopStoryIds)
}

func (tasks *Tasks) getNewStories(handler *Handler) ([]byte, *ApiError) {
	defer func() { handler.cache.ClearStories(cacheKeyNewStoriesList) }()
	return tasks.getStoryIds(handler, handler.apiClient.GetNewStories, handler.cache.SetNewStories, handler.dataStore.SaveNewStoryIds)
}

func (tasks *Tasks) getAskStories(handler *Handler) ([]byte, *ApiError) {
	defer func() { handler.cache.ClearStories(cacheKeyAskStoriesList) }()
	return tasks.getStoryIds(handler, handler.apiClient.GetAskStories, handler.cache.SetAskStories, handler.dataStore.SaveAskStoryIds)
}

func (tasks *Tasks) getShowStories(handler *Handler) ([]byte, *ApiError) {
	defer func() { handler.cache.ClearStories(cacheKeyShowStoriesList) }()
	return tasks.getStoryIds(handler, handler.apiClient.GetShowStories, handler.cache.SetShowStories, handler.dataStore.SaveShowStoryIds)
}

func (tasks *Tasks) getJobStories(handler *Handler) ([]byte, *ApiError) {
	defer func() { handler.cache.ClearStories(cacheKeyJobStoriesList) }()
	return tasks.getStoryIds(handler, handler.apiClient.GetJobStories, handler.cache.SetJobStories, handler.dataStore.SaveJobStoryIds)
}

func (tasks *Tasks) getStoryIds(handler *Handler, fromApiClientFn func() ([]int, error), saveToCacheFn func([]int), saveToDbFn func([]int) error) ([]byte, *ApiError) {
	storyIds, err := fromApiClientFn()
	if err != nil {
		return nil, NewError(err)
	} else {
		handler.Logd("Got story ids from network: %v", storyIds)
	}

	storiesToRetrieve := len(storyIds)
	if storiesToRetrieve > 120 {
		storiesToRetrieve = 120
	}

	storyIds = storyIds[:storiesToRetrieve]

	if err = getStories(handler, storyIds); err != nil {
		return nil, NewError(err)
	}

	if err = saveToDbFn(storyIds); err != nil {
		return nil, NewError(err)
	}

	saveToCacheFn(storyIds)

	return nil, nil
}

func getStories(handler *Handler, storyIds []int) error {
	const workers = 60
	ch := make(chan int, workers)
	storyCh := make(chan *FetchStoryResult, len(storyIds))

	var wg sync.WaitGroup
	wg.Add(workers)
	for i := 0; i < workers; i++ {
		go func() {
			defer wg.Done()
			for storyId := range ch {
				handler.Logd("Attempting to fetch story %v", storyId)
				story, err := handler.GetStory(storyId, true, false)
				storyCh <- &FetchStoryResult{story: story, err: err}
			}
		}()
	}

	for _, id := range storyIds {
		ch <- id
	}

	close(ch)
	wg.Wait()

	stories := []*Story{}

	for i := 0; i < len(storyIds); i++ {
		result := <-storyCh
		if result.err != nil {
			return result.err
		} else {
			stories = append(stories, result.story)
		}
	}

	return nil
}
