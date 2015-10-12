//
//  Comment.swift
//  hackernews
//
//  Created by Daniel Grech on 12/10/2015.
//  Copyright © 2015 DGSD. All rights reserved.
//

import Foundation

struct Comment {
    var id: Int64 = -1
    var time: Int64 = -1
    var author: String? = nil
    var text: String? = nil
    var parentId: Int64 = -1
    var commentCount: Int32
    var commentIds: [Int64] = []
    var comments: [Comment] = []
    var deleted: Bool
    var dead: Bool
    
    func hasBeenRemoved() -> Bool {
        return deadOrDeleted() && commentIds.isEmpty
    }
    
    func deadOrDeleted() -> Bool {
        return dead || deleted
    }
    
}