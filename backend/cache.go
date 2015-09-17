package hackernews

import (
	"appengine"
	"appengine/memcache"
	"strconv"
)

const cacheKeyTopStories = "top_stories"
const cacheKeyStoryPrefix = "story_"

type Cache struct {
	context appengine.Context
}

func NewCache(c appengine.Context) *Cache {
	var cache Cache
	cache.context = c
	return &cache
}

func (cache *Cache) GetStory(id int) (Story, error) {
	var cacheEntry Story
	cacheKey := cacheKeyStoryPrefix + strconv.Itoa(id)
	_, err := memcache.Gob.Get(cache.context, cacheKey, &cacheEntry)
	return cacheEntry, err
}

func (cache *Cache) SetStory(story Story) {
	item := &memcache.Item{
		Key:    cacheKeyStoryPrefix + strconv.Itoa(story.ID),
		Object: story,
	}

	if err := memcache.Gob.Set(cache.context, item); err != nil {
		cache.context.Warningf("Error saving story to memcache: %v", err)
	}
}

func (cache *Cache) GetTopStories() []int {
	var cacheEntry []int
	if _, err := memcache.Gob.Get(cache.context, cacheKeyTopStories, &cacheEntry); err != nil {
		if err != memcache.ErrCacheMiss {
			cache.context.Warningf("Error getting top stories from memcache: %v", err)
		}
		return nil
	} else {
		return cacheEntry
	}
}

func (cache *Cache) SetTopStories(storyIds []int) {
	item := &memcache.Item{
		Key:    cacheKeyTopStories,
		Object: storyIds,
	}

	if err := memcache.Gob.Set(cache.context, item); err != nil {
		cache.context.Warningf("Error saving story ids to memcache: %v", err)
	}
}
