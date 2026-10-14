package oit.is.z3541.kaizi.janken;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class JankenSecurityTests {

  @Autowired
  private MockMvc mvc;

  @Test
  void topPageIsPublic() throws Exception {
    mvc.perform(get("/")).andExpect(status().isOk());
    mvc.perform(get("/index.html")).andExpect(status().isOk());
  }

  @ParameterizedTest
  @ValueSource(strings = { "/janken", "/jankengame?hand=Gu", "/janken/other", "/jankenother" })
  void jankenUrlsRequireLogin(String url) throws Exception {
    mvc.perform(get(url))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/login"));
  }

  @ParameterizedTest
  @ValueSource(strings = { "user1", "user2" })
  void bothUsersCanLogIn(String username) throws Exception {
    mvc.perform(formLogin().user(username).password("isdev"))
        .andExpect(authenticated().withUsername(username).withRoles("USER"))
        .andExpect(redirectedUrl("/"));
  }

  @Test
  void wrongPasswordIsRejected() throws Exception {
    mvc.perform(formLogin().user("user1").password("wrong"))
        .andExpect(unauthenticated())
        .andExpect(redirectedUrl("/login?error"));
  }

  @Test
  void logoutRequiresConfirmationAndCsrfToken() throws Exception {
    MockHttpSession session = (MockHttpSession) mvc
        .perform(formLogin().user("user1").password("isdev"))
        .andReturn().getRequest().getSession(false);

    mvc.perform(get("/logout").session(session))
        .andExpect(status().isOk())
        .andExpect(authenticated());
    mvc.perform(post("/logout").session(session))
        .andExpect(status().isForbidden())
        .andExpect(authenticated());
    mvc.perform(post("/logout").session(session).with(csrf()))
        .andExpect(unauthenticated())
        .andExpect(redirectedUrl("/"));
  }
}
