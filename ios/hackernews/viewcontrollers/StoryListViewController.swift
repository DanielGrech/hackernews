//
//  MainViewController.swift
//  hackernews
//
//  Created by Daniel Grech on 1/10/2015.
//  Copyright © 2015 DGSD. All rights reserved.
//

import UIKit

class StoryListViewController: PresentableViewController<StoryListPresenter>, StoryListMvpView, UITableViewDelegate {
    
    let tableView: UITableView
    let refreshControl: UIRefreshControl
    let tableDataSource: StoryListViewDataSource
    
    init(pageType: PageType) {
        tableView = UITableView()
        refreshControl = UIRefreshControl()
        tableDataSource = StoryListViewDataSource()
        
        super.init(presenter: StoryListPresenter(pageType: pageType) )
        presenter.view = self
        
        tableView.delegate = self
        tableView.dataSource = tableDataSource
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.view.backgroundColor = UIColor.whiteColor()
        
        refreshControl.tintColor = UIColor.accent
        refreshControl.addTarget(self.presenter, action: Selector("onRefreshRequested"), forControlEvents: .ValueChanged)
        
        tableView.frame = CGRectMake(0, 0, UIScreen.width(), UIScreen.height())
        tableView.autoresizingMask = [.FlexibleWidth, .FlexibleHeight, .FlexibleTopMargin, .FlexibleBottomMargin]
        tableView.addSubview(refreshControl)
        self.view.addSubview(tableView)
    }
    
    func showError(err: NSError) {
        refreshControl.endRefreshing()
    }
    
    func showStories(stories: [Story]) {
        refreshControl.endRefreshing()
        
        tableDataSource.stories.removeAll()
        tableDataSource.stories += stories
        tableView.reloadData()
    }
    
    func showPageTitle(title: String) {
        self.title = title
    }    
}

