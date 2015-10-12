//
//  MainTabBarController.swift
//  hackernews
//
//  Created by Daniel Grech on 1/10/2015.
//  Copyright © 2015 DGSD. All rights reserved.
//

import UIKit
import MMDrawerController

class MainDrawerController: MMDrawerController {
    convenience init() {
        let navController = UINavigationController(rootViewController: StoryListViewController(pageType: PageType.Top))
        self.init(centerViewController: navController, leftDrawerViewController: DrawerViewController())
        
        openDrawerGestureModeMask = MMOpenDrawerGestureMode.PanningCenterView;
        closeDrawerGestureModeMask = MMCloseDrawerGestureMode.PanningCenterView;
    }
    
}
