package hackernews

import (
	"encoding/json"
	"github.com/gorilla/mux"
	"net/http"
	"strconv"
)

func init() {
	router := mux.NewRouter()

	var tasks Tasks

	router.Handle("/top", AppHandler(getTopStories))
	router.Handle("/story/{story:[0-9]+}", AppHandler(getStory))
	router.Handle("/story/{story:[0-9]+}/comments", AppHandler(getCommentsForStory))
	router.Handle("/comment/{comment:[0-9]+}", AppHandler(getComment))
	router.Handle("/comment/{comment:[0-9]+}/comments", AppHandler(getCommentsForComment))
	router.Handle("/tasks/top_stories", AppHandler(tasks.getTopStories))

	http.Handle("/", router)
}

func getStory(handler *Handler) (string, *AppError) {
	storyId, _ := strconv.Atoi(handler.vars["story"])

	story, err := handler.dataStore.GetStory(storyId)
	if err != nil {
		story, err = handler.apiClient.GetStory(storyId)
		if err != nil {
			return "", NewErrorWithMessage(err, "Error retrieving story")
		}

		handler.dataStore.SaveStory(story)
	}

	jsonData, err := json.Marshal(story)
	if err != nil {
		return "", NewError(err)
	}

	return string(jsonData), nil
}

func getComment(handler *Handler) (string, *AppError) {
	commentId, _ := strconv.Atoi(handler.vars["comment"])

	comment, err := handler.dataStore.GetComment(commentId)
	if err != nil {
		handler.Logd("No comment in data store!")
		comment, err = handler.apiClient.GetComment(commentId)
		if err != nil {
			return "", NewErrorWithMessageAndCode(err, "No comment found", http.StatusNotFound)
		}

		handler.Logd("Saving %v", comment)
		if err = handler.dataStore.SaveComment(comment); err != nil {
			handler.Loge("Error saving comment to data store: %v", err)
		}
	}

	jsonData, err := json.Marshal(comment)
	if err != nil {
		return "", NewError(err)
	}

	return string(jsonData), nil
}

func getCommentsForStory(handler *Handler) (string, *AppError) {
	storyId, _ := strconv.Atoi(handler.vars["story"])

	story, err := handler.dataStore.GetStory(storyId)
	if err != nil {
		return "", NewError(err)
	}

	commentIds := story.Kids
	if len(commentIds) == 0 {
		return "[]", nil
	}

	jsonData, err := json.Marshal(commentIds)
	if err != nil {
		return "", NewError(err)
	}

	return string(jsonData), nil
}

func getCommentsForComment(handler *Handler) (string, *AppError) {
	commentId, _ := strconv.Atoi(handler.vars["comment"])

	comment, err := handler.dataStore.GetComment(commentId)
	if err != nil {
		return "", NewErrorWithMessageAndCode(err, "No comment found", http.StatusNotFound)
	}

	commentIds := comment.Kids
	if len(commentIds) == 0 {
		return "[]", nil
	}

	jsonData, err := json.Marshal(commentIds)
	if err != nil {
		return "", NewError(err)
	}

	return string(jsonData), nil
}

func getTopStories(handler *Handler) (string, *AppError) {
	storyIds, err := handler.GetTopStoryIds()
	if err != nil {
		return "", NewError(err)
	}

	stories, err := handler.dataStore.GetStories(storyIds)
	if err != nil {
		return "", NewError(err)
	}

	jsonData, err := json.Marshal(stories)
	if err != nil {
		return "", NewError(err)
	}

	return string(jsonData), nil
}
