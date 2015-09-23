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

func getStory(handler *Handler) (string, *ApiError) {
	storyId, _ := strconv.Atoi(handler.vars["story"])

	story, err := handler.GetStory(storyId, false)

	if err != nil {
		return "", NewErrorWithMessageAndCode(err, "No story found", http.StatusNotFound)
	}

	return toJson(story)
}

func getComment(handler *Handler) (string, *ApiError) {
	commentId, _ := strconv.Atoi(handler.vars["comment"])

	comment, err := handler.GetComment(commentId)
	if err != nil {
		return "", NewErrorWithMessageAndCode(err, "No comment found", http.StatusNotFound)
	}

	return toJson(comment)
}

func getCommentIdsForStory(handler *Handler) (string, *ApiError) {
	storyId, _ := strconv.Atoi(handler.vars["story"])

	story, err := handler.GetStory(storyId, false)

	if err != nil {
		return "", NewError(err)
	}

	return getIdsJson(story.Kids)
}

func getCommentIdsForComment(handler *Handler) (string, *ApiError) {
	commentId, _ := strconv.Atoi(handler.vars["comment"])

	comment, err := handler.dataStore.GetComment(commentId)
	if err != nil {
		return "", NewErrorWithMessageAndCode(err, "No comment found", http.StatusNotFound)
	}

	return getIdsJson(comment.Kids)
}

func getTopStories(handler *Handler) (string, *ApiError) {
	return handler.getStoriesJson(handler.GetTopStoryIds)
}

func getNewStories(handler *Handler) (string, *ApiError) {
	return handler.getStoriesJson(handler.GetNewStoryIds)
}

func getAskStories(handler *Handler) (string, *ApiError) {
	return handler.getStoriesJson(handler.GetAskStoryIds)
}

func getShowStories(handler *Handler) (string, *ApiError) {
	return handler.getStoriesJson(handler.GetShowStoryIds)
}

func getJobStories(handler *Handler) (string, *ApiError) {
	return handler.getStoriesJson(handler.GetJobStoryIds)
}

func (handler *Handler) getStoriesJson(fn func() ([]int, error)) (string, *ApiError) {
	storyIds, err := fn()
	if err != nil {
		return "", NewError(err)
	}

	return handler.GetStoriesFromDataStore(storyIds)
}
