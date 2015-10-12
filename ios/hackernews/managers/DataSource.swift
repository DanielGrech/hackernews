//
//  DataSource.swift
//  hackernews
//
//  Created by Daniel Grech on 12/10/2015.
//  Copyright © 2015 DGSD. All rights reserved.
//

import Foundation

class DataSource {
    
    var networkDataSource: NetworkDataSource
    
    init(networkDataSource: NetworkDataSource) {
        self.networkDataSource = networkDataSource
    }
    
    func getTopStories(handler: ([Story]?, NSError?) -> Void ) {
       return networkDataSource.getTopStories(handler)
    }
}