/**
 * @constructor
 */
function CurationController() {}

/**
 * Whether the value is undefined or null.
 *
 * This provides a safe way to check for the existence of possibly-falsy values.
 *
 * @param {*} value
 * @return {boolean}
 */
CurationController.prototype.isEmpty = function ( value ) {
	return value === undefined || value === null;
};

/**
 * Apply filtering rules to a value.
 *
 * @param {*} value
 * @param {StreamProducerCurationConfig} rules
 * @return {boolean} true if the event passes filtering, false if not
 */
CurationController.prototype.applyRules = function ( value, rules ) {
	/** @type keyof StreamProducerCurationConfig */
	var operator;

	for ( operator in rules ) {
		var i;
		var operand = rules[ operator ];
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
			var found;
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
 *         performer_is_logged_in: {
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
 * @param {MetricsPlatformEventData} eventData
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
			case 'page_namespace':
				if ( !eventData.page || this.isEmpty( eventData.page.namespace ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.page.namespace,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'page_namespace_name':
				if ( !eventData.page || this.isEmpty( eventData.page.namespace_name ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.page.namespace_name,
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
			case 'performer_id':
				if ( !eventData.performer || this.isEmpty( eventData.performer.id ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.performer.id, curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'performer_name':
				if ( !eventData.performer || this.isEmpty( eventData.performer.name ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.performer.name, curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'performer_groups':
				if ( !eventData.performer || this.isEmpty( eventData.performer.groups ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.performer.groups, curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'performer_is_logged_in':
				if ( !eventData.performer || this.isEmpty( eventData.performer.is_logged_in ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.performer.is_logged_in,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'performer_is_bot':
				if ( !eventData.performer || this.isEmpty( eventData.performer.is_bot ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.performer.is_bot, curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'performer_can_probably_edit_page':
				if ( !eventData.performer ||
					this.isEmpty( eventData.performer.can_probably_edit_page ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.performer.can_probably_edit_page,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'performer_edit_count':
				if ( !eventData.performer || this.isEmpty( eventData.performer.edit_count ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.performer.edit_count,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'performer_edit_count_bucket':
				if (
					!eventData.performer ||
					this.isEmpty( eventData.performer.edit_count_bucket )
				) {
					return false;
				}
				if ( !this.applyRules( eventData.performer.edit_count_bucket,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'performer_registration_timestamp':
				if ( !eventData.performer ||
					this.isEmpty( eventData.performer.registration_dt ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.performer.registration_dt,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'performer_language':
				if ( !eventData.performer || this.isEmpty( eventData.performer.language ) ) {
					return false;
				}
				if ( !this.applyRules( eventData.performer.language,
					curationConfig[ property ] ) ) {
					return false;
				}
				break;
			case 'performer_language_variant':
				if (
					!eventData.performer ||
					this.isEmpty( eventData.performer.language_variant )
				) {
					return false;
				}
				if ( !this.applyRules( eventData.performer.language_variant,
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
			default:
				break;
		}
	}
	return true;
	// eslint-enable camelcase
};

module.exports = CurationController;
