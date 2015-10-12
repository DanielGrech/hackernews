//
//  Presenter.swift
//  hackernews
//
//  Created by Daniel Grech on 12/10/2015.
//  Copyright © 2015 DGSD. All rights reserved.
//

import Foundation

class Presenter {
   
    lazy var dataSource: DataSource = {
        return AppDelegate.instance.dataSource
    }()
    
    func viewDidLoad() {
        
    }
}

protocol MvpView {
    // Marker interface
}