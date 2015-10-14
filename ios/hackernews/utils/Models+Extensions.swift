//
//  Models+Extensions.swift
//  hackernews
//
//  Created by Daniel Grech on 14/10/2015.
//  Copyright © 2015 DGSD. All rights reserved.
//

import Foundation

private func getDateFormatter() -> NSDateFormatter {
    let formatter = NSDateFormatter()
    formatter.doesRelativeDateFormatting = true
    formatter.dateStyle = .MediumStyle
    formatter.timeStyle = .ShortStyle
    return formatter
}

private let DATE_FORMATTER = getDateFormatter()

extension Story {
    
    func getStoryListDetailText() -> String {
        let votesText = self.score == 0 ? "No votes" : (self.score == 1 ? "1 vote" : "\(self.score) votes")
        let commentsText = self.commentCount == 0 ? "No comments" : (self.commentCount == 1 ? "1 comment" : "\(self.commentCount) comments")
        let authorText = self.author ?? "Unknown"
        let dateText = DATE_FORMATTER.stringFromDate(NSDate(timeIntervalSince1970: NSTimeInterval(self.time)))
        return String(format:"story_list_item_details_template".localized, votesText, commentsText, authorText, dateText)
    }
}