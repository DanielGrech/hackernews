//
//  MainViewController.swift
//  hackernews
//
//  Created by Daniel Grech on 1/10/2015.
//  Copyright © 2015 DGSD. All rights reserved.
//

import UIKit



class StoryListViewController: PresentableViewController<StoryListPresenter>, StoryListMvpView {
    
    init(pageType: PageType) {
        super.init(presenter: StoryListPresenter(pageType: pageType) )
        presenter.view = self
    }
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.view.backgroundColor = UIColor.whiteColor()
    }
    
    
    func showError(err: NSError) {
        
    }
    
    func showStories(stories: [Story]) {
        
    }
    
    func showPageTitle(title: String) {
        self.title = title
    }
}

