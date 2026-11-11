package com.example.support;

import com.evalship.tinyeval.Tinyeval;

/**
 * tinyeval's worker: it starts the bot once and answers each test case tinyeval sends it.
 * An API call that fails for reasons outside the app (no key, rate limit, server error, network) counts as
 * "couldn't run"; an exception from the app's own code becomes the reply, so the eval fails.
 */
public final class EvalWorker {
    private static Support support;

    public static void main(String[] args) {
        Tinyeval.worker(input -> {
            if (support == null) support = new Support(); // built inside the worker, so its output stays off stdout
            return support.answer(input);
        });
    }
}
