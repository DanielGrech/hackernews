//
//  Story.swift
//  hackernews
//
//  Created by Daniel Grech on 12/10/2015.
//  Copyright © 2015 DGSD. All rights reserved.
//

import Foundation

enum StoryType {
    case Story
    case Job
    case Poll
    case Poll_Answer
}

struct Story {
    
    var id: Int64 = -1
    var time: Int64 = -1
    var type: StoryType = StoryType.Story
    var parentId: Int64 = -1
    var author: String? = nil
    var title: String? = nil
    var text: String? = nil
    var url: String? = nil
    var commentCount: Int32 = -1
    var score: Int32 = -1
    var pollAnswers: [Int64] = []
    var commentIds: [Int64] = []
    var comments: [Comment] = []
    var deleted: Bool
    var dead: Bool
    var dateRetrieved: Int64 = NSDate.currentTimeInMillis()
    
    func hasComments() -> Bool {
        return !commentIds.isEmpty
    }
    
    func hasCommentsToLoad() -> Bool {
        return hasComments() && comments.isEmpty
    }
    
}