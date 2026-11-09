package com.example.support;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.anthropic.errors.AnthropicIoException;
import com.anthropic.errors.InternalServerException;
import com.anthropic.errors.RateLimitException;
import com.anthropic.errors.UnauthorizedException;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * tinyeval's worker: it reads each input up to a NUL byte on stdin, and prints the reply and a NUL byte.
 * The app starts once per worker, not once per test case. Keep logs off stdout.
 */
public final class EvalWorker {
    public static void main(String[] args) throws IOException {
        var support = new Support();
        var in = new BufferedInputStream(System.in);
        var input = new ByteArrayOutputStream();
        for (int b; (b = in.read()) != -1; ) {
            if (b != 0) {
                input.write(b);
                continue;
            }
            System.out.write(reply(support, input.toString(UTF_8)).getBytes(UTF_8));
            System.out.write(0);
            System.out.flush();
            input.reset();
        }
    }

    // A call that fails for reasons outside the app counts as "couldn't run", not as a failed eval.
    static String reply(Support support, String input) {
        try {
            return support.answer(input);
        } catch (UnauthorizedException | RateLimitException | InternalServerException | AnthropicIoException e) {
            return "#tinyeval couldnt_run " + e.getClass().getSimpleName() + "\n";
        }
    }
}
