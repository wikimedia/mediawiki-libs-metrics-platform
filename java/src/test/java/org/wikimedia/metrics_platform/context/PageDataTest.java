package org.wikimedia.metrics_platform.context;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

class PageDataTest {

    @Test void testPageData() {
        PageData pageData = PageData.builder()
                .id(1)
                .namespaceId(0)
                .namespaceText("")
                .title("Test")
                .isRedirect(false)
                .revisionId(1)
                .wikidataItemId("Q1")
                .contentLanguage("zh")
                .groupsAllowedToEdit(Collections.emptyList())
                .groupsAllowedToMove(Collections.emptyList())
                .build();

        assertThat(pageData.getId()).isEqualTo(1);
        assertThat(pageData.getNamespaceId()).isEqualTo(0);
        assertThat(pageData.getNamespaceText()).isEmpty();
        assertThat(pageData.getTitle()).isEqualTo("Test");
        assertThat(pageData.getIsRedirect()).isFalse();
        assertThat(pageData.getRevisionId()).isEqualTo(1);
        assertThat(pageData.getWikidataItemId()).isEqualTo("Q1");
        assertThat(pageData.getContentLanguage()).isEqualTo("zh");
        assertThat(pageData.getGroupsAllowedToEdit()).isEmpty();
        assertThat(pageData.getGroupsAllowedToMove()).isEmpty();

        Gson gson = new Gson();
        String json = gson.toJson(pageData);
        assertThat(json).isEqualTo("{\"id\":1,\"namespace_id\":0,\"namespace_text\":\"\",\"title\":\"Test\"," +
                "\"is_redirect\":false,\"revision_id\":1,\"wikidata_id\":\"Q1\",\"content_language\":\"zh\"," +
                "\"user_groups_allowed_to_edit\":[],\"user_groups_allowed_to_move\":[]}");
    }

}
