//
//  MainViewController.swift
//  hackernews
//
//  Created by Daniel Grech on 1/10/2015.
//  Copyright © 2015 DGSD. All rights reserved.
//

import UIKit

enum StoryListType {
    case Top
    case New
    case AskHn
    case ShowHn
    case Jobs
}

class StoryListViewController: UIViewController {
    
    var listType: StoryListType
    
    init(type: StoryListType) {
        listType = type
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("Use from storyboard not supported")
        
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.view.backgroundColor = UIColor.whiteColor()
        self.title = {
            switch (listType) {
            case StoryListType.Top: return "page_title_top".localized
            case StoryListType.New: return "page_title_new".localized
            case StoryListType.AskHn: return "page_title_ask".localized
            case StoryListType.ShowHn: return "page_title_show".localized
            case StoryListType.Jobs: return "page_title_jobs".localized
            }}()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
}

