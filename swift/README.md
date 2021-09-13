# WikimediaMetricsPlatform
Metrics client library for the official Wikipedia app for iOS. See the [Metrics Platform](https://wikitech.wikimedia.org/wiki/Metrics_Platform) project page on Wikitech for details.

The library itself is platform-agnostic, and is built and tested exclusively with free-licensed code.

## Testing
Executing `make test` will run the tests in a temporary Docker container, after first downloading the `swift` image if needed.