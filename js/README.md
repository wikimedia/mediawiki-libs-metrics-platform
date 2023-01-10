# Wikimedia Metrics Platform Client

This is the JavaScript implementation of the Wikimedia Metrics Platform Client (JS MPC).

See https://wikitech.wikimedia.org/wiki/Metrics_Platform for high-level detail about the Metrics Platform project.

## Getting Started

```js
const { DefaultIntegration, MetricsPlatform } = require( '/path/to/metrics-platform' );

// The default integration will:
//
// * Fetch stream configs from metawiki using the streamconfigs MediaWiki Action API query
//   module; and
// * Submits events to the EventGate instance available at https://intake-analytics.wikimedia.org
const integration = new DefaultIntegration();
const metricsPlatform = new MetricsPlatform( integration );

integration.setContextAttributes( {
    performer: {
        is_logged_in: false,
        pageview_id: '…'
    }
} );
```

## Browser Support

The MPCs are Wikimedia Foundation projects and the JS MPC is integrated with the EventLogging MediaWiki extension.
Therefore, we aim to support the _Modern (Grade A)_ browsers enumerated at
[mw:Compatibility#Browser_support_matrix](https://www.mediawiki.org/wiki/Compatibility#Browser_support_matrix).

However, `DefaultIntegration` uses:

* The URL API, which is not supported by IE11;
* Promises, which are not supported by IE11; and
* Fetch, which is not supported by IE11 and Safari on iOS < 10.3

## Testing

You can run the unit tests with `npm t` or `npm run test` and produce a coverage report with `npm run coverage`.

It is **strongly** recommended that you run the commands above via [🌱 Fresh](https://gerrit.wikimedia.org/g/fresh).

### Running the Integration Test

The integration test requires:

1. https://meta.wikimedia.org to be reachable; and
2. An instance of EventGate listening on port 8192

If those requirements are met, you can run the integration test as follows:

Terminal 1:

```bash
cd /path/to/mediawiki/extensions/EventLogging/devserver/

docker build -t eventlogging-devserver .
docker run -p "8192:8192" eventlogging-devserver
```

Terminal 2:

```bash
node test/integration/index.js
```
