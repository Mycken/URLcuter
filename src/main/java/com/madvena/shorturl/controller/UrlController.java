package com.madvena.shorturl.controller;

import com.madvena.shorturl.model.dto.LinkResponseDto;
import com.madvena.shorturl.model.dto.StatisticDto;
import com.madvena.shorturl.model.dto.UrlRequestDto;
import com.madvena.shorturl.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static com.madvena.shorturl.service.UrlService.LINK_PREFIX;

@RestController
@Validated

public class UrlController {

    private final UrlService service;

    @Autowired
    public UrlController(UrlService service) {
        this.service = service;
    }

    @PostMapping(value = "/generate")
    @Operation(summary = "Generate short link",
            description = "Produce string variable from id of record in table with url",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Short link",
                            content = @Content(mediaType = "text/plain",
                                    schema = @Schema(implementation = LinkResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid URL"),
                    @ApiResponse(responseCode = "405", description = "Validation error")}
    )
    LinkResponseDto generate(@RequestBody @Valid UrlRequestDto url) {
        LinkResponseDto link = new LinkResponseDto();
        link.setLink(LINK_PREFIX + service.getShortUrl(url.getOriginal()));
        return link;
    }

    @GetMapping(LINK_PREFIX + "{link}")
    @Operation(summary = "Redirect by short link",
            description = "Redirect by short link corresponded of record in table with url",
            responses = {
                    @ApiResponse(responseCode = "404", description = "Invalid link",
                            content = @Content(mediaType = "text/plain",
                                    schema = @Schema(implementation = LinkResponseDto.class)))}
    )
    RedirectView redirect(@PathVariable String link) {
        return new RedirectView(service.doRedirect(link));
    }

    @GetMapping("/stats" + "/{link}")
    @Operation(summary = "Link statistic",
            description = "Return statistic data for one link",
            responses = {
                    @ApiResponse(responseCode = "404", description = "Invalid link",
                            content = @Content(mediaType = "text/plain",
                                    schema = @Schema(implementation = LinkResponseDto.class)))})
    StatisticDto statsOfLink(@PathVariable String link) {
        return service.getRankedUrlByShort(link);
    }

    @GetMapping("/stats")
    @Operation(summary = "All Link statistic",
            description = "Return statistic data for all links by pages",
            responses = {
                    @ApiResponse(responseCode = "404", description = "Invalid link",
                            content = @Content(mediaType = "text/plain",
                                    schema = @Schema(implementation = LinkResponseDto.class)))})
    List<StatisticDto> rankedUrl(
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
            @RequestParam(defaultValue = "100") @Min(1) @Max(100) Integer count
    ) {
        return service.getRankedUrl(PageRequest.of(page, count));
    }
}
