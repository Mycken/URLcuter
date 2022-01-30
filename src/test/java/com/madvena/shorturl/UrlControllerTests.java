package com.madvena.shorturl;

import com.madvena.shorturl.util.Base62;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UrlControllerTests {
    @Autowired
    private MockMvc mvc;


    @Test
    @DisplayName(value = "Request is valid url, response starts with '/l/'")
    void shouldWorkWithValidUrl() throws Exception {
        String body = "{ \"original\" : \"http://google.com/search?newwindow=1&q=СКБ Контур\" }";
        mvc.perform(post("/generate")
                .contentType(APPLICATION_JSON_VALUE)
                .content(body)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.link", startsWith("/l/")));
    }

    @Test
    @DisplayName(value = "Throw 'bad request' when request is empty")
    void shouldNotAcceptEmptyUrl() throws Exception {
        String body = "{ \"original\" : \"\" }";
        mvc.perform(post("/generate")
                .contentType(APPLICATION_JSON_VALUE)
                .content(body)
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName(value = "Throw 'bad request' when request is incorrect")
    void shouldNotAcceptWrongUrlFormat() throws Exception {
        String body = "{ \"original\" : \"google.com\" }";
        mvc.perform(post("/generate")
                .contentType(APPLICATION_JSON_VALUE)
                .content(body)
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName(value = "Throw 'bad request' when request is not url")
    void shouldNotAcceptNotAUrl() throws Exception {
        String body = "{ \"original\" : \"bla-bla-bla\" }";
        mvc.perform(post("/generate")
                .contentType(APPLICATION_JSON_VALUE)
                .content(body)
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @DisplayName(value = "Response code is 300 after request with short link")
    void shouldRedirect() throws Exception {
        mvc.perform(get("/l/1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName(value = "Response code is 400 after request with incorrect link")
    void shouldNotAcceptWrongParameterFormat() throws Exception {
        mvc.perform(get("/l/_1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName(value = "Response code is 404 if service not found the short link")
    void shouldReturnNotFoundIfLinkNotExists() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/l/" + Base62.to(Integer.MAX_VALUE)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName(value = "Return statistic by id")
    void shouldReturnStatisticById() throws Exception {
        mvc.perform(get("/stats/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.link", is(equalTo("/l/1"))))
                .andExpect(jsonPath("$.original", is(equalTo("https://www.example.com/home"))))
                .andExpect(jsonPath("$.rank", is(equalTo(5))))
                .andExpect(jsonPath("$.count", is(equalTo(51979))));
    }

    @Test
    @DisplayName(value = "Return ordered by rank statistic for all records on first page")
    void shouldReturnStatisticByPage_firstPage() throws Exception {
        mvc.perform(get("/stats")
                .param("page", "0")
                .param("count", "10")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.length()", is(equalTo(10))))
                .andExpect(jsonPath("$[0].rank", is(equalTo(1))))
                .andExpect(jsonPath("$[9].rank", is(equalTo(10))));

    }

    @Test
    @DisplayName(value = "Return ordered by rank statistic for all records on last page")
    void shouldReturnStatisticByPage_lastPage() throws Exception {
        mvc.perform(get("/stats")
                .param("page", "1")
                .param("count", "10")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.length()", is(equalTo(5))))
                .andExpect(jsonPath("$[0].rank", is(equalTo(11))))
                .andExpect(jsonPath("$[4].rank", is(equalTo(15))))
        ;
    }

    @Test
    @DisplayName(value = "Return bad request when page less than 0")
    void shouldReturnBadRequestIfPageLessThanZero() throws Exception {
        mvc.perform(get("/stats")
                .param("page", "-1")
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName(value = "Return bad request when page size not positive")
    void shouldReturnBadRequestIfPageSizeNotPositiveNumber() throws Exception {
        mvc.perform(get("/stats")
                .param("count", "0")
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName(value = "Return bad request when page size greater than 100")
    void shouldReturnBadRequestIfPageSizeGreaterThan100() throws Exception {
        mvc.perform(get("/stats")
                .param("count", "101")
        )
                .andExpect(status().isBadRequest());
    }
}
