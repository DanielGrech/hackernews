//
//  MainViewController.swift
//  hackernews
//
//  Created by Daniel Grech on 1/10/2015.
//  Copyright © 2015 DGSD. All rights reserved.
//

import UIKit
import SnapKit

class StoryListViewController: PresentableViewController<StoryListPresenter>, StoryListMvpView, UITableViewDelegate {
    
    let tableView: UITableView
    let refreshControl: UIRefreshControl
    let tableDataSource: StoryListViewDataSource
    
    init(pageType: PageType) {
        tableView = UITableView()
        refreshControl = UIRefreshControl()
        tableDataSource = StoryListViewDataSource()
        
        tableView.translatesAutoresizingMaskIntoConstraints = false
        
        super.init(presenter: StoryListPresenter(pageType: pageType) )
        presenter.view = self
        
        tableView.delegate = self
        tableView.dataSource = tableDataSource
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = UIColor.whiteColor()
        
        refreshControl.tintColor = UIColor.accent
        refreshControl.addTarget(self.presenter, action: Selector("onRefreshRequested"), forControlEvents: .ValueChanged)
        
        tableView.addSubview(refreshControl)
        view.addSubview(tableView)
        
        tableView.snp_makeConstraints { make in
            make.edges.equalTo(self.view)
        }
        
        tableView.estimatedRowHeight = 80
        tableView.rowHeight = UITableViewAutomaticDimension
        tableView.separatorStyle = UITableViewCellSeparatorStyle.SingleLine
        tableView.separatorColor = UIColor.borderColor
        tableView.separatorInset = UIEdgeInsetsMake(0, UIView.DEFAULT_PADDING_SMALL, 0, UIView.DEFAULT_PADDING_SMALL)
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

