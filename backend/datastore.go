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

func (db *Db) DeleteStoriesNotIn(ids []int) {
	allKeys := db.getAllStoryKeys()

	var keysToKeep = make([]*datastore.Key, len(ids))
	for _, id := range ids {
		keysToKeep = append(keysToKeep, db.keyForStory(id))
	}

	keysToRemove := db.findDiff(allKeys, keysToKeep)

	db.context.Debugf("Found keys to remove: %v", keysToRemove)

	if len(keysToRemove) > 0 {
		// Delete comments for story
		for _, key := range keysToRemove {
			story := new(Story)
			if err := datastore.Get(db.context, key, story); err != nil {
				db.context.Errorf("Error getting story: %v", err)
			} else {
				db.deleteComments(story.Kids)
			}
		}

		// Delete our story items
		if err := datastore.DeleteMulti(db.context, keysToRemove); err != nil {
			db.context.Errorf("Error deleting old stories: %v", err)
		}
	}
}

func (db *Db) getAllStoryKeys() []*datastore.Key {
	q := datastore.NewQuery("Story").KeysOnly()
	keys, _ := q.GetAll(db.context, nil)
	return keys
}

func (db *Db) SaveStory(story *Story) error {
	key := db.keyForStory(story.ID)
	_, err := datastore.Put(db.context, key, story)
	return err
}

func (db *Db) GetStory(id int) (*Story, error) {
	story := new(Story)
	key := db.keyForStory(id)
	err := datastore.Get(db.context, key, story)
	if err == nil {
		for _, commentId := range story.Kids {
			comment, cErr := db.GetComment(commentId)
			if cErr == nil {
				story.Comments = append(story.Comments, *comment)
			}
		}
	}
	return story, err
}

func (db *Db) GetStories(ids []int) ([]*Story, error) {
	keys := make([]*datastore.Key, 0, len(ids))

	for _, id := range ids {
		keys = append(keys, db.keyForStory(id))
	}

	stories := make([]*Story, len(ids))
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
		for _, story := range stories {
			for _, commentId := range story.Kids {
				comment, cErr := db.GetComment(commentId)
				if cErr == nil {
					story.Comments = append(story.Comments, *comment)
				}
			}
		}

		return stories, nil
	}
}

func (db *Db) SaveComment(comment *Comment) error {
	key := db.keyForComment(comment.ID)
	_, err := datastore.Put(db.context, key, comment)
	return err
}

func (db *Db) GetComment(id int) (*Comment, error) {
	comment := new(Comment)
	key := db.keyForComment(id)
	err := datastore.Get(db.context, key, comment)

	if err == nil {
		for _, commentId := range comment.Kids {
			subComment, cErr := db.GetComment(commentId)
			if cErr == nil {
				comment.Comments = append(comment.Comments, *subComment)
			}
		}
	}

	return comment, err
}

func (db *Db) deleteComments(commentIds []int) {
	db.context.Debugf("Going to delete comments: %v", commentIds)
	for _, commentId := range commentIds {
		comment, err := db.GetComment(commentId)
		if err == nil {
			db.deleteComments(comment.Kids)
		}
		datastore.Delete(db.context, db.keyForComment(commentId))
	}
}

func (db *Db) findDiff(allKeys []*datastore.Key, keysToKeep []*datastore.Key) []*datastore.Key {
	w := 0
loop:
	for _, x := range allKeys {
		for _, y := range keysToKeep {
			if x.Equal(y) {
				continue loop
			}
		}
		allKeys[w] = x
		w++
	}
	return allKeys[:w]
}

func (db *Db) keyForComment(id int) *datastore.Key {
	return datastore.NewKey(
		db.context,
		"Comment",
		"comment_"+strconv.Itoa(id),
		0,
		nil,
	)
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
