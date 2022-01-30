package com.madvena.shorturl;

import com.madvena.shorturl.model.Url;
import com.madvena.shorturl.repository.UrlRepository;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UrlRepositoryTest {

    @Autowired
    private UrlRepository repository;

    @Test
    @DisplayName(value = "Return expected url by id")
    void findByIdWithRankTest() {
        data().forEach(expected -> {
            Optional<Url> urlOptional = repository.findById(expected.getId());
            assertTrue(urlOptional.isPresent());
            Url url = urlOptional.get();
            assertThat(url, ulrAreEqual(expected));
        });
    }

    @Test
    @DisplayName(value = "Return all url by page")
    void findAllWithRankTest() {
        Collection<Url> data = data();
        int pageSize = 10;
        int pageCount = (int) Math.ceil(1.0 * data.size() / pageSize);
        for (int page = 0; page < pageCount; ++page) {
            Iterator<Url> expectedData = data.stream().skip(pageSize * page).limit(pageSize).iterator();
            findAllWithRankTestBody(PageRequest.of(page, pageSize), expectedData);
        }
    }

    private void findAllWithRankTestBody(Pageable pageable, Iterator<Url> expectedData) {
        Iterator<Url> actualData = repository.findAll(pageable).get().iterator();
        while (expectedData.hasNext() && actualData.hasNext()) {
            assertThat(actualData.next(), ulrAreEqual(expectedData.next()));
        }
        assertEquals(expectedData.hasNext(), actualData.hasNext());
    }

    private Matcher<Url> ulrAreEqual(Url expected) {
        return new BaseMatcher<Url>() {
            @Override
            public boolean matches(final Object o) {
                Url actual = (Url) o;
                return Objects.equals(expected.getId(), actual.getId()) &&
                        Objects.equals(expected.getCount(), actual.getCount()) &&
                        Objects.equals(expected.getOriginal(), actual.getOriginal());
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("must be equal to ").appendValue(expected);
            }
        };
    }
    public static List<Url> data() {
        // Data form data.sql ordered by rank.
        return Arrays.asList(
                new Url(15, "https://vk.com/id0", 1, 96520),
                new Url(6, "https://ya.ru", 2, 82871),
                new Url(3, "https://www.example.com/news", 3, 77205),
                new Url(9, "https://google.ru/1", 4, 59070),
                new Url(1, "https://www.example.com/home", 5, 51979),
                new Url(14, "https://vk.com/blog", 6, 37663),
                new Url(2, "https://www.example.com/about", 7, 37549),
                new Url(10, "https://kontur.ru", 8, 24588),
                new Url(5, "https://yandex.ru", 9, 24129),
                new Url(8, "https://google.com", 10, 23152),
                new Url(11, "https://vk.com", 11, 20873),
                new Url(4, "https://www.example.com/search?q=something", 12, 11413),
                new Url(12, "https://vk.com/feed", 13, 7789),
                new Url(13, "https://vk.com/im", 14, 5579),
                new Url(7, "https://google.ru", 15, 241));
    }
}


