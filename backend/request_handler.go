package hackernews

import (
	"appengine"
	"net/http"
	"net/url"
	"sync"

	"github.com/gorilla/mux"
)

const encodingProto = "proto"
const encodingJson = "json"

type Handler struct {
	context     appengine.Context
	apiClient   *HnApiClient
	dataStore   *Db
	cache       *Cache
	vars        map[string]string
	queryParams url.Values
	encoding    string
}

func NewHandler(r *http.Request) *Handler {
	var handler Handler
	handler.context = appengine.NewContext(r)
	handler.apiClient = NewClient(handler.context)
	handler.dataStore = NewDb(handler.context)
	handler.cache = NewCache(handler.context)
	handler.vars = mux.Vars(r)
	handler.encoding = r.FormValue("format")
	handler.queryParams = r.URL.Query()

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

func (handler *Handler) EncodeComments(comments []*Comment) ([]byte, *ApiError) {
	if handler.encoding == encodingProto {
		return encodeAsProto(ToCommentListProto(comments))
	} else {
		return encodeAsJson(comments)
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

func (handler *Handler) GetStory(storyId int, useApi bool, enableDbCommentFetch bool) (*Story, error) {
	if !useApi {
		if story, err := handler.cache.GetStory(storyId); err == nil {
			return story, err
		}

		if story, err := handler.dataStore.GetStory(storyId, enableDbCommentFetch); err == nil {
			if enableDbCommentFetch {
				handler.cache.SetStory(story)
			}
			return story, err
		}
	}

	story, err := handler.apiClient.GetStory(storyId)
	if err != nil {
		return nil, err
	} else {
		handler.Logd("Got story %+v", story.ID, story.Title)
	}

	if useApi {
		handler.fetchTopComments(story)
	}

	if err = handler.dataStore.SaveStory(story); err != nil {
		handler.Loge("Error saving story to data store: %v", err)
	}
	handler.cache.SetStory(story)

	return story, err
}

func (handler *Handler) GetComment(commentId int) (*Comment, error) {
	comment, err := handler.cache.GetComment(commentId)

	if err != nil {
		comment, err = handler.dataStore.GetComment(commentId)
		if err != nil {
			comment, err = handler.apiClient.GetComment(commentId)
			if err != nil {
				return nil, err
			}

			if err = handler.dataStore.SaveComment(comment); err != nil {
				handler.Loge("Error saving comment to data store: %v", err)
			}
			handler.cache.SetComment(comment)

			for _, subcommentId := range comment.Kids {
				_, err = handler.GetComment(subcommentId)
				if err != nil {
					handler.Loge("Error getting subcomment: %v", err)
				}
			}
		} else {
			handler.cache.SetComment(comment)
		}
	}

	return comment, err
}

func (handler *Handler) GetTopStoryIds() ([]int, error) {
	return handler.getStoryIds(handler.cache.GetTopStories,
		handler.dataStore.GetTopStoryIds,
		handler.apiClient.GetTopStories,
		handler.dataStore.SaveTopStoryIds,
		handler.cache.SetTopStories,
	)
}

func (handler *Handler) GetNewStoryIds() ([]int, error) {
	return handler.getStoryIds(handler.cache.GetNewStories,
		handler.dataStore.GetNewStoryIds,
		handler.apiClient.GetNewStories,
		handler.dataStore.SaveNewStoryIds,
		handler.cache.SetNewStories,
	)
}

func (handler *Handler) GetAskStoryIds() ([]int, error) {
	return handler.getStoryIds(handler.cache.GetAskStories,
		handler.dataStore.GetAskStoryIds,
		handler.apiClient.GetAskStories,
		handler.dataStore.SaveAskStoryIds,
		handler.cache.SetAskStories,
	)
}

func (handler *Handler) GetShowStoryIds() ([]int, error) {
	return handler.getStoryIds(handler.cache.GetShowStories,
		handler.dataStore.GetShowStoryIds,
		handler.apiClient.GetShowStories,
		handler.dataStore.SaveShowStoryIds,
		handler.cache.SetShowStories,
	)
}

func (handler *Handler) GetJobStoryIds() ([]int, error) {
	return handler.getStoryIds(handler.cache.GetJobStories,
		handler.dataStore.GetJobStoryIds,
		handler.apiClient.GetJobStories,
		handler.dataStore.SaveJobStoryIds,
		handler.cache.SetJobStories,
	)
}

func (handler *Handler) GetStories(ids []int, typeKey string) ([]*Story, error) {
	stories := handler.cache.GetStories(typeKey)
	if len(stories) == 0 {
		stories, err := handler.dataStore.GetStories(ids, false)
		if err == nil {
			handler.cache.SetStories(typeKey, stories)
			return stories, nil
		} else {
			return nil, err
		}
	}

	return stories, nil
}

func (handler *Handler) getStoryIds(fromCacheFunc func() []int, fromDbFunc func() ([]int, error), fromApiClientFunc func() ([]int, error), saveToDbFn func([]int) error, saveToCacheFunc func([]int)) ([]int, error) {
	stories := fromCacheFunc()
	if stories != nil && len(stories) > 0 {
		return stories, nil
	}

	stories, err := fromDbFunc()
	if err == nil {
		saveToCacheFunc(stories)
		return stories, err
	}

	stories, err = fromApiClientFunc()
	if err == nil {
		err = saveToDbFn(stories)
		if err == nil {
			saveToCacheFunc(stories)
		}
	}

	return stories, err
}

func (handler *Handler) fetchTopComments(story *Story) {
	commentIds := story.Kids

	commentIdsToFetch := len(commentIds)

	const workers = 10
	ch := make(chan int, workers)
	commentCh := make(chan *Comment, commentIdsToFetch)

	var wg sync.WaitGroup
	wg.Add(workers)
	for i := 0; i < workers; i++ {
		go func() {
			defer wg.Done()
			for commentId := range ch {
				comment, err := handler.GetComment(commentId)
				if err != nil {
					handler.Loge("Error getting comment %v", commentId, err)
				} else {
					handler.Logd("Got comment %v", commentId, comment.Parent)
				}

				commentCh <- comment
			}
		}()
	}

	for index, id := range commentIds {
		if index < commentIdsToFetch {
			ch <- id
		} else {
			break
		}
	}

	close(ch)
	wg.Wait()

	for i := 0; i < commentIdsToFetch; i++ {
		comment := <-commentCh
		if comment != nil {
			story.Comments = append(story.Comments, comment)
		}
	}
}
