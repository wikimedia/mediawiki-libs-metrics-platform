//
//  File.swift
//  
//
//  Created by Sam Smith on 13/01/2023.
//

import Foundation

struct SamplingConfig: Decodable {
    var rate: Double?
    var unit: String? = "session"
}
