package hackernews

import (
	"encoding/json"
	"fmt"
	"github.com/golang/protobuf/proto"
	"net/http"
)

type ApiError struct {
	Err     error
	Message string
	Code    int
}

type ApiHandler func(handler *Handler) ([]byte, *ApiError)

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
	return NewErrorWithMessage(err, err.Error())
}

func (ae *ApiError) Error() string {
	if ae.Message != "" {
		return ae.Message
	} else if ae.Err != nil {
		return ae.Err.Error()
	} else {
		return "Unknown error"
	}
}

func (fn ApiHandler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	handler := NewHandler(r)
	if json, err := fn(handler); err != nil {
		handler.Loge("Error executing request: %v", err)
		http.Error(w, fmt.Sprintf("{\"error\": \"%s\", \"description\": \"%v\"}", err.Message, err.Error()), http.StatusInternalServerError)
	} else {
		w.Write(json)
	}
}

func encodeAsJson(object interface{}) ([]byte, *ApiError) {
	data, err := json.Marshal(object)
	if err != nil {
		return nil, NewError(err)
	}

	return data, nil
}

func encodeAsProto(object proto.Message) ([]byte, *ApiError) {
	data, err := proto.Marshal(object)
	if err != nil {
		return nil, NewError(err)
	}

	return data, nil
}
func (story *Story) ToProto() *PbStory {
	return &PbStory{
		Id:           proto.Int64(int64(story.ID)),
		Time:         proto.Int64(int64(story.Time)),
		Type:         proto.String(story.Type),
		ParentId:     proto.Int64(int64(story.Parent)),
		Author:       proto.String(story.By),
		CommentIds:   toInt64Slice(story.Kids),
		Score:        proto.Int32(int32(story.Score)),
		Title:        proto.String(story.Title),
		Text:         proto.String(story.Text),
		Url:          proto.String(story.URL),
		Parts:        toInt64Slice(story.Parts),
		CommentCount: proto.Int32(int32(story.CommentCount)),
		Comments:     toProtoCommentSlice(story.Comments),
		Deleted:      proto.Bool(story.Deleted),
		Dead:         proto.Bool(story.Dead),
	}
}

func (comment *Comment) ToProto() *PbComment {
	return &PbComment{
		Id:           proto.Int64(int64(comment.ID)),
		Time:         proto.Int64(int64(comment.Time)),
		ParentId:     proto.Int64(int64(comment.Parent)),
		Author:       proto.String(comment.By),
		CommentIds:   toInt64Slice(comment.Kids),
		Text:         proto.String(comment.Text),
		CommentCount: proto.Int32(int32(comment.CommentCount)),
		Comments:     toProtoCommentSlice(comment.Comments),
		Deleted:      proto.Bool(comment.Deleted),
		Dead:         proto.Bool(comment.Dead),
	}
}

func ToStoryListProto(stories []*Story) *PbStoryList {
	return &PbStoryList{
		Stories: toProtoStorySlice(stories),
	}
}

func ToCommentListProto(comments []*Comment) *PbCommentList {
	return &PbCommentList{
		Comments: toProtoCommentSlice(comments),
	}
}

func ToIdsProto(ids []int) *PbIds {
	return &PbIds{
		Ids: toInt64Slice(ids),
	}
}

func toProtoStorySlice(input []*Story) []*PbStory {
	if len(input) == 0 {
		return nil
	}

	retval := make([]*PbStory, len(input))
	for index, val := range input {
		retval[index] = val.ToProto()
	}

	return retval
}

func toProtoCommentSlice(input []*Comment) []*PbComment {
	if len(input) == 0 {
		return nil
	}

	retval := make([]*PbComment, len(input))
	for index, val := range input {
		retval[index] = val.ToProto()
	}

	return retval
}

func toInt64Slice(input []int) []int64 {
	if len(input) == 0 {
		return nil
	}

	retval := make([]int64, len(input))
	for index, val := range input {
		retval[index] = int64(val)
	}

	return retval
}
