package hackernews

import (
	"fmt"
	"net/http"
)

type ApiError struct {
	Err     error
	Message string
	Code    int
}

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

type ApiHandler func(handler *Handler) (string, *ApiError)

func (fn ApiHandler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	handler := NewHandler(r)
	if json, err := fn(handler); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
	} else {
		fmt.Fprintf(w, json)
	}
}
