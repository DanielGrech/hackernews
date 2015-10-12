//
//  Alamofire+Extensions.swift
//  hackernews
//
//  Created by Daniel Grech on 12/10/2015.
//  Copyright © 2015 DGSD. All rights reserved.
//

import Alamofire
import SwiftyJSON

extension Response {
    
    func error() -> NSError? {
        if (self.result.isFailure) {
            return self.result.error as? NSError
        } else {
            return nil
        }
    }
    
    func json() -> JSON? {
        if (self.result.isSuccess) {
            return JSON(self.result.value as! AnyObject)
        } else {
            return nil
        }
    }
}

extension Request {
    
    func responseAndError<T>(converter: (JSON?) -> T?, handler: (T?, NSError?) -> Void) -> Request {
        return self.responseJSON { response in
            if response.result.isSuccess {
                let val = converter(response.json())
                handler(val, nil)
            } else {
                handler(nil, response.error())
            }
        }
    }
}