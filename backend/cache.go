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

const cacheKeyTopStoriesList = "top_stories_list"
const cacheKeyNewStoriesList = "new_stories_list"
const cacheKeyAskStoriesList = "ask_stories_list"
const cacheKeyShowStoriesList = "show_stories_list"
const cacheKeyJobStoriesList = "job_stories_list"

const cacheKeyStoryPrefix = "story_"
const cacheKeyCommentPrefix = "comment_"

var storyIdCache = make(map[string][]int)
var storyListCache = make(map[string][]*Story)
var storyCache = make(map[int]*Story)
var commentCache = make(map[int]*Comment)

type Cache struct {
	context appengine.Context
}

func NewCache(c appengine.Context) *Cache {
	var cache Cache
	cache.context = c
	return &cache
}

func (cache *Cache) GetStory(id int) (*Story, error) {
	memoryCacheEntry := storyCache[id]
	if memoryCacheEntry != nil {
		return memoryCacheEntry, nil
	}

	var cacheEntry Story
	cacheKey := cacheKeyStoryPrefix + strconv.Itoa(id)
	_, err := memcache.Gob.Get(cache.context, cacheKey, &cacheEntry)

	if err == nil {
		storyCache[id] = &cacheEntry
	}

	return &cacheEntry, err
}

func (cache *Cache) SetStory(story *Story) {
	item := &memcache.Item{
		Key:    cacheKeyStoryPrefix + strconv.Itoa(story.ID),
		Object: *story,
	}

	if err := memcache.Gob.Set(cache.context, item); err != nil {
		cache.context.Warningf("Error saving story to memcache: %v", err)
	} else {
		storyCache[story.ID] = story
	}
}

func (cache *Cache) GetComment(id int) (*Comment, error) {
	memoryCacheEntry := commentCache[id]
	if memoryCacheEntry != nil {
		return memoryCacheEntry, nil
	}

	var cacheEntry Comment
	cacheKey := cacheKeyCommentPrefix + strconv.Itoa(id)
	_, err := memcache.Gob.Get(cache.context, cacheKey, &cacheEntry)

	if err == nil {
		commentCache[id] = &cacheEntry
	}

	return &cacheEntry, err
}

func (cache *Cache) SetComment(comment *Comment) {
	item := &memcache.Item{
		Key:    cacheKeyCommentPrefix + strconv.Itoa(comment.ID),
		Object: *comment,
	}

	if err := memcache.Gob.Set(cache.context, item); err != nil {
		cache.context.Warningf("Error saving comment to memcache: %v", err)
	} else {
		commentCache[comment.ID] = comment
	}
}

func (cache *Cache) GetStories(typeKey string) []*Story {
	memoryCacheEntry := storyListCache[typeKey]
	if len(memoryCacheEntry) > 0 {
		return memoryCacheEntry
	}

	var cacheEntry []*Story
	if _, err := memcache.Gob.Get(cache.context, typeKey, &cacheEntry); err != nil {
		if err != memcache.ErrCacheMiss {
			cache.context.Warningf("Error getting stories from memcache: %v", err)
		}
		return nil
	} else {
		storyListCache[typeKey] = cacheEntry
		return cacheEntry
	}
}

func (cache *Cache) SetStories(typeKey string, stories []*Story) {
	item := &memcache.Item{
		Key:    typeKey,
		Object: stories,
	}

	if err := memcache.Gob.Set(cache.context, item); err != nil {
		cache.context.Warningf("Error saving stories to memcache: %v", typeKey, err)
	} else {
		storyListCache[typeKey] = stories
	}
}

func (cache *Cache) ClearStories(typeKey string) {
	delete(storyListCache, typeKey)

	if err := memcache.Delete(cache.context, typeKey); err != nil && err != memcache.ErrCacheMiss {
		cache.context.Warningf("Error clearing stories from memcache: %v", typeKey, err)
	}
}

func (cache *Cache) ClearStory(storyId int) {
	delete(storyCache, storyId)

	key := cacheKeyStoryPrefix + strconv.Itoa(storyId)
	if err := memcache.Delete(cache.context, key); err != nil && err != memcache.ErrCacheMiss {
		cache.context.Warningf("Error clearing storiy from memcache: %v", key, err)
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
	} else {
		storyIdCache[cacheKey] = ids
	}
}

func (cache *Cache) getIdsFromCache(cacheKey string) []int {
	ids := storyIdCache[cacheKey]
	if len(ids) > 0 {
		return ids
	}

	var cacheEntry []int
	if _, err := memcache.Gob.Get(cache.context, cacheKey, &cacheEntry); err != nil {
		if err != memcache.ErrCacheMiss {
			cache.context.Warningf("Error getting story keys from memcache: %v", err)
		}
		return nil
	} else {
		storyIdCache[cacheKey] = cacheEntry
		return cacheEntry
	}
}
