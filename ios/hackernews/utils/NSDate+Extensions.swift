//
//  NSDate+Extensions.swift
//  hackernews
//
//  Created by Daniel Grech on 12/10/2015.
//  Copyright © 2015 DGSD. All rights reserved.
//

import Foundation

extension NSDate {
    
    public static func currentTimeInMillis() -> Int64 {
        return Int64(NSDate().timeIntervalSince1970 * 1000)
    }
}