import Foundation

/**
 * Curation rules to be used with context values with collection types.
 */
struct CollectionCurationRules<T: Hashable & Decodable>: Decodable {
    let contains: T?
    let doesNotContain: T?
    let containsAll: [T]?
    let containsAny: [T]?

    init(
            contains: T? = nil,
            doesNotContain: T? = nil,
            containsAll: [T]? = nil,
            containsAny: [T]? = nil
    ) {
        self.contains = contains
        self.doesNotContain = doesNotContain
        self.containsAll = containsAll
        self.containsAny = containsAny
    }

    func apply(to value: [T]) -> Bool {
        if contains != nil && !value.contains(contains!) {
            return false
        }
        if doesNotContain != nil && value.contains(doesNotContain!) {
            return false
        }
        if containsAll != nil {
            for el in containsAll! {
                if !value.contains(el) {
                    return false
                }
            }
        }
        if containsAny != nil {
            var found = false
            for el in containsAny! {
                if value.contains(el) {
                    found = true
                    break
                }
            }
            if !found {
                return false
            }
        }

        return true
    }

    enum CodingKeys: String, CodingKey {
        case contains
        case doesNotContain = "does_not_contain"
        case containsAll = "contains_all"
        case containsAny = "contains_any"
    }
}
