//
//  NetworkDataSource.swift
//  hackernews
//
//  Created by Daniel Grech on 12/10/2015.
//  Copyright © 2015 DGSD. All rights reserved.
//

import Alamofire
import SwiftyJSON

class NetworkDataSource {
    
    static let baseURLString = "https://hackernews-1082.appspot.com"
    
    func getTopStories(handler: ([Story]?, NSError?) -> Void ) {
        get("/top").responseAndError({ $0?.toStoryArray() } , handler: handler)
    }
    
    private func get(path: String) -> Request {
        return Alamofire.request(.GET, NetworkDataSource.baseURLString + path)
    }
}