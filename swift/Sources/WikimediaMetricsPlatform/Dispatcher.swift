import Foundation

/**
 * Based on Maciej Piotrowski's excellent `Dispatcher` class detailed at
 * https://swifting.io/2020/05/24/Stubbing-DispatchQueue.html
 */
class Dispatcher {
    private let dispatchQueue: DispatchQueue

    init(dispatchQueue: DispatchQueue) {
        self.dispatchQueue = dispatchQueue
    }

    func sync(_ work: @escaping () -> Void) {
        self.dispatchQueue.sync(execute: work)
    }

    func async(_ work: @escaping () -> Void) {
        self.dispatchQueue.async(execute: work)
    }

    func asyncAfter(deadline: DispatchTime, _ work: @escaping () -> Void) {
        self.dispatchQueue.asyncAfter(deadline: deadline, execute: work)
    }
}
