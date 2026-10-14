package oit.is.z3541.kaizi.janken;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class JankenEntryTests {

  @Autowired
  private MockMvc mvc;

  @Test
  void independentSessionsShareEntriesAndKeepTheirOwnLoginNames() throws Exception {
    MockHttpSession user1 = login("user1");
    mvc.perform(get("/janken").session(user1))
        .andExpect(status().isOk())
        .andExpect(model().attribute("login_user", "user1"))
        .andExpect(model().attribute("entry", hasProperty("users", contains("user1"))))
        .andExpect(content().string(containsString("エントリー")))
        .andExpect(content().string(containsString("Hi user1")));

    MockHttpSession user2 = login("user2");
    mvc.perform(get("/janken").session(user2))
        .andExpect(status().isOk())
        .andExpect(model().attribute("login_user", "user2"))
        .andExpect(model().attribute("entry", hasProperty("users", contains("user1", "user2"))))
        .andExpect(content().string(containsString("Hi user2")));

    // 再表示しても重複せず、リクエストの名前でログインユーザを偽装できない。
    mvc.perform(get("/janken").session(user1).param("userName", "someoneElse"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("login_user", "user1"))
        .andExpect(model().attribute("entry", hasProperty("users", contains("user1", "user2"))))
        .andExpect(content().string(containsString("Hi user1")));

    String[][] hands = { { "Gu", "グー", "あいこ" }, { "Choki", "チョキ", "負け" }, { "Pa", "パー", "勝ち" } };
    for (String[] hand : hands) {
      mvc.perform(get("/jankengame").session(user1).param("hand", hand[0]))
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("Hi user1")))
          .andExpect(content().string(containsString("あなたの手：" + hand[1])))
          .andExpect(content().string(containsString("相手の手：グー")))
          .andExpect(content().string(containsString("結果：" + hand[2])))
          .andExpect(model().attribute("entry", hasProperty("users", contains("user1", "user2"))))
          .andExpect(content().string(containsString("href=\"/logout\"")));
    }

    mvc.perform(get("/jankengame").session(user1)).andExpect(status().isBadRequest());
    mvc.perform(get("/jankengame").session(user1).param("hand", "Lizard"))
        .andExpect(status().isBadRequest());
  }

  private MockHttpSession login(String username) throws Exception {
    return (MockHttpSession) mvc.perform(formLogin().user(username).password("isdev"))
        .andExpect(authenticated().withUsername(username))
        .andReturn().getRequest().getSession(false);
  }
}
