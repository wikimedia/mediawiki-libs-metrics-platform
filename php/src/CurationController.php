<?php

namespace Wikimedia\Metrics;

class CurationController {

	/**
	 * Returns true if the event passes all curation rules in the stream configuration.
	 *
	 * @param array $event
	 * @param array $streamConfig
	 * @return bool
	 */
	protected function eventPassesCurationRules( array $event, array $streamConfig ): bool {
		$curationRules = $this->getCurationRules( $streamConfig );
		if ( !$curationRules ) {
			return true;
		}
		foreach ( $curationRules as $property => $rules ) {
			switch ( $property ) {
				// Page
				case "page_id":
					if ( !isset( $event["page"]["id"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["page"]["id"], $rules ) ) {
						return false;
					}
					break;
				case "page_namespace_id":
					if ( !isset( $event["page"]["namespace_id"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["page"]["namespace_id"], $rules ) ) {
						return false;
					}
					break;
				case "page_namespace_text":
					if ( !isset( $event["page"]["namespace_text"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["page"]["namespace_text"], $rules ) ) {
						return false;
					}
					break;
				case "page_title":
					if ( !isset( $event["page"]["title"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["page"]["title"], $rules ) ) {
						return false;
					}
					break;
				case "page_revision":
					if ( !isset( $event["page"]["revision"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["page"]["revision"], $rules ) ) {
						return false;
					}
					break;
				case "page_wikidata_id":
					if ( !isset( $event["page"]["wikidata_id"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["page"]["wikidata_id"], $rules ) ) {
						return false;
					}
					break;
				case "page_is_redirect":
					if ( !isset( $event["page"]["is_redirect"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["page"]["is_redirect"], $rules ) ) {
						return false;
					}
					break;
				case "page_content_language":
					if ( !isset( $event["page"]["content_language"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["page"]["content_language"], $rules ) ) {
						return false;
					}
					break;
				case "page_user_groups_allowed_to_edit":
					if ( !isset( $event["page"]["user_groups_allowed_to_edit"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["page"]["user_groups_allowed_to_edit"], $rules ) ) {
						return false;
					}
					break;
				case "page_user_groups_allowed_to_move":
					if ( !isset( $event["page"]["user_groups_allowed_to_move"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["page"]["user_groups_allowed_to_move"], $rules ) ) {
						return false;
					}
					break;

					// User
				case "user_id":
					if ( !isset( $event["user"]["id"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["user"]["id"], $rules ) ) {
						return false;
					}
					break;
				case "user_name":
					if ( !isset( $event["user"]["name"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["user"]["name"], $rules ) ) {
						return false;
					}
					break;
				case "user_groups":
					if ( !isset( $event["user"]["groups"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["user"]["groups"], $rules ) ) {
						return false;
					}
					break;
				case "user_is_logged_in":
					if ( !isset( $event["user"]["is_logged_in"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["user"]["is_logged_in"], $rules ) ) {
						return false;
					}
					break;
				case "user_is_bot":
					if ( !isset( $event["user"]["is_bot"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["user"]["is_bot"], $rules ) ) {
						return false;
					}
					break;
				case "user_can_probably_edit_page":
					if ( !isset( $event["user"]["can_probably_edit_page"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["user"]["can_probably_edit_page"], $rules ) ) {
						return false;
					}
					break;
				case "user_edit_count":
					if ( !isset( $event["user"]["edit_count"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["user"]["edit_count"], $rules ) ) {
						return false;
					}
					break;
				case "user_edit_count_bucket":
					if ( !isset( $event["user"]["edit_count_bucket"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["user"]["edit_count_bucket"], $rules ) ) {
						return false;
					}
					break;
				case "user_registration_timestamp":
					if ( !isset( $event["user"]["registration_timestamp"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["user"]["registration_timestamp"], $rules ) ) {
						return false;
					}
					break;
				case "user_language":
					if ( !isset( $event["user"]["language"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["user"]["language"], $rules ) ) {
						return false;
					}
					break;
				case "user_language_variant":
					if ( !isset( $event["user"]["language_variant"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["user"]["language_variant"], $rules ) ) {
						return false;
					}
					break;

					// MediaWiki
				case "mediawiki_skin":
					if ( !isset( $event["mediawiki"]["skin"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["mediawiki"]["skin"], $rules ) ) {
						return false;
					}
					break;
				case "mediawiki_version":
					if ( !isset( $event["mediawiki"]["version"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["mediawiki"]["version"], $rules ) ) {
						return false;
					}
					break;
				case "mediawiki_site_content_language":
					if ( !isset( $event["mediawiki"]["site_content_language"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["mediawiki"]["site_content_language"], $rules ) ) {
						return false;
					}
					break;

				case "access_method":
					if ( !isset( $event["access_method"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["access_method"], $rules ) ) {
						return false;
					}
					break;
				case "platform":
					if ( !isset( $event["platform"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["platform"], $rules ) ) {
						return false;
					}
					break;
				case "platform_family":
					if ( !isset( $event["platform_family"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["platform_family"], $rules ) ) {
						return false;
					}
					break;
				case "is_production":
					if ( !isset( $event["is_production"] ) ) {
						return false;
					}
					if ( !$this->applyRules( $event["is_production"], $rules ) ) {
						return false;
					}
					break;
				default:
					break;
			}
		}

		return true;
	}

	/**
	 * Apply configured rules to a context value.
	 *
	 * @param mixed $value
	 * @param array $rules
	 * @return bool
	 */
	private function applyRules( $value, array $rules ): bool {
		foreach ( $rules as $operator => $comparator ) {
			switch ( $operator ) {
				case "equals":
					if ( $value !== $comparator ) {
						return false;
					}
					break;
				case "not_equals":
					if ( $value === $comparator ) {
						return false;
					}
					break;
				case "greater_than":
					if ( $value <= $comparator ) {
						return false;
					}
					break;
				case "less_than":
					if ( $value >= $comparator ) {
						return false;
					}
					break;
				case "greater_than_or_equals":
					if ( $value < $comparator ) {
						return false;
					}
					break;
				case "less_than_or_equals":
					if ( $value > $comparator ) {
						return false;
					}
					break;
				case "in":
					if ( !in_array( $value, $comparator ) ) {
						return false;
					}
					break;
				case "not_in":
					if ( in_array( $value, $comparator ) ) {
						return false;
					}
					break;
				case "contains":
					if ( !in_array( $comparator, $value ) ) {
						return false;
					}
					break;
				case "does_not_contain":
					if ( in_array( $comparator, $value ) ) {
						return false;
					}
					break;
				case "contains_all":
					if ( count( array_diff( $comparator, $value ) ) > 0 ) {
						return false;
					}
					break;
				case "contains_any":
					if ( count( array_intersect( $comparator, $value ) ) === 0 ) {
						return false;
					}
					break;
				default:
					break;
			}
		}
		return true;
	}

	/**
	 * Extract the list of requested values from a stream configuration, if present.
	 *
	 * @param array $streamConfig
	 * @return array|null
	 */
	private function getCurationRules( array $streamConfig ) : ?array {
		if ( !$streamConfig ) {
			return null;
		}
		if ( !isset( $streamConfig["producers"] ) ) {
			return null;
		}
		if ( !isset( $streamConfig["producers"]["metrics_platform_client"] ) ) {
			return null;
		}
		if ( !isset( $streamConfig["producers"]["metrics_platform_client"]["curation"] ) ) {
			return null;
		}
		return $streamConfig["producers"]["metrics_platform_client"]["curation"];
	}

}
