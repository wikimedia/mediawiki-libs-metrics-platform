import Foundation

/**
 * The configuration of a stream.
 */
struct StreamConfig: Decodable {
    var schemaTitle: String
    var destinationEventService: DestinationEventService
    var producers: Producers?
    
    func getSamplingConfig() -> SamplingConfig? {
        return producers?.metricsPlatformClient?.sampling
    }
    
    func getRequestedValues() -> [ContextAttribute] {
        return producers?.metricsPlatformClient?.provideValues ?? []
    }
    
    func getCurationRules() -> CurationFilter? {
        return producers?.metricsPlatformClient?.curation;
    }

    struct Producers: Decodable {
        var metricsPlatformClient: MetricsPlatformClient?

        struct MetricsPlatformClient: Decodable {
            var sampling: SamplingConfig?
            var provideValues: [ContextAttribute]?
            var curation: CurationFilter?
        }
    }
}
