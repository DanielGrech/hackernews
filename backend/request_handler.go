package hackernews

import (
	"appengine"
	"encoding/json"
	"net/http"

	"github.com/gorilla/mux"
)

type Handler struct {
	context   appengine.Context
	apiClient *HnApiClient
	dataStore *Db
	cache     *Cache
	vars      map[string]string
}

func NewHandler(r *http.Request) *Handler {
	var handler Handler
	handler.context = appengine.NewContext(r)
	handler.apiClient = NewClient(handler.context)
	handler.dataStore = NewDb(handler.context)
	handler.cache = NewCache(handler.context)
	handler.vars = mux.Vars(r)
	return &handler
}

func (handler *Handler) Logd(format string, args ...interface{}) {
	handler.context.Debugf(format, args)
}

func (handler *Handler) Loge(format string, args ...interface{}) {
	handler.context.Errorf(format, args)
}

func (handler *Handler) GetTopStoryIds() ([]int, error) {
	topStories := handler.cache.GetTopStories()
	if topStories != nil {
		return topStories, nil
	}

	topStories, err := handler.apiClient.GetTopStories()
	if err == nil {
		handler.cache.SetTopStories(topStories)
	}

	return topStories, err
}

func (handler *Handler) GetNewStoryIds() ([]int, error) {
	newStories := handler.cache.GetNewStories()
	if newStories != nil {
		return newStories, nil
	}

	newStories, err := handler.apiClient.GetNewStories()
	if err == nil {
		handler.cache.SetNewStories(newStories)
	}

	return newStories, err
}

func (handler *Handler) GetStoriesFromDataStore(ids []int) (string, *ApiError) {
	stories, err := handler.dataStore.GetStories(ids)
	if err != nil {
		return "", NewError(err)
	}

	jsonData, err := json.Marshal(stories)
	if err != nil {
		return "", NewError(err)
	}

	return string(jsonData), nil
}
