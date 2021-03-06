package hackernews

import (
	"strings"
)

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
	By           string     `json:"author"`
	ID           int        `json:"id"`
	Parent       int        `json:"parent_id,omitempty"`
	Kids         []int      `json:"comment_ids,omitempty"`
	Score        int        `json:"score"`
	Time         int        `json:"time"`
	Title        string     `json:"title"`
	Text         string     `json:"text" datastore:",noindex"`
	Type         string     `json:"type"`
	URL          string     `json:"url"`
	Parts        []int      `json:"parts,omitempty"`
	CommentCount int        `json:"comment_count"`
	Comments     []*Comment `json:"comments,omitempty" datastore:"-"`
	Deleted      bool       `json:"deleted,omitempty"`
	Dead         bool       `json:"dead,omitempty"`
}

type Comment struct {
	By           string     `json:"author"`
	ID           int        `json:"id"`
	Kids         []int      `json:"comment_ids,omitempty"`
	Parent       int        `json:"parent"`
	Text         string     `json:"text" datastore:",noindex"`
	Time         int        `json:"time"`
	CommentCount int        `json:"comment_count"`
	Comments     []*Comment `json:"comments,omitempty" datastore:"-"`
	Deleted      bool       `json:"deleted,omitempty"`
	Dead         bool       `json:"dead,omitempty"`
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

func (i item) Text() string {
	s, _ := i["text"].(string)
	return s
}

func (i item) Time() int {
	s, _ := i["time"].(float64)
	return int(s)
}

func (i item) Title() string {
	s, _ := i["title"].(string)
	return strings.Replace(s, "\t", " ", -1)
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

func (i item) Deleted() bool {
	s, _ := i["deleted"].(bool)
	return s
}

func (i item) Dead() bool {
	s, _ := i["dead"].(bool)
	return s
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
	s.Type = i.Type()
	s.Parts = i.Parts()
	s.Parent = i.Parent()
	s.CommentCount = i.Descendants()
	s.Deleted = i.Deleted()
	s.Dead = i.Dead()
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
	c.Deleted = i.Deleted()
	c.Dead = i.Dead()
	return c
}

func (story *Story) PracticallyEquals(other *Story) bool {
	return story.ID == other.ID && story.By == story.By && other.Title == other.Title &&
		story.URL == other.URL && story.Dead == other.Dead && story.Deleted == other.Deleted &&
		story.CommentCount == other.CommentCount && story.Score == other.Score
}
