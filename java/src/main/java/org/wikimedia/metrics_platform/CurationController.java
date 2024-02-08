package org.wikimedia.metrics_platform;

import java.util.ArrayList;
import java.util.Collection;

import org.wikimedia.metrics_platform.config.CurationFilter;
import org.wikimedia.metrics_platform.config.StreamConfig;
import org.wikimedia.metrics_platform.context.AgentData;
import org.wikimedia.metrics_platform.context.MediawikiData;
import org.wikimedia.metrics_platform.context.PageData;
import org.wikimedia.metrics_platform.context.PerformerData;
import org.wikimedia.metrics_platform.config.curation.CollectionCurationRules;
import org.wikimedia.metrics_platform.config.curation.ComparableCurationRules;
import org.wikimedia.metrics_platform.config.curation.CurationRules;
import org.wikimedia.metrics_platform.event.EventProcessed;

import lombok.NonNull;

public class CurationController {
    boolean shouldProduceEvent(@NonNull EventProcessed event, @NonNull StreamConfig streamConfig) {
        if (!streamConfig.hasCurationFilter()) {
            return true;
        }

        CurationFilter curationFilter = streamConfig.getCurationFilter();

        return applyAgentRules(event.getAgentData(), curationFilter) &&
            applyMediaWikiRules(event.getMediawikiData(), curationFilter) &&
            applyPageRules(event.getPageData(), curationFilter) &&
            applyPerformerRules(event.getPerformerData(), curationFilter);
    }

    private static boolean applyAgentRules(@NonNull AgentData data, @NonNull CurationFilter rules) {
        return applyRules(rules.getAgentAppInstallIdRules(), data.getAppInstallId()) &&
            applyRules(rules.getAgentClientPlatformRules(), data.getClientPlatform()) &&
            applyRules(rules.getAgentClientPlatformFamilyRules(), data.getClientPlatformFamily());
    }

    private static boolean applyMediaWikiRules(@NonNull MediawikiData data, @NonNull CurationFilter rules) {
        return applyRules(rules.getMediawikiDatabase(), data.getDatabase());
    }

    private static boolean applyPageRules(@NonNull PageData data, @NonNull CurationFilter rules) {
        return applyRules(rules.getPageIdRules(), data.getId()) &&
            applyRules(rules.getPageNamespaceIdRules(), data.getNamespaceId()) &&
            applyRules(rules.getPageNamespaceNameRules(), data.getNamespaceName()) &&
            applyRules(rules.getPageTitleRules(), data.getTitle()) &&
            applyRules(rules.getPageRevisionIdRules(), data.getRevisionId()) &&
            applyRules(rules.getPageWikidataQidRules(), data.getWikidataItemQid()) &&
            applyRules(rules.getPageContentLanguageRules(), data.getContentLanguage());
    }

    private static boolean applyPerformerRules(@NonNull PerformerData data, @NonNull CurationFilter rules) {
        return applyRules(rules.getPerformerIdRules(), data.getId()) &&
            applyRules(rules.getPerformerNameRules(), data.getName()) &&
            applyRules(rules.getPerformerSessionIdRules(), data.getSessionId()) &&
            applyRules(rules.getPerformerPageviewIdRules(), data.getPageviewId()) &&
            applyRules(rules.getPerformerGroupsRules(), data.getGroups()) &&
            applyRules(rules.getPerformerIsLoggedInRules(), data.getIsLoggedIn()) &&
            applyRules(rules.getPerformerIsTempRules(), data.getIsTemp()) &&
            applyRules(rules.getPerformerRegistrationDtRules(), data.getRegistrationDt()) &&
            applyRules(rules.getPerformerLanguageGroupsRules(), data.getLanguageGroups()) &&
            applyRules(rules.getPerformerLanguagePrimaryRules(), data.getLanguagePrimary());
    }

    private static <T> boolean applyRules(CollectionCurationRules<T> rules, Collection<T> value) {
        if (rules == null) {
            return true;
        }
        if (value == null) {
            return false;
        }

        return applyContainsRule(rules.getContains(), value) &&
            applyDoesNotContainRule(rules.getDoesNotContain(), value) &&
            applyContainsAllRule(rules.getContainsAll(), value) &&
            applyContainsAnyRule(rules.getContainsAny(), value);
    }

    private static <T extends Comparable<T>> boolean applyRules(
        ComparableCurationRules<T> rules,
        T value
    ) {
        if (rules == null) {
            return true;
        }
        if (value == null) {
            return false;
        }

        return applyGreaterThanRule(rules.getGreaterThan(), value) &&
            applyLessThanRule(rules.getLessThan(), value) &&
            applyGreaterThanOrEqualsRule(rules.getGreaterThanOrEquals(), value) &&
            applyLessThanOrEquals(rules.getLessThanOrEquals(), value) &&
            applyIsEqualsRule(rules.getIsEquals(), value) &&
            applyIsNotEqualsRule(rules.getIsNotEquals(), value) &&
            applyInRule(rules.getIn(), value) &&
            applyNotInRule(rules.getNotIn(), value);
    }

    private static <T> boolean applyRules(CurationRules<T> rules, T value) {
        if (rules == null) return true;

        return rules.test(value);
    }

    private static <T> boolean applyIsEqualsRule(T rule, @NonNull T value) {
        return rule == null || value.equals(rule);
    }

    private static <T> boolean applyIsNotEqualsRule(T rule, @NonNull T value) {
        return rule == null || !value.equals(rule);
    }

    private static <T> boolean applyInRule(Collection<T> rule, @NonNull T value) {
        return rule == null || rule.contains(value);
    }

    private static <T> boolean applyNotInRule(Collection<T> rule, @NonNull T value) {
        return rule == null || !rule.contains(value);
    }

    private static <T extends Comparable<T>> boolean applyGreaterThanRule(T rule, @NonNull T value) {
        return rule == null || value.compareTo(rule) > 0;
    }

    private static <T extends Comparable<T>> boolean applyLessThanRule(T rule, @NonNull T value) {
        return rule == null || value.compareTo(rule) < 0;
    }

    private static <T extends Comparable<T>> boolean applyGreaterThanOrEqualsRule(T rule, @NonNull T value) {
        return rule == null || value.compareTo(rule) >= 0;
    }

    private static <T extends Comparable<T>> boolean applyLessThanOrEquals(T rule, @NonNull T value) {
        return rule == null || value.compareTo(rule) <= 0;
    }

    private static <T> boolean applyContainsRule(T rule, @NonNull Collection<T> value) {
        return rule == null || value.contains(rule);
    }

    private static <T> boolean applyDoesNotContainRule(T rule, @NonNull Collection<T> value) {
        return rule == null || !value.contains(rule);
    }

    private static <T> boolean applyContainsAllRule(Collection<T> rule, @NonNull Collection<T> value) {
        return rule == null || value.containsAll(rule);
    }

    private static <T> boolean applyContainsAnyRule(Collection<T> rule, @NonNull Collection<T> value) {
        if (rule == null) {
            return true;
        }

        Collection<T> t = new ArrayList<>(value);
        t.retainAll(rule);

        return !t.isEmpty();
    }
}
