package io.github.pricescrawler.content.controller.feedback;

import io.github.pricescrawler.content.common.dao.feedback.FeedbackDao;
import io.github.pricescrawler.content.common.util.DateTimeUtils;
import io.github.pricescrawler.content.repository.feedback.FeedbackDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Write-only: feedback is never read back through this API, only stored — check the
 * "feedback" Mongo collection directly to review submissions.
 */
@Log4j2
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feedback")
@ConditionalOnProperty("prices.crawler.controller.feedback.enabled")
public class FeedbackController {
    private static final Set<String> ALLOWED_TYPES = Set.of("bug", "suggestion", "other");
    private static final int MAX_MESSAGE_LENGTH = 2000;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final FeedbackDataRepository feedbackDataRepository;

    @PostMapping
    public Mono<Void> submit(@RequestBody FeedbackRequest request) {
        validate(request);

        var now = DateTimeUtils.getCurrentDateTime();
        var feedback = FeedbackDao.builder()
                .type(request.type())
                .message(request.message().trim())
                .email(request.email() == null || request.email().isBlank() ? null : request.email().trim())
                .created(now)
                .updated(now)
                .build();

        return feedbackDataRepository.save(feedback)
                .doOnError(e -> log.error("Error saving feedback. Error message: {}", e.getMessage()))
                .then();
    }

    private void validate(FeedbackRequest request) {
        if (request == null || request.type() == null || !ALLOWED_TYPES.contains(request.type())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid feedback type");
        }
        if (request.message() == null || request.message().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message is required");
        }
        if (request.message().length() > MAX_MESSAGE_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message is too long");
        }
        if (request.email() != null && !request.email().isBlank() && !EMAIL_PATTERN.matcher(request.email()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email");
        }
    }
}
