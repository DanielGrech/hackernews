package hackernews

import (
	"encoding/json"
)

type TextAsBytes struct {
	Text string
}

func (t TextAsBytes) MarshalJSON() ([]byte, error) {
	return json.Marshal(t.Text)
}

func (t *TextAsBytes) UnmarshalJSON(value []byte) error {
	t.Text = string(value)

	return nil
}

type Item interface {
	By() string
	ID() int
	Kids() []int
	Parent() int
	Parts() []int
	Score() int
	Text() string
	Time() int
	Title() string
	Type() string
	URL() string
	Decendants() int
}

type Story struct {
	By           string      `json:"author"`
	ID           int         `json:"id"`
	Kids         []int       `json:"comment_ids"`
	Score        int         `json:"score"`
	Time         int         `json:"time"`
	Title        string      `json:"title"`
	Text         TextAsBytes `json:"text"`
	URL          string      `json:"url"`
	CommentCount int         `json:"comment_count"`
	Comments     []Comment   `json:"comments,omitempty" datastore:"-"`
}

type Comment struct {
	By           string      `json:"author"`
	ID           int         `json:"id"`
	Kids         []int       `json:"comment_ids"`
	Parent       int         `json:"parent"`
	Text         TextAsBytes `json:"text"`
	Time         int         `json:"time"`
	CommentCount int         `json:"comment_count"`
	Comments     []Comment   `json:"comments,omitempty" datastore:"-"`
}

type Poll struct {
	By           string      `json:"author"`
	ID           int         `json:"id"`
	Kids         []int       `json:"-"`
	Parts        []int       `json:"parts"`
	Score        int         `json:"score"`
	Text         TextAsBytes `json:"text"`
	Time         int         `json:"time"`
	Title        string      `json:"title"`
	CommentCount int         `json:"comment_count"`
}

type Part struct {
	By     string      `json:"author"`
	ID     int         `json:"id"`
	Parent int         `json:"parent"`
	Score  int         `json:"score"`
	Text   TextAsBytes `json:"text"`
	Time   int         `json:"time"`
}

// item cannot be autogenerated as a struct, as it should not be represented as a struct
// item has varying fields, and not all fields will be returned for any single call
type item map[string]interface{}

func (i item) By() string {
	s, _ := i["by"].(string)
	return s
}

func (i item) ID() int {
	s, _ := i["id"].(float64)
	return int(s)
}

func (i item) Kids() []int {
	kids, _ := i["kids"]
	fsi, _ := kids.([]interface{})
	is := make([]int, len(fsi))
	for i, f := range fsi {
		is[i] = int(f.(float64))
	}
	return is
}

func (i item) Parent() int {
	s, _ := i["parent"].(float64)
	return int(s)
}

func (i item) Parts() []int {
	parts, _ := i["parts"]
	fsi, _ := parts.([]interface{})
	is := make([]int, len(fsi))
	for i, f := range fsi {
		is[i] = int(f.(float64))
	}
	return is
}

func (i item) Score() int {
	s, _ := i["score"].(float64)
	return int(s)
}

func (i item) Text() TextAsBytes {
	s, _ := i["text"].(string)
	return TextAsBytes{s}
}

func (i item) Time() int {
	s, _ := i["time"].(float64)
	return int(s)
}

func (i item) Title() string {
	s, _ := i["title"].(string)
	return s
}

func (i item) Type() string {
	s, _ := i["type"].(string)
	return s
}

func (i item) URL() string {
	s, _ := i["url"].(string)
	return s
}

func (i item) Descendants() int {
	s, _ := i["descendants"].(float64)
	return int(s)
}

// Convert an item to a Story
func (i item) ToStory() Story {
	var s Story
	s.By = i.By()
	s.ID = i.ID()
	s.Kids = i.Kids()
	s.Score = i.Score()
	s.Time = i.Time()
	s.Title = i.Title()
	s.Text = i.Text()
	s.URL = i.URL()
	s.CommentCount = i.Descendants()
	return s
}

// Convert an item to a Comment
func (i item) ToComment() Comment {
	var c Comment
	c.By = i.By()
	c.ID = i.ID()
	c.Kids = i.Kids()
	c.Parent = i.Parent()
	c.Text = i.Text()
	c.Time = i.Time()
	c.CommentCount = len(i.Kids())
	return c
}

// Convert an item to a Poll
func (i item) ToPoll() Poll {
	var p Poll
	p.By = i.By()
	p.ID = i.ID()
	p.Kids = i.Kids()
	p.Parts = i.Parts()
	p.Score = i.Score()
	p.Text = i.Text()
	p.Time = i.Time()
	p.Title = i.Title()
	p.CommentCount = i.Descendants()
	return p
}

// Convert an item to a Part
func (i item) ToPart() Part {
	var p Part
	p.By = i.By()
	p.ID = i.ID()
	p.Parent = i.Parent()
	p.Score = i.Score()
	p.Text = i.Text()
	p.Time = i.Time()
	return p
}
