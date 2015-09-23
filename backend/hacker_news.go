package hackernews

import (
	"github.com/gorilla/mux"
	"net/http"
	"strconv"
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
	router.Handle("/comment/{comment:[0-9]+}", ApiHandler(getComment))
	router.Handle("/comment/{comment:[0-9]+}/comments", ApiHandler(getCommentIdsForComment))
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

	story, err := handler.GetStory(storyId, false)

	if err != nil {
		return nil, NewErrorWithMessageAndCode(err, "No story found", http.StatusNotFound)
	}

	return handler.EncodeStory(story)
}

func getComment(handler *Handler) ([]byte, *ApiError) {
	commentId, _ := strconv.Atoi(handler.vars["comment"])

	comment, err := handler.GetComment(commentId)
	if err != nil {
		return nil, NewErrorWithMessageAndCode(err, "No comment found", http.StatusNotFound)
	}

	return handler.EncodeComment(comment)
}

func getCommentIdsForStory(handler *Handler) ([]byte, *ApiError) {
	storyId, _ := strconv.Atoi(handler.vars["story"])

	story, err := handler.GetStory(storyId, false)

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
	return handler.getStoriesJson(handler.GetTopStoryIds)
}

func getNewStories(handler *Handler) ([]byte, *ApiError) {
	return handler.getStoriesJson(handler.GetNewStoryIds)
}

func getAskStories(handler *Handler) ([]byte, *ApiError) {
	return handler.getStoriesJson(handler.GetAskStoryIds)
}

func getShowStories(handler *Handler) ([]byte, *ApiError) {
	return handler.getStoriesJson(handler.GetShowStoryIds)
}

func getJobStories(handler *Handler) ([]byte, *ApiError) {
	return handler.getStoriesJson(handler.GetJobStoryIds)
}

func (handler *Handler) getStoriesJson(fn func() ([]int, error)) ([]byte, *ApiError) {
	storyIds, err := fn()
	if err != nil {
		return nil, NewError(err)
	}

	return handler.GetStoriesFromDataStore(storyIds)
}
