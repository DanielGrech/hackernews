package hackernews

import (
	"appengine"
	"appengine/memcache"
	"strconv"
)

const cacheKeyTopStories = "top_stories"
const cacheKeyNewStories = "new_stories"
const cacheKeyStoryPrefix = "story_"

type Cache struct {
	context appengine.Context
}

func NewCache(c appengine.Context) *Cache {
	var cache Cache
	cache.context = c
	return &cache
}

func (cache *Cache) GetStory(id int) (*Story, error) {
	var cacheEntry Story
	cacheKey := cacheKeyStoryPrefix + strconv.Itoa(id)
	_, err := memcache.Gob.Get(cache.context, cacheKey, &cacheEntry)
	return &cacheEntry, err
}

func (cache *Cache) SetStory(story *Story) {
	item := &memcache.Item{
		Key:    cacheKeyStoryPrefix + strconv.Itoa(story.ID),
		Object: *story,
	}

	if err := memcache.Gob.Set(cache.context, item); err != nil {
		cache.context.Warningf("Error saving story to memcache: %v", err)
	}
}

func (cache *Cache) GetTopStories() []int {
	return cache.getIdsFromCache(cacheKeyTopStories)
}

func (cache *Cache) SetTopStories(storyIds []int) {
	cache.putIdsInCache(storyIds, cacheKeyTopStories)
}

func (cache *Cache) GetNewStories() []int {
	return cache.getIdsFromCache(cacheKeyNewStories)
}

func (cache *Cache) SetNewStories(storyIds []int) {
	cache.putIdsInCache(storyIds, cacheKeyNewStories)
}

func (cache *Cache) putIdsInCache(ids []int, cacheKey string) {
	item := &memcache.Item{
		Key:    cacheKey,
		Object: ids,
	}

	if err := memcache.Gob.Set(cache.context, item); err != nil {
		cache.context.Warningf("Error saving story ids to memcache: %v", err)
	}
}

func (cache *Cache) getIdsFromCache(cacheKey string) []int {
	var cacheEntry []int
	if _, err := memcache.Gob.Get(cache.context, cacheKey, &cacheEntry); err != nil {
		if err != memcache.ErrCacheMiss {
			cache.context.Warningf("Error getting story keys from memcache: %v", err)
		}
		return nil
	} else {
		return cacheEntry
	}
}
