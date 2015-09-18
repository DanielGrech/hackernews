package hackernews

import (
	"appengine"
	"appengine/urlfetch"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"strconv"
)

type HnApiClient struct {
	BaseUrl string
	Suffix  string
	context appengine.Context
}

func NewClient(context appengine.Context) *HnApiClient {
	var hnc HnApiClient
	hnc.BaseUrl = "https://hacker-news.firebaseio.com/v0/"
	hnc.Suffix = ".json"
	hnc.context = context
	return &hnc
}

func (hnc *HnApiClient) fullUrl(urlPart string) string {
	return hnc.BaseUrl + urlPart + hnc.Suffix
}

func (hnc *HnApiClient) GetTopStories() (topStories []int, e error) {
	url := hnc.fullUrl("topstories")

	client := urlfetch.Client(hnc.context)

	resp, err := client.Get(url)
	if err != nil {
		return nil, err
	}

	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return nil, err
	}

	err = json.Unmarshal(body, &topStories)
	if err != nil {
		return nil, err
	}

	return
}

func (hnc *HnApiClient) GetStory(id int) (*Story, error) {
	item, err := hnc.GetItem(id)
	if err != nil {
		return nil, err
	}

	if item.Type() != "story" {
		return nil, fmt.Errorf("Called GetStory with ID #%v "+
			"which is not a story. It is a %v", id, item.Type())
	} else {
		story := item.ToStory()
		return &story, nil
	}
}

func (hnc *HnApiClient) GetComment(id int) (*Comment, error) {
	item, err := hnc.GetItem(id)
	if err != nil {
		return nil, err
	}

	if item.Type() != "comment" {
		return nil, fmt.Errorf("Called GetComment with ID #%v "+
			"which is not a comment. It is a %v", id, item.Type())
	} else {
		comment := item.ToComment()
		return &comment, nil
	}
}

func (hnc *HnApiClient) GetItem(id int) (item item, e error) {
	url := hnc.fullUrl("item/" + strconv.Itoa(id))

	client := urlfetch.Client(hnc.context)

	resp, err := client.Get(url)
	if err != nil {
		return item, err
	}

	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return nil, err
	}

	err = json.Unmarshal(body, &item)

	return item, err
}
