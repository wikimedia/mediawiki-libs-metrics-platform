import Foundation
@testable import WikimediaMetricsPlatform

class StubDispatcher: Dispatcher {
    init() {
        /// NOTE: We will never interact with this dispatch queue.
        super.init(dispatchQueue: DispatchQueue.main)
    }
    
    override func sync(_ work: @escaping () -> Void) {
        work()
    }
    
    override func async(_ work: @escaping () -> Void) {
        work()
    }
    
    override func asyncAfter(deadline: DispatchTime, _ work: @escaping () -> Void) {
        work()
    }
}
