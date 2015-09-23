package hackernews

import (
	"appengine"
	"net/http"

	"github.com/gorilla/mux"
)

const encodingProto = "proto"
const encodingJson = "json"

type Handler struct {
	context   appengine.Context
	apiClient *HnApiClient
	dataStore *Db
	cache     *Cache
	vars      map[string]string
	encoding  string
}

func NewHandler(r *http.Request) *Handler {
	var handler Handler
	handler.context = appengine.NewContext(r)
	handler.apiClient = NewClient(handler.context)
	handler.dataStore = NewDb(handler.context)
	handler.cache = NewCache(handler.context)
	handler.vars = mux.Vars(r)
	handler.encoding = r.FormValue("format")
	return &handler
}

func (handler *Handler) Logd(format string, args ...interface{}) {
	handler.context.Debugf(format, args)
}

func (handler *Handler) Loge(format string, args ...interface{}) {
	handler.context.Errorf(format, args)
}

func (handler *Handler) EncodeStories(stories []*Story) ([]byte, *ApiError) {
	if handler.encoding == encodingProto {
		return encodeAsProto(ToStoryListProto(stories))
	} else {
		return encodeAsJson(stories)
	}
}

func (handler *Handler) EncodeStory(story *Story) ([]byte, *ApiError) {
	if handler.encoding == encodingProto {
		return encodeAsProto(story.ToProto())
	} else {
		return encodeAsJson(story)
	}
}

func (handler *Handler) EncodeComment(comment *Comment) ([]byte, *ApiError) {
	if handler.encoding == encodingProto {
		return encodeAsProto(comment.ToProto())
	} else {
		return encodeAsJson(comment)
	}
}

func (handler *Handler) EncodeIds(ids []int) ([]byte, *ApiError) {
	if handler.encoding == encodingProto {
		return encodeAsProto(ToIdsProto(ids))
	} else {
		return encodeAsJson(ids)
	}
}

func (handler *Handler) cleanupOldData() {
	// First, get ids of stories we still need

	idFns := []func() ([]int, error){
		handler.GetTopStoryIds,
		handler.GetNewStoryIds,
		handler.GetAskStoryIds,
		handler.GetShowStoryIds,
		handler.GetJobStoryIds,
	}

	ids := make([]int, 0)
	for _, idFn := range idFns {
		newIds, err := idFn()
		if err == nil {
			ids = append(ids, newIds...)
		}
	}

	handler.dataStore.DeleteStoriesNotIn(ids)
}

func (handler *Handler) GetStory(storyId int, fetchComments bool) (*Story, error) {
	story, err := handler.cache.GetStory(storyId)

	if err != nil {
		story, err = handler.dataStore.GetStory(storyId)
		if err != nil {
			story, err = handler.apiClient.GetStory(storyId)
			if err != nil {
				return nil, err
			} else {
				handler.Logd("Got story %+v", *story)
			}

			if fetchComments {
				handler.fetchTopComments(story)
			}

			if err = handler.dataStore.SaveStory(story); err != nil {
				handler.Loge("Error saving story to data store: %v", err)
			}
			handler.cache.SetStory(story)
		}
	}

	return story, err
}

func (handler *Handler) GetComment(storyId int) (*Comment, error) {
	comment, err := handler.cache.GetComment(storyId)

	if err != nil {
		comment, err = handler.dataStore.GetComment(storyId)
		if err != nil {
			comment, err = handler.apiClient.GetComment(storyId)
			if err != nil {
				return nil, err
			}

			if err = handler.dataStore.SaveComment(comment); err != nil {
				handler.Loge("Error saving comment to data store: %v", err)
			}
			handler.cache.SetComment(comment)
		}
	}

	return comment, err
}

func (handler *Handler) GetTopStoryIds() ([]int, error) {
	return getStoryIds(handler.cache.GetTopStories,
		handler.apiClient.GetTopStories,
		handler.cache.SetTopStories,
	)
}

func (handler *Handler) GetNewStoryIds() ([]int, error) {
	return getStoryIds(handler.cache.GetNewStories,
		handler.apiClient.GetNewStories,
		handler.cache.SetNewStories,
	)
}

func (handler *Handler) GetAskStoryIds() ([]int, error) {
	return getStoryIds(handler.cache.GetAskStories,
		handler.apiClient.GetAskStories,
		handler.cache.SetAskStories,
	)
}

func (handler *Handler) GetShowStoryIds() ([]int, error) {
	return getStoryIds(handler.cache.GetShowStories,
		handler.apiClient.GetShowStories,
		handler.cache.SetShowStories,
	)
}

func (handler *Handler) GetJobStoryIds() ([]int, error) {
	return getStoryIds(handler.cache.GetJobStories,
		handler.apiClient.GetJobStories,
		handler.cache.SetJobStories,
	)
}

func (handler *Handler) GetStoriesFromDataStore(ids []int) ([]byte, *ApiError) {
	stories, err := handler.dataStore.GetStories(ids)
	if err != nil {
		return nil, NewError(err)
	}

	return handler.EncodeStories(stories)
}

func getStoryIds(fromCacheFunc func() []int, fromApiClientFunc func() ([]int, error), saveToCacheFunc func([]int)) ([]int, error) {
	stories := fromCacheFunc()
	if stories != nil {
		return stories, nil
	}

	stories, err := fromApiClientFunc()
	if err == nil {
		saveToCacheFunc(stories)
	}

	return stories, err
}

func (handler *Handler) fetchTopComments(story *Story) {
	commentsToRetrieve := len(story.Kids)
	if commentsToRetrieve > 5 {
		commentsToRetrieve = 5
	}

	for i := 0; i < commentsToRetrieve; i++ {
		commentId := story.Kids[i]

		comment, err := handler.GetComment(commentId)
		if err == nil {
			story.Comments = append(story.Comments, *comment)
		}
	}
}
