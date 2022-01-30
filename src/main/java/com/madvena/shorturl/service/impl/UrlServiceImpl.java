package com.madvena.shorturl.service.impl;

import com.madvena.shorturl.exception.LinkNotFoundException;
import com.madvena.shorturl.exception.WrongLinkException;
import com.madvena.shorturl.model.Url;
import com.madvena.shorturl.model.dto.StatisticDto;
import com.madvena.shorturl.repository.UrlRepository;
import com.madvena.shorturl.service.UrlService;
import com.madvena.shorturl.util.Base62;
import com.madvena.shorturl.exception.Base62Exception;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

import java.util.*;

import static com.madvena.shorturl.util.Base62.*;

@Slf4j
@Service
@Data
@Transactional

public class UrlServiceImpl implements UrlService {

    private final UrlRepository repository;

    @Autowired
    public UrlServiceImpl(UrlRepository repository) {
        this.repository = repository;
    }

    /**
     * Static method Base62.to() from package 'com.madvena.shorturl.util' return
     * string representation of the URL index translated into a 62-bit calculus system
     */
    @Override
    public String getShortUrl(String original) {
        Url url = repository.findByOriginal(original).orElse(new Url(original));
        repository.save(url);
        String link = to(url.getId());

        log.info("The short link '/l/{}' is derived from the url {} ", link, original);

        this.toRank();

        return link;
    }

    @Override
    public String doRedirect(String link) {
        try {
            int id = Base62.from(link);
            Url url = repository
                    .findById(id)
                    .orElseThrow(() -> new LinkNotFoundException(link));

            url.setCount(url.getCount() + 1);
            repository.save(url);

            log.info("New redirection made by short link {}", link);

            this.toRank();

            return url.getOriginal();
        } catch (Base62Exception e) {
            throw new WrongLinkException(link, e);
        }
    }

    @Override
    public List<StatisticDto> getRankedUrl(Pageable pageable) {
        List<StatisticDto> result = new ArrayList<>();
        repository.findAll(pageable).get()
                .forEach(url -> result.add(new StatisticDto(url))
                );
        return result;
    }

    void toRank() {
        List<Url> list = repository.findAll();

        repository.findAll()
                .forEach(url -> {
                    url.setRank(list.indexOf(url) + 1);
                });
    }


    @Override
    public StatisticDto getRankedUrlByShort(String link) {

        try {
            Url url = repository.findById(Base62.from(link))
                    .orElseThrow(() -> new LinkNotFoundException(link));
            return new StatisticDto(url);

        } catch (Base62Exception e) {
            throw new WrongLinkException(link, e);
        }
    }
}
