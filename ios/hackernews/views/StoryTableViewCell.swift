//
//  StoryTableViewCell.swift
//  hackernews
//
//  Created by Daniel Grech on 13/10/2015.
//  Copyright © 2015 DGSD. All rights reserved.
//

import UIKit

class StoryTableViewCell: UITableViewCell {
    
    let storyTitle: UILabel
    
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        storyTitle = UILabel()
        storyTitle.textColor = UIColor.primaryText
        storyTitle.translatesAutoresizingMaskIntoConstraints = false
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        
        self.contentView.addSubview(storyTitle)
        
        self.contentView.alignLeading(self.storyTitle)
        self.contentView.alignTop(self.storyTitle)
        self.contentView.alignTrailing(self.storyTitle)
    }
    
    required init?(coder: NSCoder) {
        fatalError("Use with Nib not supported")
    }
    
    func setStory(story: Story) {
        self.storyTitle.text = story.title
        self.detailTextLabel?.text = story.author
    }
    
    override func gestureRecognizerShouldBegin(gestureRecognizer: UIGestureRecognizer) -> Bool {
        return false
    }
}