package hackernews

import (
	"github.com/gorilla/mux"
	"net/http"
	"strconv"
	"sync"
)

func init() {
	router := mux.NewRouter()

	var tasks Tasks

	router.Handle("/top", ApiHandler(getTopStories))
	router.Handle("/new", ApiHandler(getNewStories))
	router.Handle("/ask", ApiHandler(getAskStories))
	router.Handle("/show", ApiHandler(getShowStories))
	router.Handle("/job", ApiHandler(getJobStories))
	router.Handle("/story/{story:[0-9]+}", ApiHandler(getStory))
	router.Handle("/story/{story:[0-9]+}/comments", ApiHandler(getCommentIdsForStory))
	router.Handle("/storybycomment/{comment:[0-9]+}", ApiHandler(getStoryByComment))
	router.Handle("/comment/{comment:[0-9]+}", ApiHandler(getComment))
	router.Handle("/comment/{comment:[0-9]+}/comments", ApiHandler(getCommentIdsForComment))
	router.Handle("/comments", ApiHandler(getComments))
	router.Handle("/tasks/top_stories", ApiHandler(tasks.getTopStories))
	router.Handle("/tasks/new_stories", ApiHandler(tasks.getNewStories))
	router.Handle("/tasks/ask_stories", ApiHandler(tasks.getAskStories))
	router.Handle("/tasks/show_stories", ApiHandler(tasks.getShowStories))
	router.Handle("/tasks/job_stories", ApiHandler(tasks.getJobStories))
	router.Handle("/tasks/cleanup", ApiHandler(tasks.clearOldData))

	http.Handle("/", router)
}

func getStory(handler *Handler) ([]byte, *ApiError) {
	storyId, _ := strconv.Atoi(handler.vars["story"])

	story, err := handler.GetStory(storyId, false, true)

	if err != nil {
		return nil, NewErrorWithMessageAndCode(err, "No story found", http.StatusNotFound)
	}

	return handler.EncodeStory(story)
}

func getStoryByComment(handler *Handler) ([]byte, *ApiError) {
	commentId, _ := strconv.Atoi(handler.vars["comment"])

	comment, err := handler.GetComment(commentId)
	if err != nil {
		return nil, NewError(err)
	}

	var story *Story
	for comment != nil && comment.Parent > 0 {
		story, err = handler.GetStory(comment.Parent, false, true)
		if err == nil {
			break
		} else {
			comment, _ = handler.GetComment(comment.Parent)
		}
	}

	if story == nil {
		return nil, NewErrorWithMessage(nil, "No story found for comment")
	} else {
		return handler.EncodeStory(story)
	}

}

func getComments(handler *Handler) ([]byte, *ApiError) {
	storyId := handler.queryParams["story"]
	commentIds := handler.queryParams["id"]

	const workers = 5
	ch := make(chan int, workers)
	commentCh := make(chan *Comment, len(commentIds))

	var wg sync.WaitGroup
	wg.Add(workers)
	for i := 0; i < workers; i++ {
		go func() {
			defer wg.Done()
			for commentId := range ch {
				comment, err := handler.GetComment(commentId)
				if err != nil {
					handler.Loge("Error getting comment %v: %v", commentId, err)
				}

				commentCh <- comment
			}
		}()
	}

	for _, strId := range commentIds {
		id, _ := strconv.Atoi(strId)
		ch <- id
	}

	close(ch)
	wg.Wait()

	comments := []*Comment{}

	for i := 0; i < len(commentIds); i++ {
		comment := <-commentCh
		if comment != nil {
			comments = append(comments, comment)
		}
	}

	if len(storyId) > 0 {
		id, err := strconv.Atoi(storyId[0])
		if err == nil {
			handler.cache.ClearStory(id)
		}
	}

	return handler.EncodeComments(comments)
}

func getComment(handler *Handler) ([]byte, *ApiError) {
	commentId, _ := strconv.Atoi(handler.vars["comment"])

	comment, err := handler.GetComment(commentId)
	if err != nil {
		return nil, NewErrorWithMessageAndCode(err, "No comment found", http.StatusNotFound)
	} else {
		handler.cache.ClearStory(comment.Parent)
	}

	return handler.EncodeComment(comment)
}

func getCommentIdsForStory(handler *Handler) ([]byte, *ApiError) {
	storyId, _ := strconv.Atoi(handler.vars["story"])

	story, err := handler.GetStory(storyId, false, false)

	if err != nil {
		return nil, NewError(err)
	}

	return handler.EncodeIds(story.Kids)
}

func getCommentIdsForComment(handler *Handler) ([]byte, *ApiError) {
	commentId, _ := strconv.Atoi(handler.vars["comment"])

	comment, err := handler.dataStore.GetComment(commentId)
	if err != nil {
		return nil, NewErrorWithMessageAndCode(err, "No comment found", http.StatusNotFound)
	}

	return handler.EncodeIds(comment.Kids)
}

func getTopStories(handler *Handler) ([]byte, *ApiError) {
	return getStoriesJson(handler, handler.GetTopStoryIds, cacheKeyTopStoriesList)
}

func getNewStories(handler *Handler) ([]byte, *ApiError) {
	return getStoriesJson(handler, handler.GetNewStoryIds, cacheKeyNewStoriesList)
}

func getAskStories(handler *Handler) ([]byte, *ApiError) {
	return getStoriesJson(handler, handler.GetAskStoryIds, cacheKeyAskStoriesList)
}

func getShowStories(handler *Handler) ([]byte, *ApiError) {
	return getStoriesJson(handler, handler.GetShowStoryIds, cacheKeyShowStoriesList)
}

func getJobStories(handler *Handler) ([]byte, *ApiError) {
	return getStoriesJson(handler, handler.GetJobStoryIds, cacheKeyJobStoriesList)
}

func getStoriesJson(handler *Handler, idsFn func() ([]int, error), key string) ([]byte, *ApiError) {
	storyIds, err := idsFn()
	if err != nil {
		return nil, NewError(err)
	}

	stories, err := handler.GetStories(storyIds, key)
	if err != nil {
		return nil, NewError(err)
	}

	return handler.EncodeStories(stories)
}
