//
//  UIView+extensions.swift
//  hackernews
//
//  Created by Daniel Grech on 13/10/2015.
//  Copyright © 2015 DGSD. All rights reserved.
//

import UIKit

extension NSLayoutConstraint {
    static func equals(fromView fromView: UIView, toView: UIView, attr: NSLayoutAttribute) -> NSLayoutConstraint {
        return NSLayoutConstraint(item: fromView,
            attribute: attr,
            relatedBy: .Equal,
            toItem: toView,
            attribute: attr,
            multiplier: 1,
            constant: 0
        )
    }
}

extension UIView {
    
    func alignLeading(otherView: UIView) {
        self.addConstraint(NSLayoutConstraint.equals(fromView: self, toView: otherView, attr: .Leading))
    }
    
    func alignTop(otherView: UIView) {
        self.addConstraint(NSLayoutConstraint.equals(fromView: self, toView: otherView, attr: .Top))
    }
    
    func alignBottom(otherView: UIView) {
        self.addConstraint(NSLayoutConstraint.equals(fromView: self, toView: otherView, attr: .Bottom))
    }
    
    func alignTrailing(otherView: UIView) {
        self.addConstraint(NSLayoutConstraint.equals(fromView: self, toView: otherView, attr: .Trailing))
    }
    
    func matchToSize(otherView: UIView) {
        otherView.alignLeading(self)
        otherView.alignTrailing(self)
        otherView.alignTop(self)
        otherView.alignBottom(self)
    }
}
