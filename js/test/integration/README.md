# Integration Test

## Setup

The `js/test/integration/index.js` script submits events to the following streams:

1. `test.metrics_platform.metrics_events`; and
2. `test.metrics_platform.interactions`

The script fetches stream configs from a MediaWiki wiki under your control
(default: http://localhost:8080). You should add configuration for the above streams in the
`LocalSettings.php` file for that wiki, e.g.

```php
$wgEventStreams = [
    'test.metrics_platform.metrics_events' => [
        'schema_title' => 'analytics/mediawiki/client/metrics_event',
        'destination_event_service' => 'eventgate-analytics-external',
        'producers' => [
            'metrics_platform_client' => [
                'events' => [
                    'test.',
                ],
                'provide_values' => [
                    'agent_client_platform_family',
                    'mediawiki_skin',
                ],
            ],
        ],
    ],
    'test.metrics_platform.interactions' => [
        'schema_title' => 'analytics/product_metrics/web/base',
        'destination_event_service' => 'eventgate-analytics-external',
        'producers' => [
            'metrics_platform_client' => [
                'provide_values' => [
                    'agent_client_platform_family',
                ],
            ],
        ],
    ],
];
```

## Usage

```
cd /path/to/metrics-platform
node js/test/integration/index.js
```

The script should output something like:

```
Submitted the following event: {
  '$schema': '/analytics/mediawiki/client/metrics_event/2.1.0',
  dt: '...',
  name: 'test.init',
  custom_data: {},
  meta: {
    stream: 'test.metrics_platform.metrics_events',
    domain: '...'
  },
  agent: { client_platform_family: 'app' },
  mediawiki: { skin: 'minerva' }
}
Submitted the following event: {
  '$schema': '/analytics/mediawiki/client/metrics_event/2.1.0',
  dt: '...',
  name: 'test.click',
  custom_data: { element_id: { data_type: 'string', value: 'ca-edit' } },
  meta: {
    stream: 'test.metrics_platform.metrics_events',
    domain: '...'
  },
  agent: { client_platform_family: 'app' },
  mediawiki: { skin: 'minerva' }
}
Submitted the following event: {
  '$schema': '/analytics/mediawiki/client/metrics_event/2.1.0',
  dt: '...',
  name: 'test.click',
  custom_data: { element_id: { data_type: 'string', value: 'ca-talk' } },
  meta: {
    stream: 'test.metrics_platform.metrics_events',
    domain: '...'
  },
  agent: { client_platform_family: 'app' },
  mediawiki: { skin: 'minerva' }
}
Submitted the following event: {
  action: 'init',
  funnel_event_sequence_position: 1,
  '$schema': '/analytics/product_metrics/web/base/1.2.0',
  agent: { client_platform_family: 'app' },
  dt: '...',
  meta: { stream: 'test.metrics_platform.interactions', domain: '...' }
}
Submitted the following event: {
  action: 'click',
  element_id: 'ca-edit',
  element_friendly_name: 'edit',
  funnel_event_sequence_position: 2,
  '$schema': '/analytics/product_metrics/web/base/1.2.0',
  agent: { client_platform_family: 'app' },
  dt: '...',
  meta: { stream: 'test.metrics_platform.interactions', domain: '...' }
}
Submitted the following event: {
  action: 'click',
  element_id: 'ca-talk',
  element_friendly_name: 'talk',
  funnel_event_sequence_position: 3,
  '$schema': '/analytics/product_metrics/web/base/1.2.0',
  agent: { client_platform_family: 'app' },
  dt: '...',
  meta: { stream: 'test.metrics_platform.interactions', domain: '...' }
}
```
