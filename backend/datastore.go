package hackernews

import (
	"strconv"

	"appengine"
	"appengine/datastore"
)

type Db struct {
	context appengine.Context
}

func NewDb(c appengine.Context) *Db {
	var db Db
	db.context = c
	return &db
}

func (db *Db) SaveStory(story Story) error {
	key := db.keyForStory(story.ID)
	_, err := datastore.Put(db.context, key, &story)
	return err
}

func (db *Db) GetStory(id int) (Story, error) {
	var story Story
	key := db.keyForStory(story.ID)
	err := datastore.Get(db.context, key, &story)
	return story, err
}

func (db *Db) GetStories(ids []int) ([]Story, error) {
	keys := make([]*datastore.Key, 0, len(ids))

	for _, id := range ids {
		keys = append(keys, db.keyForStory(id))
	}

	stories := make([]Story, len(ids))
	err := datastore.GetMulti(db.context, keys, stories)
	if err_entries, ok := err.(appengine.MultiError); ok {
		removed := 0
		for i, err_entry := range err_entries {
			if err_entry != nil {
				stories = append(stories[:i-removed], stories[i+1-removed:]...)
				removed++
			}
		}
	}

	if len(stories) == 0 {
		return nil, err
	} else {
		return stories, nil
	}
}

func (db *Db) keyForStory(id int) *datastore.Key {
	return datastore.NewKey(
		db.context,
		"Story",
		"story_"+strconv.Itoa(id),
		0,
		nil,
	)
}
