//
//  UIViewController+Extensions.swift
//  hackernews
//
//  Created by Daniel Grech on 12/10/2015.
//  Copyright © 2015 DGSD. All rights reserved.
//

import UIKit

extension UIViewController {
    
    func useableScreenHeight() -> CGFloat {
        let statusBarHeight = UIApplication.sharedApplication().statusBarFrame.size.height
        let navBarHeight = self.navigationController?.navigationBar.frame.size.height ?? 0
        
        return UIScreen.height() - navBarHeight - statusBarHeight
    }
}

