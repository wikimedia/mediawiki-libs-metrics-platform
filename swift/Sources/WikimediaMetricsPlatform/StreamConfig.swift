import Foundation

/// An individual stream's configuration.
struct StreamConfig: Decodable {
    var stream: String
    var schema: String
    var destination: DestinationEventService
    var producerConfig: ProducerConfig?

    func getSamplingConfig() -> SamplingConfig? {
        return producerConfig?.clientConfig?.samplingConfig
    }

    enum CodingKeys: String, CodingKey {
        case stream
        case schema = "schema_title"
        case destination = "destination_event_service"
        case producerConfig = "producers"
    }

    struct ProducerConfig: Decodable {
        var clientConfig: MetricsPlatformClientConfig?

        enum CodingKeys: String, CodingKey {
            case clientConfig = "metrics_platform_client"
        }

        struct MetricsPlatformClientConfig: Decodable {
            var samplingConfig: SamplingConfig?
            var requestedValues: [ContextValue]?
            var curationFilter: CurationFilter?

            enum CodingKeys: String, CodingKey {
                case samplingConfig = "sampling"
                case requestedValues = "provide_values"
                case curationFilter = "curation"
            }
        }
    }
}
