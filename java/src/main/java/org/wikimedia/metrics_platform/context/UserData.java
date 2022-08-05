package org.wikimedia.metrics_platform.context;

import java.util.Collection;

import com.google.gson.annotations.SerializedName;

/**
 * User context data context fields.
 *
 * All fields are nullable, and boxed types are used in place of their equivalent primitive types to avoid
 * unexpected default values from being used where the true value is null.
 */
public class UserData {
    private Integer id;
    private String name;
    private Collection<String> groups;
    @SerializedName("is_logged_in") private Boolean isLoggedIn;
    @SerializedName("is_bot") private Boolean isBot;
    @SerializedName("can_probably_edit_page") private Boolean canProbablyEditPage;
    @SerializedName("edit_count") private Integer editCount;
    @SerializedName("edit_count_bucket") private String editCountBucket;
    @SerializedName("registration_timestamp") private Long registrationTimestamp;
    private String language;
    @SerializedName("language_variant") private String languageVariant;

    public UserData() { }

    public UserData(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.groups = builder.groups;
        this.isLoggedIn = builder.isLoggedIn;
        this.isBot = builder.isBot;
        this.canProbablyEditPage = builder.canProbablyEditPage;
        this.editCount = builder.editCount;
        this.editCountBucket = builder.editCountBucket;
        this.registrationTimestamp = builder.registrationTimestamp;
        this.language = builder.language;
        this.languageVariant = builder.languageVariant;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(Boolean loggedIn) {
        this.isLoggedIn = loggedIn;
    }

    public Boolean isBot() {
        return isBot;
    }

    public void setIsBot(Boolean bot) {
        this.isBot = bot;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<String> getGroups() {
        return groups;
    }

    public void setGroups(Collection<String> groups) {
        this.groups = groups;
    }

    public Boolean canProbablyEditPage() {
        return canProbablyEditPage;
    }

    public void setCanProbablyEditPage(Boolean canProbablyEditPage) {
        this.canProbablyEditPage = canProbablyEditPage;
    }

    public Integer getEditCount() {
        return editCount;
    }

    public void setEditCount(Integer editCount) {
        this.editCount = editCount;
    }

    public String getEditCountBucket() {
        return editCountBucket;
    }

    public void setEditCountBucket(String editCountBucket) {
        this.editCountBucket = editCountBucket;
    }

    public Long getRegistrationTimestamp() {
        return registrationTimestamp;
    }

    public void setRegistrationTimestamp(Long registrationTimestamp) {
        this.registrationTimestamp = registrationTimestamp;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguageVariant() {
        return languageVariant;
    }

    public void setLanguageVariant(String languageVariant) {
        this.languageVariant = languageVariant;
    }

    public static class Builder {
        private Integer id;
        private String name;
        private Collection<String> groups;
        private Boolean isLoggedIn;
        private Boolean isBot;
        private Boolean canProbablyEditPage;
        private Integer editCount;
        private String editCountBucket;
        private Long registrationTimestamp;
        private String language;
        private String languageVariant;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder groups(Collection<String> groups) {
            this.groups = groups;
            return this;
        }

        public Builder isLoggedIn(Boolean isLoggedIn) {
            this.isLoggedIn = isLoggedIn;
            return this;
        }

        public Builder isBot(Boolean isBot) {
            this.isBot = isBot;
            return this;
        }

        public Builder canProbablyEditPage(Boolean canProbablyEditPage) {
            this.canProbablyEditPage = canProbablyEditPage;
            return this;
        }

        public Builder editCount(Integer editCount) {
            this.editCount = editCount;
            return this;
        }

        public Builder editCountBucket(String editCountBucket) {
            this.editCountBucket = editCountBucket;
            return this;
        }

        public Builder registrationTimestamp(Long registrationTimestamp) {
            this.registrationTimestamp = registrationTimestamp;
            return this;
        }

        public Builder language(String language) {
            this.language = language;
            return this;
        }

        public Builder languageVariant(String languageVariant) {
            this.languageVariant = languageVariant;
            return this;
        }

        public UserData build() {
            return new UserData(this);
        }
    }
}
