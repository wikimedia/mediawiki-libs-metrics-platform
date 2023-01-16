import Foundation

class CurationController {
    let contextController: ContextController

    init(contextController: ContextController) {
        self.contextController = contextController
    }

    func eventPassesCurationRules(_ event: Event, streamConfig: StreamConfig) -> Bool {
        guard let curationRules = streamConfig.getCurationRules() else {
            return true
        }
        
        return curationRules.apply(to: event)
    }
}
