package com.himanshu.javalearn;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasKey;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listsTopics() throws Exception {
        mockMvc.perform(get("/api/topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[0].slug").value("java-basics"));
    }

    @Test
    void questionsDoNotLeakTheAnswer() throws Exception {
        mockMvc.perform(get("/api/topics/oop/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]", not(hasKey("correctOption"))));
    }

    @Test
    void missingTopicReturns404() throws Exception {
        mockMvc.perform(get("/api/topics/kotlin"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Topic not found: kotlin"));
    }

    @Test
    void blankUsernameIsRejected() throws Exception {
        mockMvc.perform(post("/api/lessons/1/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"  \"}"))
                .andExpect(status().isBadRequest());
    }
}
