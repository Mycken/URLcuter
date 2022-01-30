package com.madvena.shorturl.service;

import com.madvena.shorturl.model.dto.StatisticDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Pageable;

import java.util.List;

@PropertySource("classpath:link.properties")
public interface UrlService {
    @Value("${PREFIX}")
    String LINK_PREFIX = "/l/";

    String getShortUrl(String original);

    String doRedirect(String link);

    List<StatisticDto> getRankedUrl(Pageable pageable);

    StatisticDto getRankedUrlByShort(String link);
}
