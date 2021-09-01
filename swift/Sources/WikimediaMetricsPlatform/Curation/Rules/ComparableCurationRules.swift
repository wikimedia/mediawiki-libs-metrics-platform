import Foundation

/**
 * Curation rules to be used with context values whose types conform to Comparable.
 * This is used for context values with numeric types.
 */
struct ComparableCurationRules<T: Comparable & Hashable & Decodable>: Decodable {
    var equals: T?
    var notEquals: T?
    var greaterThan: T?
    var lessThan: T?
    var greaterThanOrEquals: T?
    var lessThanOrEquals: T?
    var inCollection: [T]?
    var notInCollection: [T]?

    init(
            equals: T? = nil,
            notEquals: T? = nil,
            greaterThan: T? = nil,
            lessThan: T? = nil,
            greaterThanOrEquals: T? = nil,
            lessThanOrEquals: T? = nil,
            inCollection: [T]? = nil,
            notInCollection: [T]? = nil
    ) {
        self.equals = equals
        self.notEquals = notEquals
        self.greaterThan = greaterThan
        self.lessThan = lessThan
        self.greaterThanOrEquals = greaterThanOrEquals
        self.lessThanOrEquals = lessThanOrEquals
        self.inCollection = inCollection
        self.notInCollection = notInCollection
    }

    func apply(to value: T) -> Bool {
        if equals != nil && value != equals! {
            return false
        }
        if notEquals != nil && value == notEquals! {
            return false
        }
        if greaterThan != nil && value <= greaterThan! {
            return false
        }
        if lessThan != nil && value >= lessThan! {
            return false
        }
        if greaterThanOrEquals != nil && value < greaterThanOrEquals! {
            return false
        }
        if lessThanOrEquals != nil && value > lessThanOrEquals! {
            return false
        }
        if inCollection != nil && !inCollection!.contains(value) {
            return false
        }
        if notInCollection != nil && notInCollection!.contains(value) {
            return false
        }
        return true
    }

    enum CodingKeys: String, CodingKey {
        case equals
        case notEquals = "not_equals"
        case greaterThan = "greater_than"
        case lessThan = "less_than"
        case greaterThanOrEquals = "greater_than_or_equals"
        case lessThanOrEquals = "less_than_or_equals"
        case inCollection = "in"
        case notInCollection = "not_in"
    }
}
