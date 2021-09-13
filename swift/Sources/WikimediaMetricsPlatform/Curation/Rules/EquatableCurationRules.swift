import Foundation

/**
 * Curation rules to be used with context values whose types conform to
 * Hashable (and therefore to its parent protocol Equatable) but not to
 * Comparable.
 *
 * This class is also used for String-typed context values, although
 * Strings also implement Comparable (likely for alphabetical ordering).
 */
struct EquatableCurationRules<T: Hashable & Decodable>: Decodable {
    var equals: T? = nil
    var notEquals: T? = nil
    var inCollection: [T]? = nil
    var notInCollection:[T]? = nil

    init(
            equals: T? = nil,
            notEquals: T? = nil,
            inCollection: [T]? = nil,
            notInCollection: [T]? = nil
    ) {
        self.equals = equals
        self.notEquals = notEquals
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
        case inCollection = "in"
        case notInCollection = "not_in"
    }
}
