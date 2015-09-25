package hackernews

type Tasks struct{}

func (tasks *Tasks) clearOldData(handler *Handler) ([]byte, *ApiError) {
	handler.cleanupOldData()
	return nil, nil
}

func (tasks *Tasks) getTopStories(handler *Handler) ([]byte, *ApiError) {
	return tasks.getStoryIds(handler, handler.apiClient.GetTopStories, handler.cache.SetTopStories, handler.dataStore.SaveTopStoryIds)
}

func (tasks *Tasks) getNewStories(handler *Handler) ([]byte, *ApiError) {
	return tasks.getStoryIds(handler, handler.apiClient.GetNewStories, handler.cache.SetNewStories, handler.dataStore.SaveNewStoryIds)
}

func (tasks *Tasks) getAskStories(handler *Handler) ([]byte, *ApiError) {
	return tasks.getStoryIds(handler, handler.apiClient.GetAskStories, handler.cache.SetAskStories, handler.dataStore.SaveAskStoryIds)
}

func (tasks *Tasks) getShowStories(handler *Handler) ([]byte, *ApiError) {
	return tasks.getStoryIds(handler, handler.apiClient.GetShowStories, handler.cache.SetShowStories, handler.dataStore.SaveShowStoryIds)
}

func (tasks *Tasks) getJobStories(handler *Handler) ([]byte, *ApiError) {
	return tasks.getStoryIds(handler, handler.apiClient.GetJobStories, handler.cache.SetJobStories, handler.dataStore.SaveJobStoryIds)
}

func (tasks *Tasks) getStoryIds(handler *Handler, fromApiClientFn func() ([]int, error), saveToCacheFn func([]int), saveToDbFn func([]int) error) ([]byte, *ApiError) {
	storyIds, err := fromApiClientFn()
	if err != nil {
		return nil, NewError(err)
	} else {
		handler.Logd("Got story ids from network: %v", storyIds)
	}

	for index, id := range storyIds {
		if index < 2 {
			_, err := handler.GetStory(id, true)
			if err != nil {
				return nil, NewError(err)
			}
		}
	}

	if err = saveToDbFn(storyIds); err != nil {
		return nil, NewError(err)
	}

	saveToCacheFn(storyIds)

	return nil, nil
}
