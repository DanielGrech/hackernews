//
//  UIScreen+Extensions.swift
//  hackernews
//
//  Created by Daniel Grech on 13/10/2015.
//  Copyright © 2015 DGSD. All rights reserved.
//

import UIKit

extension UIScreen {
    
    static func width() -> CGFloat {
        return UIScreen.mainScreen().bounds.width
    }
    
    static func height() -> CGFloat {
        return UIScreen.mainScreen().bounds.height
    }
}