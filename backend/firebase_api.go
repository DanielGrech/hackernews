package hackernews

import (
	"appengine"
	"appengine/urlfetch"
	"encoding/json"
	"errors"
	"fmt"
	"io/ioutil"
	"net/http"
	"strconv"
	"time"
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

func (hnc *HnApiClient) NewUrlClient() *http.Client {
	var client = http.Client{
		Transport: &urlfetch.Transport{
			Context: hnc.context,
			AllowInvalidServerCertificate: true,
			Deadline:                      time.Minute,
		},
	}

	return &client
}

func (hnc *HnApiClient) fullUrl(urlPart string) string {
	return hnc.BaseUrl + urlPart + hnc.Suffix
}

func (hnc *HnApiClient) GetTopStories() (topStories []int, e error) {
	return hnc.getStories("topstories")
}

func (hnc *HnApiClient) GetNewStories() (stories []int, e error) {
	return hnc.getStories("newstories")
}

func (hnc *HnApiClient) GetAskStories() (stories []int, e error) {
	return hnc.getStories("askstories")
}

func (hnc *HnApiClient) GetShowStories() (stories []int, e error) {
	return hnc.getStories("showstories")
}

func (hnc *HnApiClient) GetJobStories() (stories []int, e error) {
	return hnc.getStories("jobstories")
}

func (hnc *HnApiClient) GetStory(id int) (*Story, error) {
	item, err := hnc.GetItem(id)
	if err != nil {
		return nil, err
	}

	if item.Type() == "comment" {
		return nil, fmt.Errorf("Called GetStory with ID #%v "+
			"which is not a story. It is a comment.", id)
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

	client := hnc.NewUrlClient()

	resp, err := client.Get(url)
	if err != nil {
		return item, err
	}

	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return nil, err
	}

	err = json.Unmarshal(body, &item)
	if len(item) == 0 {
		return nil, errors.New("Null reponse received from API")
	}

	return item, err
}

func (hnc *HnApiClient) getStories(urlPath string) (stories []int, e error) {
	url := hnc.fullUrl(urlPath)

	client := hnc.NewUrlClient()

	resp, err := client.Get(url)
	if err != nil {
		return nil, err
	}

	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return nil, err
	}

	err = json.Unmarshal(body, &stories)
	if err != nil {
		return nil, err
	}

	return
}
