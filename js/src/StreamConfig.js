// Types
// =====

/**
 * @memberof StreamConfig
 *
 * @typedef {Object.<string,StreamConfig>} StreamConfigs
 */

/**
 * @memberof StreamConfig
 *
 * @typedef {Object} StreamConfig
 * @property {string} [schema_title]
 * @property {Object.<string,StreamProducerConfig>} [producers]
 * @property {StreamSamplingConfig} [sample]
 */

/**
 * @memberof StreamConfig
 *
 * @typedef {Object} StreamSamplingConfig
 * @property {string} unit
 * @property {number} rate
 */

/**
 * @memberof StreamConfig
 *
 * @typedef {Object} StreamProducerConfig
 * @property {string[]} [events]
 * @property {StreamSamplingConfig} [sampling]
 * @property {StreamProducerContextAttribute[]} [provide_values]
 * @property {StreamProducerCurationConfigs} [curation]
 */

/**
 * @memberof StreamConfig
 *
 * @typedef {Object.<string,StreamProducerContextAttribute>} StreamProducerCurationConfigs
 */

/**
 * @memberof StreamConfig
 *
 * @readonly
 * @enum {string}
 */
const StreamProducerContextAttribute = {

    // Agent
    agent_client_platform: 'agent_client_platform',
    agent_client_platform_family: 'agent_client_platform_family',

    // Page
    page_id: 'page_id',
    page_title: 'page_title',
    page_namespace: 'page_namespace',
    page_namespace_name: 'page_namespace_name',
    page_revision_id: 'page_revision_id',
    page_wikidata_id: 'page_wikidata_id',
    page_wikidata_qid: 'page_wikidata_id',
    page_content_language: 'page_content_language',
    page_is_redirect: 'page_is_redirect',
    page_user_groups_allowed_to_move: 'page_user_groups_allowed_to_move',
    page_user_groups_allowed_to_edit: 'page_user_groups_allowed_to_edit',

    // MediaWiki
    mediawiki_skin: 'mediawiki_skin',
    mediawiki_version: 'mediawiki_version',
    mediawiki_is_production: 'mediawiki_is_production',
    mediawiki_is_debug_mode: 'mediawiki_is_debug_mode',
    mediawiki_database: 'mediawiki_database',
    mediawiki_site_content_language: 'mediawiki_site_content_language',
    mediawiki_site_content_language_variant: 'mediawiki_site_content_language_variant',

    // Performer
    performer_is_logged_in: 'performer_is_logged_in',
    performer_id: 'performer_id',
    performer_name: 'performer_name',
    performer_session_id: 'performer_session_id',
    performer_active_browsing_session_token: 'performer_active_browsing_session_token',
    performer_pageview_id: 'performer_pageview_id',
    performer_groups: 'performer_groups',
    performer_is_bot: 'performer_is_bot',
    performer_is_temp: 'performer_is_temp',
    performer_language: 'performer_language',
    performer_language_variant: 'performer_language_variant',
    performer_can_probably_edit_page: 'performer_can_probably_edit_page',
    performer_edit_count: 'performer_edit_count',
    performer_edit_count_bucket: 'performer_edit_count_bucket',
    performer_registration_dt: 'performer_registration_dt'
};

/**
 * @memberof StreamConfig
 *
 * @typedef {Object} StreamProducerCurationConfig
 * @property {StreamConfig.StreamProducerCurationOperand} [equals]
 * @property {StreamConfig.StreamProducerCurationOperand} [not_equals]
 * @property {StreamConfig.StreamProducerCurationOperand} [greater_than]
 * @property {number} [less_than]
 * @property {number} [less_than_or_equals]
 * @property {number} [greater_than_or_equals]
 * @property {StreamConfig.StreamProducerCurationOperand[]} [in]
 * @property {StreamConfig.StreamProducerCurationOperand[]} [not_in]
 * @property {StreamConfig.StreamProducerCurationOperand} [contains]
 * @property {StreamConfig.StreamProducerCurationOperand[]} [contains_all]
 * @property {StreamConfig.StreamProducerCurationOperand[]} [contains_any]
 * @property {StreamConfig.StreamProducerCurationOperand} [does_not_contain]
 */

/**
 * @memberof StreamConfig
 *
 * @typedef {string|number|boolean|null} StreamProducerCurationOperand
 */

// Functions
// =========

/**
 * @namespace StreamConfig
 */

/**
 * @memberof StreamConfig
 *
 * @param {?StreamConfig.StreamSamplingConfig} sample
 * @return {boolean}
 */
function isValidSample( sample ) {
	return !!(
		sample &&
		sample.unit && sample.rate &&
		sample.rate >= 0 && sample.rate <= 1
	);
}

module.exports = {
	isValidSample: isValidSample
};
