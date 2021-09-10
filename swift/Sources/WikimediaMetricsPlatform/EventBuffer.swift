import Foundation
import DequeModule

class EventBuffer {
    var events: Deque<Event>
    let destination: URL

    init(destination: URL) {
        self.events = Deque()
        self.destination = destination
    }

    func prepend(_ event: Event) {
        events.prepend(event)
    }

    func append(_ event: Event) {
        events.append(event)
    }

    func popFirst() -> Event? {
        return events.popFirst()
    }
}
