package com.example.support;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.OutputConfig;
import com.anthropic.models.messages.TextBlock;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/** A support bot for a web shop. Build it once: it holds the HTTP client. */
public final class Support {
    private final AnthropicClient client = AnthropicOkHttpClient.fromEnv(); // reads ANTHROPIC_API_KEY
    private final String systemPrompt;

    public Support() throws IOException {
        systemPrompt = Files.readString(Path.of("prompts/support.md"));
    }

    public String answer(String message) {
        var params = MessageCreateParams.builder()
                .model("claude-opus-5-5")
                .maxTokens(1024L)
                // Opus 5.5 always thinks; low effort is enough for a short reply.
                .outputConfig(OutputConfig.builder().effort(OutputConfig.Effort.LOW).build())
                .system(systemPrompt)
                .addUserMessage(message)
                .build();
        return client.messages().create(params).content().stream()
                .flatMap(block -> block.text().stream())
                .map(TextBlock::text)
                .collect(Collectors.joining());
    }
}
