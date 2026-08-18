package io.github.pricescrawler.content.controller.changelog;

import io.github.pricescrawler.content.common.dao.changelog.ChangelogDao;
import io.github.pricescrawler.content.repository.changelog.ChangelogDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Read-only: entries are written directly against {@link ChangelogDataRepository} by an
 * admin UI (content-management), not through this API.
 */
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/changelog")
@ConditionalOnProperty("prices.crawler.controller.changelog.enabled")
public class ChangelogController {
    private final ChangelogDataRepository changelogDataRepository;

    @GetMapping
    public Flux<ChangelogDao> list() {
        return changelogDataRepository.findAllByOrderByDateDesc();
    }
}
