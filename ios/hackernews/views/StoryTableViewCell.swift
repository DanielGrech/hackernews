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
    
    let storyInfo: UILabel
    
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        storyTitle = UILabel()
        storyTitle.translatesAutoresizingMaskIntoConstraints = false
        storyTitle.textColor = UIColor.primaryText
        storyTitle.font = UIFont.storyTitleFont
        storyTitle.lineBreakMode = .ByWordWrapping
        storyTitle.numberOfLines = 0
        
        storyInfo = UILabel()
        storyInfo.translatesAutoresizingMaskIntoConstraints = false
        storyInfo.textColor = UIColor.secondaryText
        storyInfo.font = UIFont.storyDetail
        storyInfo.lineBreakMode = .ByTruncatingTail
        storyInfo.numberOfLines = 1
        
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        
        self.contentView.addSubview(self.storyTitle)
        self.contentView.addSubview(self.storyInfo)
        
        self.storyTitle.snp_makeConstraints { make in
            make.top.equalTo(self.contentView).inset(UIView.DEFAULT_PADDING_SMALL)
            make.leading.equalTo(self.contentView).inset(UIView.DEFAULT_PADDING)
            make.trailing.equalTo(self.contentView).inset(UIView.DEFAULT_PADDING)
        }
        
        self.storyInfo.snp_makeConstraints { make in
            make.leading.equalTo(self.contentView).inset(UIView.DEFAULT_PADDING)
            make.trailing.equalTo(self.contentView).inset(UIView.DEFAULT_PADDING)
            make.top.greaterThanOrEqualTo(self.storyTitle.snp_bottom)
        }
        
        self.contentView.snp_makeConstraints { make in
            make.leading.equalTo(self)
            make.trailing.equalTo(self)
            make.top.equalTo(self)
            make.bottom.equalTo(self.storyInfo).offset(UIView.DEFAULT_PADDING_SMALL)
        }
        
        self.setNeedsLayout()
    }
    
    required init?(coder: NSCoder) {
        fatalError("Use with Nib not supported")
    }
    
    func setStory(story: Story) {
        self.storyTitle.text = story.title
        self.storyInfo.text = story.getStoryListDetailText()
    }
}