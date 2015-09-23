package hackernews

import (
	"encoding/json"
	"fmt"
	"net/http"
)

type ApiError struct {
	Err     error
	Message string
	Code    int
}

type ApiHandler func(handler *Handler) (string, *ApiError)

func NewErrorWithMessageAndCode(err error, message string, code int) *ApiError {
	return &ApiError{
		Err:     err,
		Message: message,
		Code:    code,
	}
}

func NewErrorWithMessage(err error, message string) *ApiError {
	return NewErrorWithMessageAndCode(err, message, http.StatusInternalServerError)
}

func NewError(err error) *ApiError {
	return NewErrorWithMessage(err, "")
}

func (ae *ApiError) Error() string {
	return ae.Err.Error()
}

func (fn ApiHandler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	handler := NewHandler(r)
	if json, err := fn(handler); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
	} else {
		fmt.Fprintf(w, json)
	}
}

func getIdsJson(ids []int) (string, *ApiError) {
	if len(ids) == 0 {
		return "[]", nil
	}

	return toJson(ids)
}

func toJson(object interface{}) (string, *ApiError) {
	jsonData, err := json.Marshal(object)
	if err != nil {
		return "", NewError(err)
	}

	return string(jsonData), nil
}
