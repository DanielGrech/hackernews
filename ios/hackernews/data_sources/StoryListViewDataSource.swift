//
//  StoryListViewDataSource.swift
//  hackernews
//
//  Created by Daniel Grech on 13/10/2015.
//  Copyright © 2015 DGSD. All rights reserved.
//

import UIKit

class StoryListViewDataSource: NSObject, UITableViewDataSource {
    
    var CELL_TOKEN: dispatch_once_t = 0
    
    let CELL_IDENTIFIER = "story_cell"
    
    var stories: [Story] = []
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.stories.count
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        dispatch_once(&CELL_TOKEN) {
            tableView.registerClass(StoryTableViewCell.self, forCellReuseIdentifier: self.CELL_IDENTIFIER)
        }
        
        let cell = tableView.dequeueReusableCellWithIdentifier(CELL_IDENTIFIER) as! StoryTableViewCell
        cell.setStory(self.stories[indexPath.row])
        return cell
    }
}
