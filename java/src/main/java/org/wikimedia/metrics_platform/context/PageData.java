package org.wikimedia.metrics_platform.context;

import com.google.gson.annotations.SerializedName;

import java.util.Collection;

/**
 * Page context data context fields.
 *
 * All fields are nullable, and boxed types are used in place of their equivalent primitive types to avoid
 * unexpected default values from being used where the true value is null.
 */
public class PageData {
    private Integer id;
    @SerializedName("namespace_id") private Integer namespaceId;
    @SerializedName("namespace_text") private String namespaceText;
    private String title;
    @SerializedName("is_redirect") private Boolean isRedirect;
    @SerializedName("revision_id") private Integer revisionId;
    @SerializedName("wikidata_id") private String wikidataItemId;
    @SerializedName("content_language") private String contentLanguage;
    @SerializedName("user_groups_allowed_to_edit") private Collection<String> groupsAllowedToEdit;
    @SerializedName("user_groups_allowed_to_move") private Collection<String> groupsAllowedToMove;

    public PageData() { }

    public PageData(Builder builder) {
        this.id = builder.id;
        this.namespaceId = builder.namespaceId;
        this.namespaceText = builder.namespaceText;
        this.title = builder.title;
        this.isRedirect = builder.isRedirect;
        this.revisionId = builder.revisionId;
        this.wikidataItemId = builder.wikidataItemId;
        this.contentLanguage = builder.contentLanguage;
        this.groupsAllowedToEdit = builder.groupsAllowedToEdit;
        this.groupsAllowedToMove = builder.groupsAllowedToMove;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNamespaceId() {
        return namespaceId;
    }

    public void setNamespaceId(Integer namespaceId) {
        this.namespaceId = namespaceId;
    }

    public String getNamespaceText() {
        return namespaceText;
    }

    public void setNamespaceText(String namespaceText) {
        this.namespaceText = namespaceText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean isRedirect() {
        return isRedirect;
    }

    public void setIsRedirect(Boolean redirect) {
        this.isRedirect = redirect;
    }

    public Integer getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(Integer revisionId) {
        this.revisionId = revisionId;
    }

    public String getWikidataItemId() {
        return wikidataItemId;
    }

    public void setWikidataItemId(String wikidataItemId) {
        this.wikidataItemId = wikidataItemId;
    }

    public String getContentLanguage() {
        return contentLanguage;
    }

    public void setContentLanguage(String contentLanguage) {
        this.contentLanguage = contentLanguage;
    }

    public Collection<String> getGroupsAllowedToEdit() {
        return groupsAllowedToEdit;
    }

    public void setGroupsAllowedToEdit(Collection<String> groupsAllowedToEdit) {
        this.groupsAllowedToEdit = groupsAllowedToEdit;
    }

    public Collection<String> getGroupsAllowedToMove() {
        return groupsAllowedToMove;
    }

    public void setGroupsAllowedToMove(Collection<String> groupsAllowedToMove) {
        this.groupsAllowedToMove = groupsAllowedToMove;
    }

    public static class Builder {
        private Integer id;
        private Integer namespaceId;
        private String namespaceText;
        private String title;
        private Boolean isRedirect;
        private Integer revisionId;
        private String wikidataItemId;
        private String contentLanguage;
        private Collection<String> groupsAllowedToEdit;
        private Collection<String> groupsAllowedToMove;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder namespaceId(Integer namespaceId) {
            this.namespaceId = namespaceId;
            return this;
        }

        public Builder namespaceText(String namespaceText) {
            this.namespaceText = namespaceText;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder isRedirect(Boolean isRedirect) {
            this.isRedirect = isRedirect;
            return this;
        }

        public Builder revisionId(Integer revisionId) {
            this.revisionId = revisionId;
            return this;
        }

        public Builder wikidataId(String wikidataId) {
            this.wikidataItemId = wikidataId;
            return this;
        }

        public Builder contentLanguage(String contentLanguage) {
            this.contentLanguage = contentLanguage;
            return this;
        }

        public Builder groupsAllowedToEdit(Collection<String> groupsAllowedToEdit) {
            this.groupsAllowedToEdit = groupsAllowedToEdit;
            return this;
        }

        public Builder groupsAllowedToMove(Collection<String> groupsAllowedToMove) {
            this.groupsAllowedToMove = groupsAllowedToMove;
            return this;
        }

        public PageData build() {
            return new PageData(this);
        }
    }
}
