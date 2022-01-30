package com.madvena.shorturl.model.dto;

import com.madvena.shorturl.controller.UrlController;
import com.madvena.shorturl.model.Url;
import com.madvena.shorturl.service.UrlService;
import com.madvena.shorturl.util.Base62;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticDto {
    private String link;
    private String original;
    private Integer rank;
    private Integer count;


    public StatisticDto(Url url) {
        this.link = UrlService.LINK_PREFIX + Base62.to(url.getId());
        this.original = url.getOriginal();
        this.rank = url.getRank();
        this.count = url.getCount();
    }
}
