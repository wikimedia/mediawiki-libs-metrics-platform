import Foundation

class CurationController {
    let contextController: ContextController

    init(contextController: ContextController) {
        self.contextController = contextController
    }

    func eventPassesCurationRules(_ event: Event, streamConfig: StreamConfig) -> Bool {
        guard let curationFilter = streamConfig.producerConfig?.clientConfig?.curationFilter else {
            return true
        }
        return curationFilter.apply(to: event)
    }
}
