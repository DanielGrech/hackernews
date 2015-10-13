//
//  StoryListPresenter.swift
//  hackernews
//
//  Created by Daniel Grech on 12/10/2015.
//  Copyright © 2015 DGSD. All rights reserved.
//

import Foundation

class StoryListPresenter : Presenter {
    
    let pageType: PageType
    
    var view: StoryListMvpView!
    
    init(pageType: PageType) {
        self.pageType = pageType
    }
    
    // MARK: Presenter methods
    
    override func viewDidLoad() {
        view.showPageTitle(getPageTitle())
        refreshData(false)
    }
    
    func onRefreshRequested() {
        refreshData(true)
    }
    
    private func refreshData(skipCache: Bool) {
        dataSource.getTopStories { stories, error in
            if (error != nil) {
                print("Got error: ", error)
                self.view.showError(error!)
            } else {
                print("Got stories: ", stories)
                self.view.showStories(stories!)
            }
        }
    }
    
    private func getPageTitle() -> String! {
        return {
            switch (pageType) {
            case PageType.Top: return "page_title_top".localized
            case PageType.New: return "page_title_new".localized
            case PageType.AskHn: return "page_title_ask".localized
            case PageType.ShowHn: return "page_title_show".localized
            case PageType.Jobs: return "page_title_jobs".localized
            }}()
    }
}

protocol StoryListMvpView {
    
    func showPageTitle(title: String)
    
    func showError(err: NSError)
    
    func showStories(stories: [Story])
}