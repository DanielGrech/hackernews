package hackernews

type Tasks struct{}

func (tasks *Tasks) clearOldData(handler *Handler) (string, *ApiError) {
	handler.cleanupOldData()
	return "Success", nil
}

func (tasks *Tasks) getTopStories(handler *Handler) (string, *ApiError) {
	return tasks.getStoryIds(handler, handler.apiClient.GetTopStories, handler.cache.SetTopStories)
}

func (tasks *Tasks) getNewStories(handler *Handler) (string, *ApiError) {
	return tasks.getStoryIds(handler, handler.apiClient.GetNewStories, handler.cache.SetNewStories)
}

func (tasks *Tasks) getAskStories(handler *Handler) (string, *ApiError) {
	return tasks.getStoryIds(handler, handler.apiClient.GetAskStories, handler.cache.SetAskStories)
}

func (tasks *Tasks) getShowStories(handler *Handler) (string, *ApiError) {
	return tasks.getStoryIds(handler, handler.apiClient.GetShowStories, handler.cache.SetShowStories)
}

func (tasks *Tasks) getJobStories(handler *Handler) (string, *ApiError) {
	return tasks.getStoryIds(handler, handler.apiClient.GetJobStories, handler.cache.SetJobStories)
}

func (tasks *Tasks) getStoryIds(handler *Handler, fromApiClientFn func() ([]int, error), saveToCacheFn func([]int)) (string, *ApiError) {
	storyIds, err := fromApiClientFn()
	if err != nil {
		return "", NewError(err)
	} else {
		handler.Logd("Got story ids from network: %v", storyIds)
	}

	for index, id := range storyIds {
		if index < 5 {
			_, err := handler.GetStory(id, true)
			if err != nil {
				return "", NewError(err)
			}
		}
	}

	saveToCacheFn(storyIds)

	return "Success", nil
}
