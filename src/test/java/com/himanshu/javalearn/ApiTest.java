package com.himanshu.javalearn;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiTest {

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession signIn(String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/guest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value(name))
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession();
    }

    @Test
    void catalogIsPublic() throws Exception {
        mockMvc.perform(get("/api/topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(6))))
                .andExpect(jsonPath("$[0].slug").value("java-basics"));
        mockMvc.perform(get("/api/problems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].slug", hasItem("two-sum")));
    }

    @Test
    void questionsAndProblemsDoNotLeakAnswers() throws Exception {
        mockMvc.perform(get("/api/topics/oop/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]", not(hasKey("correctOption"))));
        mockMvc.perform(get("/api/problems/two-sum"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(hasKey("referenceSolution"))))
                .andExpect(jsonPath("$.samples", hasSize(3)))
                .andExpect(jsonPath("$.params[0]").value("nums"));
    }

    @Test
    void personalEndpointsNeedSignIn() throws Exception {
        mockMvc.perform(get("/api/me/dashboard")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/lessons/1/complete")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/problems/two-sum/submit")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"code\":\"class Solution {}\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void guestSessionUnlocksDashboard() throws Exception {
        MockHttpSession session = signIn("Priya");

        mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provider").value("GUEST"));
        mockMvc.perform(post("/api/lessons/1/complete").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lessonsCompleted").value(1));
        mockMvc.perform(get("/api/me/dashboard").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Priya"))
                .andExpect(jsonPath("$.lessonsCompleted").value(1))
                .andExpect(jsonPath("$.available.easy", greaterThanOrEqualTo(1)));
        mockMvc.perform(get("/api/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].displayName", hasItem("Priya")));
    }

    @Test
    void invalidGuestNameIsRejected() throws Exception {
        mockMvc.perform(post("/api/auth/guest").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"<script>\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/auth/guest").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"a\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void googleIsOffWithoutCredentials() throws Exception {
        mockMvc.perform(get("/api/auth/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.google").value(false));
    }

    @Test
    void clientRoutesServeTheReactApp() throws Exception {
        mockMvc.perform(get("/problems/two-sum")).andExpect(forwardedUrl("/index.html"));
        mockMvc.perform(get("/app")).andExpect(forwardedUrl("/index.html"));
    }

    @Test
    void missingTopicReturns404() throws Exception {
        mockMvc.perform(get("/api/topics/kotlin"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Topic not found: kotlin"));
    }
}
