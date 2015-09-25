package hackernews

import (
	"appengine"
	"appengine/memcache"
	"strconv"
)

const cacheKeyTopStories = "top_stories"
const cacheKeyNewStories = "new_stories"
const cacheKeyAskStories = "ask_stories"
const cacheKeyShowStories = "show_stories"
const cacheKeyJobStories = "job_stories"
const cacheKeyStoryPrefix = "story_"
const cacheKeyCommentPrefix = "comment_"

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

func (cache *Cache) GetComment(id int) (*Comment, error) {
	var cacheEntry Comment
	cacheKey := cacheKeyCommentPrefix + strconv.Itoa(id)
	_, err := memcache.Gob.Get(cache.context, cacheKey, &cacheEntry)
	return &cacheEntry, err
}

func (cache *Cache) SetComment(comment *Comment) {
	item := &memcache.Item{
		Key:    cacheKeyCommentPrefix + strconv.Itoa(comment.ID),
		Object: *comment,
	}

	if err := memcache.Gob.Set(cache.context, item); err != nil {
		cache.context.Warningf("Error saving comment to memcache: %v", err)
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

func (cache *Cache) GetAskStories() []int {
	return cache.getIdsFromCache(cacheKeyAskStories)
}

func (cache *Cache) SetAskStories(storyIds []int) {
	cache.putIdsInCache(storyIds, cacheKeyAskStories)
}

func (cache *Cache) GetShowStories() []int {
	return cache.getIdsFromCache(cacheKeyShowStories)
}

func (cache *Cache) SetShowStories(storyIds []int) {
	cache.putIdsInCache(storyIds, cacheKeyShowStories)
}

func (cache *Cache) GetJobStories() []int {
	return cache.getIdsFromCache(cacheKeyJobStories)
}

func (cache *Cache) SetJobStories(storyIds []int) {
	cache.putIdsInCache(storyIds, cacheKeyJobStories)
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
