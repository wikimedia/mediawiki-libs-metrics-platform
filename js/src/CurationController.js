/**
 * @constructor
 */
function CurationController() {}

/**
 * Returns true if the value is undefined or null.
 * This provides a safe way to check for the existence of possibly-falsy values.
 *
 * @param {*} value
 * @return {boolean}
 */
CurationController.prototype.isEmpty = function ( value ) {
	return value === undefined || value === null;
};

/**
 * Applies filtering rules to a value.
 *
 * @param {*} value
 * @param {StreamProducerCurationConfig} rules
 * @return {boolean} true if the event passes filtering, false if not
 */
CurationController.prototype.applyRules = function ( value, rules ) {
	var i, found, operand;

	/** @type keyof StreamProducerCurationConfig */
	var operator;

	for ( operator in rules ) {
		operand = rules[ operator ];
		if ( operator === 'equals' && value !== operand ) {
			return false;
		} else if ( operator === 'not_equals' && value === operand ) {
			return false;
		} else if ( operator === 'greater_than' && value <= Number( operand ) ) {
			return false;
		} else if ( operator === 'less_than' && value >= Number( operand ) ) {
			return false;
		} else if ( operator === 'greater_than_or_equals' && value < Number( operand ) ) {
			return false;
		} else if ( operator === 'less_than_or_equals' && value > Number( operand ) ) {
			return false;
		} else if (
			operator === 'in' &&
			Array.isArray( operand ) &&
			operand.indexOf( value ) === -1
		) {
			return false;
		} else if (
			operator === 'not_in' &&
			Array.isArray( operand ) &&
			operand.indexOf( value ) > -1
		) {
			return false;
		} else if ( operator === 'contains' && value.indexOf( operand ) === -1 ) {
			return false;
		} else if ( operator === 'does_not_contain' && value.indexOf( operand ) > -1 ) {
			return false;
		} else if ( operator === 'contains_all' && Array.isArray( operand ) ) {
			for ( i = 0; i < operand.length; i++ ) {
				if ( value.indexOf( operand[ i ] ) === -1 ) {
					return false;
				}
			}
		} else if ( operator === 'contains_any' && Array.isArray( operand ) ) {
			for ( i = 0; i < operand.length; i++ ) {
				if ( value.indexOf( operand[ i ] ) > -1 ) {
					found = true;
					break;
				}
			}
			if ( !found ) {
				return false;
			}
			found = false;
		}
	}
	return true;
};

/**
 * Apply any curation rules specified in the stream config to the event.
 *
 * Curation rules can be added to the 'metrics_platform_client.curation' property of any given
 * stream configuration. For example:
 *
 * ```
 * "very.cool.stream": {
 *   producers: {
 *     metrics_platform_client: {
 *       curation: {
 *         user_is_logged_in: {
 *           equals: true
 *         },
 *         mediawiki_skin: {
 *           in: [ "Vector", "MinervaNeue" ]
 *         }
 *       }
 *     }
 *   }
 * }
 * ```
 *
 * The following rules are supported:
 *
 * ```
 * { equals: x }
 * { not_equals: x }
 * { less_than: x }
 * { greater_than: x }
 * { less_than_or_equals: x }
 * { greater_than_or_equals: x }
 * { in: [x, y, z] }
 * { not_in: [x, y, z] }
 * { contains: x }
 * { not_contains: x }
 * { contains_all: [x, y, z] }
 * { contains_any: [x, y, z] }
 * ```
 *
 * @param {EventData} eventData
 * @param {StreamConfig} streamConfig
 * @return {boolean} true if the event passes filtering, false if not
 * @throws {Error} If a malformed filter is found
 */
CurationController.prototype.shouldProduceEvent = function ( eventData, streamConfig ) {
	// eslint-disable camelcase
	var curationConfig = streamConfig &&
		streamConfig.producers &&
		streamConfig.producers.metrics_platform_client &&
		streamConfig.producers.metrics_platform_client.curation;

	if ( !curationConfig || typeof curationConfig !== 'object' ) {
		return true;
	}

	for ( var property in curationConfig ) {
		switch ( property ) {
			// page
			case 'page_id':
				if ( !eventData.page || this.isEmpty( eventData.page.id ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.page.id, curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'page_namespace_id':
				if ( !eventData.page || this.isEmpty( eventData.page.namespace_id ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.page.namespace_id,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'page_namespace_text':
				if ( !eventData.page || this.isEmpty( eventData.page.namespace_text ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.page.namespace_text,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'page_title':
				if ( !eventData.page || this.isEmpty( eventData.page.title ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.page.title, curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'page_revision_id':
				if ( !eventData.page || this.isEmpty( eventData.page.revision_id ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.page.revision_id,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'page_wikidata_id':
				if ( !eventData.page || this.isEmpty( eventData.page.wikidata_id ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.page.wikidata_id,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'page_is_redirect':
				if ( !eventData.page || this.isEmpty( eventData.page.is_redirect ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.page.is_redirect,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'page_content_language':
				if ( !eventData.page || this.isEmpty( eventData.page.content_language ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.page.content_language,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'page_user_groups_allowed_to_move':
				if ( !eventData.page ||
					this.isEmpty( eventData.page.user_groups_allowed_to_move ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.page.user_groups_allowed_to_move,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'page_user_groups_allowed_to_edit':
				if ( !eventData.page ||
					this.isEmpty( eventData.page.user_groups_allowed_to_edit ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.page.user_groups_allowed_to_edit,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;

				// User
			case 'user_id':
				if ( !eventData.user || this.isEmpty( eventData.user.id ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.user.id, curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'user_name':
				if ( !eventData.user || this.isEmpty( eventData.user.name ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.user.name, curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'user_groups':
				if ( !eventData.user || this.isEmpty( eventData.user.groups ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.user.groups, curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'user_is_logged_in':
				if ( !eventData.user || this.isEmpty( eventData.user.is_logged_in ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.user.is_logged_in,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'user_is_bot':
				if ( !eventData.user || this.isEmpty( eventData.user.is_bot ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.user.is_bot, curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'user_can_probably_edit_page':
				if ( !eventData.user ||
					this.isEmpty( eventData.user.can_probably_edit_page ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.user.can_probably_edit_page,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'user_edit_count':
				if ( !eventData.user || this.isEmpty( eventData.user.edit_count ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.user.edit_count,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'user_edit_count_bucket':
				if ( !eventData.user || this.isEmpty( eventData.user.edit_count_bucket ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.user.edit_count_bucket,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'user_registration_timestamp':
				if ( !eventData.user ||
					this.isEmpty( eventData.user.registration_timestamp ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.user.registration_timestamp,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'user_language':
				if ( !eventData.user || this.isEmpty( eventData.user.language ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.user.language,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'user_language_variant':
				if ( !eventData.user || this.isEmpty( eventData.user.language_variant ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.user.language_variant,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;

				// MediaWiki
			case 'mediawiki_skin':
				if ( !eventData.mediawiki || this.isEmpty( eventData.mediawiki.skin ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.mediawiki.skin,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'mediawiki_version':
				if ( !eventData.mediawiki || this.isEmpty( eventData.mediawiki.version ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.mediawiki.version,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'mediawiki_site_content_language':
				if ( !eventData.mediawiki ||
					this.isEmpty( eventData.mediawiki.site_content_language ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.mediawiki.site_content_language,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;

				// Device
			case 'device_pixel_ratio':
				if ( !eventData.device || this.isEmpty( eventData.device.pixel_ratio ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.device.pixel_ratio,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'device_hardware_concurrency':
				if ( !eventData.device ||
					this.isEmpty( eventData.device.hardware_concurrency ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.device.hardware_concurrency,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'device_max_touch_points':
				if ( !eventData.device || this.isEmpty( eventData.device.max_touch_points ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.device.max_touch_points,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;

				// Misc
			case 'access_method':
				if ( this.isEmpty( eventData.access_method ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.access_method,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'platform':
				if ( this.isEmpty( eventData.platform ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.platform, curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'platform_family':
				if ( this.isEmpty( eventData.platform_family ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.platform_family,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'is_production':
				if ( this.isEmpty( eventData.is_production ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.is_production,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			default:
				break;
		}
	}
	return true;
	// eslint-enable camelcase
};

module.exports = CurationController;
