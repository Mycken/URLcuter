package com.madvena.shorturl;

import com.madvena.shorturl.exception.LinkNotFoundException;
import com.madvena.shorturl.exception.WrongLinkException;
import com.madvena.shorturl.model.Url;
import com.madvena.shorturl.model.dto.StatisticDto;
import com.madvena.shorturl.repository.UrlRepository;

import com.madvena.shorturl.service.impl.UrlServiceImpl;
import com.madvena.shorturl.util.Base62;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.madvena.shorturl.util.Base62.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository repository;

    @InjectMocks
    private UrlServiceImpl service;

    private static final Url RANKED_URL = new Url(1, "https://kontur.ru", 1, 0);

    @Test
    @DisplayName(value = "Generate new link from url")
    void shouldGenerateNewShortUrls() {
        doAnswer(invocationOnMock -> {
            Url url = invocationOnMock.getArgument(0);
            url.setId(1);
            return null;
        }).when(repository).save(any());

        String actual = service.getShortUrl("https://kontur.ru");

        verify(repository).save(any());
        assertThat(actual, is(to(1)));
    }

    @Test
    @DisplayName(value = "Return url for redirect")
    void shouldDoRedirect() {
        String expectedUrl = "http://kontur.ru";
        Url url = mock(Url.class);
        when(repository.findById(1))
                .thenReturn(Optional.of(url));
        when(url.getOriginal())
                .thenReturn(expectedUrl);
        String actualUrl = service.doRedirect("1");

        assertThat(expectedUrl, is(actualUrl));
        verify(repository).findById(1);

    }

    @Test
    @DisplayName(value = "Throw Exception when link not present in table")
    void doRedirectShouldThrowIfLinkNotPresentedInRepository() {
        assertThrows(LinkNotFoundException.class, () -> {
            when(repository.findById(1))
                    .thenReturn(Optional.empty());

            service.doRedirect("1");
        });
    }

    @Test
    @DisplayName(value = "Return expected url by id - repository")
    void shouldReturnRankedUrlByShortLink() {
        StatisticDto expected = new StatisticDto(RANKED_URL);

        when(repository.findById(1))
                .thenReturn(Optional.of(RANKED_URL));
        StatisticDto actual = service.getRankedUrlByShort((Base62.to(1)));

        verify(repository).findById(1);
        assertThat(actual.getCount(), is(RANKED_URL.getCount()));
        assertThat(actual.getOriginal(), is(RANKED_URL.getOriginal()));
        assertThat(actual.getRank(), is(RANKED_URL.getRank()));
        assertThat(actual.getLink(), is("/l/" + to(RANKED_URL.getId())));
    }

    @Test

    void getRankedUrlByShortLinkShouldThrowIfLinkNotPresentedInRepository() {
        assertThrows(LinkNotFoundException.class, () -> {
            when(repository.findById(1))
                    .thenReturn(Optional.empty());

            service.getRankedUrlByShort(to(1));
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnAllRankedUrl() {
        Pageable pageRequest = PageRequest.of(1, 1);
        Page<Url> page = mock(Page.class);

        when(repository.findAll(pageRequest)).thenReturn(page);
        when(page.get()).thenReturn(Stream.of(RANKED_URL));

        List<StatisticDto> actual = service.getRankedUrl(pageRequest);

        verify(repository).findAll(pageRequest);
        assertThat(actual, hasSize(1));
        StatisticDto urlStatistic = actual.get(0);
        assertThat(urlStatistic.getCount(), is(RANKED_URL.getCount()));
        assertThat(urlStatistic.getOriginal(), is(RANKED_URL.getOriginal()));
        assertThat(urlStatistic.getRank(), is(RANKED_URL.getRank()));
        assertThat(urlStatistic.getLink(), is("/l/" + to(RANKED_URL.getId())));
    }

    @Test
    void doRedirectShouldThrowIfWrongLinkFormat() {
        assertThrows(WrongLinkException.class, () -> service.doRedirect("_asd_"));
    }

    @Test
    void getRankedUrlByShortLinkShouldThrowIfWrongLinkFormat() {
        assertThrows(WrongLinkException.class, () -> service.getRankedUrlByShort("_asd_"));
    }

}
