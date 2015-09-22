package hackernews

type Tasks struct{}

type StoryIdFunc func(*Handler) ([]int, error)

func (tasks *Tasks) getTopStories(handler *Handler) (string, *ApiError) {
	return tasks.getStories(handler, cacheTopStoryIds)
}

func (tasks *Tasks) getNewStories(handler *Handler) (string, *ApiError) {
	return tasks.getStories(handler, cacheNewStoryIds)
}

func (tasks *Tasks) getStories(handler *Handler, fn StoryIdFunc) (string, *ApiError) {
	topStories, err := fn(handler)
	if err != nil {
		return "", NewError(err)
	}

	for index, id := range topStories {
		if index < 5 {
			_, err := fetchStoryIfNeeded(id, handler)
			if err != nil {
				return "", NewError(err)
			}
		}
	}

	return "Success", nil
}

func cacheTopStoryIds(handler *Handler) ([]int, error) {
	topStories, err := handler.apiClient.GetTopStories()
	if err != nil {
		return topStories, err
	}

	handler.Logd("Got top stories from network: %v", topStories)

	handler.cache.SetTopStories(topStories)

	return topStories, nil
}

func cacheNewStoryIds(handler *Handler) ([]int, error) {
	newStories, err := handler.apiClient.GetNewStories()
	if err != nil {
		return newStories, err
	}

	handler.Logd("Got new stories from network: %v", newStories)

	handler.cache.SetNewStories(newStories)

	return newStories, nil
}

func fetchStoryIfNeeded(id int, handler *Handler) (*Story, error) {
	story, err := handler.cache.GetStory(id)

	// We have a copy in memcache
	if err == nil {
		return story, nil
	}

	// We have a copy in our datastore
	story, err = handler.dataStore.GetStory(id)
	if err == nil {
		return story, nil
	}

	// S.O.L .. Will need to fetch the story
	story, err = handler.apiClient.GetStory(id)
	if err != nil {
		handler.Loge("%v", err)
	} else {
		fetchTopComments(story, handler)
		err = handler.dataStore.SaveStory(story)
		if err != nil {
			handler.Logd("Error saving story to datastore: %v", err)
		} else {
			handler.Logd("Got story: %v", id)
			handler.cache.SetStory(story)
		}
	}

	return story, err
}

func fetchTopComments(story *Story, handler *Handler) {
	commentsToRetrieve := len(story.Kids)
	if commentsToRetrieve > 5 {
		commentsToRetrieve = 5
	}

	for i := 0; i < commentsToRetrieve; i++ {
		commentId := story.Kids[i]

		comment, err := handler.dataStore.GetComment(commentId)
		if err == nil {
			story.Comments = append(story.Comments, *comment)
		} else {
			comment, err := handler.apiClient.GetComment(commentId)
			if err == nil {
				story.Comments = append(story.Comments, *comment)
				handler.dataStore.SaveComment(comment)
			} else {
				handler.Loge("Error fetching comment: %v", err)
			}
		}
	}
}
