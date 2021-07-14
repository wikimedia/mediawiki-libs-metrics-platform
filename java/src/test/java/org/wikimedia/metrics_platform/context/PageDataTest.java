package org.wikimedia.metrics_platform.context;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PageDataTest {

    @Test
    public void testPageData() {
        PageData pageData = new PageData.Builder()
                .id(1)
                .namespaceId(0)
                .namespaceText("")
                .title("Test")
                .isRedirect(false)
                .revisionId(1)
                .wikidataId("Q1")
                .contentLanguage("zh")
                .groupsAllowedToEdit(Collections.emptyList())
                .groupsAllowedToMove(Collections.emptyList())
                .build();

        assertThat(pageData.getId(), is(1));
        assertThat(pageData.getNamespaceId(), is(0));
        assertThat(pageData.getNamespaceText(), is(""));
        assertThat(pageData.getTitle(), is("Test"));
        assertThat(pageData.isRedirect(), is(false));
        assertThat(pageData.getRevisionId(), is(1));
        assertThat(pageData.getWikidataItemId(), is("Q1"));
        assertThat(pageData.getContentLanguage(), is("zh"));
        assertThat(pageData.getGroupsAllowedToEdit(), is(Collections.emptyList()));
        assertThat(pageData.getGroupsAllowedToMove(), is(Collections.emptyList()));

        Gson gson = new Gson();
        String json = gson.toJson(pageData);
        assertThat(json, is("{\"id\":1,\"namespace_id\":0,\"namespace_text\":\"\",\"title\":\"Test\"," +
                "\"is_redirect\":false,\"revision_id\":1,\"wikidata_id\":\"Q1\",\"content_language\":\"zh\"," +
                "\"user_groups_allowed_to_edit\":[],\"user_groups_allowed_to_move\":[]}"));
    }

}
