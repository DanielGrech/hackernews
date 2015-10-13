//
//  UIColor+Extensions.swift
//  hackernews
//
//  Created by Daniel Grech on 1/10/2015.
//  Copyright © 2015 DGSD. All rights reserved.
//

import UIKit

extension UIColor {
    
    @nonobjc public static let primary = UIColor(rgb: 0x3D3D3D)
    @nonobjc public static let primaryDark = UIColor(rgb: 0x313131)
    @nonobjc public static let accent = UIColor(rgb: 0xFF8937)
    @nonobjc public static let primaryText = UIColor(rgb: 0xDD000000)
    @nonobjc public static let secondaryText = UIColor(rgb: 0x727272)
    
    convenience init(rgb: UInt, alphaVal:CGFloat = 1) {
        self.init(
            red: CGFloat((rgb & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgb & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgb & 0x0000FF) / 255.0,
            alpha: CGFloat(alphaVal)
        )
    }
    
}