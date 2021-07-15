import Foundation
import DequeModule

/**
 * Wraps a Deque instance with logic ensuring that it never exceeds a maximum capacity.
 * This encapsulates the logic required for the Metrics Client's limited-capacity "input buffer."
 */
struct LimitedCapacityDeque<Element> {
    let capacity: Int
    var deque = Deque<Element>()

    init(capacity: Int) {
        self.capacity = capacity
    }

    func count() -> Int {
        return self.deque.count
    }

    mutating func append(_ newElement: Element) {
        while self.deque.count >= capacity {
             _ = self.deque.popFirst()
        }
        self.deque.append(newElement)
    }

    mutating func popFirst() -> Element? {
        return self.deque.popFirst()
    }
}
