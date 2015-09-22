package hackernews

import (
	"fmt"
	"net/http"
)

type AppError struct {
	Err     error
	Message string
	Code    int
}

func NewErrorWithMessageAndCode(err error, message string, code int) *AppError {
	return &AppError{
		Err:     err,
		Message: message,
		Code:    code,
	}
}

func NewErrorWithMessage(err error, message string) *AppError {
	return NewErrorWithMessageAndCode(err, message, http.StatusInternalServerError)
}

func NewError(err error) *AppError {
	return NewErrorWithMessage(err, "")
}

func (ae *AppError) Error() string {
	return ae.Err.Error()
}

type AppHandler func(handler *Handler) (string, *AppError)

func (fn AppHandler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	handler := NewHandler(r)
	if json, err := fn(handler); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
	} else {
		fmt.Fprintf(w, json)
	}
}
