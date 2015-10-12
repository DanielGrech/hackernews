//
//  Story+Extensions.swift
//  hackernews
//
//  Created by Daniel Grech on 12/10/2015.
//  Copyright © 2015 DGSD. All rights reserved.
//

import SwiftyJSON

extension JSON {
    
    func toComment() -> Comment {
        return Comment(
            id: self["id"].int64Value,
            time: self["time"].int64Value,
            author: self["author"].stringValue,
            text: self["text"].stringValue,
            parentId: self["parent_id"].int64Value,
            commentCount: self["comment_count"].int32Value,
            commentIds: self["comment_ids"].toIdArray().sort(<),
            comments: self["comments"].toCommentArray().sort { $0.time < $1.time },
            deleted: self["deleted"].boolValue,
            dead: self["dead"].boolValue
        )
    }
    
    func toStory() -> Story {
        return Story(
            id: self["id"].int64Value,
            time: self["time"].int64Value,
            type: JSON.convertType(self["type"].stringValue),
            parentId: self["parent_id"].int64Value,
            author: self["author"].stringValue,
            title: self["title"].stringValue,
            text: self["text"].stringValue,
            url: self["url"].stringValue,
            commentCount: self["comment_count"].int32Value,
            score: self["score"].int32Value,
            pollAnswers: self["parts"].toIdArray(),
            commentIds: self["comment_ids"].toIdArray().sort(<),
            comments: self["comments"].toCommentArray().sort { $0.time < $1.time },
            deleted: self["deleted"].boolValue,
            dead: self["dead"].boolValue,
            dateRetrieved: NSDate.currentTimeInMillis()
        )
    }
    
    func toStoryArray() -> [Story] {
        let arr = self.arrayValue
        var retval = [Story]()
        for storyJson in arr {
            retval.append(storyJson.toStory())
        }
        return retval
    }
    
    func toCommentArray() -> [Comment] {
        let arr = self.arrayValue
        var retval = [Comment]()
        for commentJson in arr {
            retval.append(commentJson.toComment())
        }
        return retval
    }
    
    func toIdArray() -> [Int64] {
        let arr = self.arrayValue
        var retval = [Int64]()
        for idVal in arr {
            retval.append(idVal.int64Value)
        }
        return retval
    }
    
    private static func convertType(typeAsString: String?) -> StoryType {
        if typeAsString != nil {
            switch (typeAsString!) {
            case "story": return StoryType.Story
            case "job": return StoryType.Job
            case "poll": return StoryType.Poll
            case "pollopt": return StoryType.Poll_Answer
            default: break
            }
        }
        
        return StoryType.Story
    }
}