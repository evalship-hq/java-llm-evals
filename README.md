# LLM evals for Java

A Java support bot whose AI call is tested on every pull request with [tinyeval](https://tinyeval.com).

## Run it

Clone this repo or use it as a template. You need Java 21; `./mvnw` downloads Maven for you.

```sh
export ANTHROPIC_API_KEY=sk-ant-...
./mvnw -q package   # again after each change to the Java code
./tinyeval
```

```
support
  ✓ refund after the window 5.5s
  ✓ missing package 5.1s
  ✓ refund inside the window 5.2s

All 3 evals passed (eval-results.json)
```

## What's here

- `src/main/java/com/example/support/Support.java`: the bot. `answer(message)` sends the message to Claude with the system prompt.
- `prompts/support.md`: the system prompt.
- `src/main/java/com/example/support/EvalWorker.java`: starts the bot once and answers each test case tinyeval sends it.
- `evals/support.sh`: the test cases, with the checks each reply must pass.
- `tinyeval`: the eval runner, a single bash file.
- `.github/workflows/evals.yml`: builds the app and runs the evals on every pull request.
- `pom.xml` and `mvnw`: the Maven build, with the official Anthropic Java SDK.

## Use it for your app

Replace `answer` with your own AI call. Keep the worker: tinyeval then starts your app once, not once per test case. A failed API call (no key, rate limit, server error, network) makes the worker reply `#tinyeval couldnt_run`, so tinyeval counts it as couldn't run rather than as a failed eval.

Write your test cases in `evals/`. Each one lists the checks its reply must pass, such as `matches` for a regex and `judge` for a criterion a model grades. See [all check lines](https://github.com/evalship-hq/tinyeval#writing-evals).

In a Spring Boot app, start the application context once in the worker's `main` with `SpringApplication.run`, then `getBean` your service and call it in the loop. Keep the banner and console logs off stdout, since stdout carries the replies.

## In CI

Add `ANTHROPIC_API_KEY` as a repository secret. The judge uses the same key. The workflow builds the app, runs each test case 3 times and passes it when at least half of the runs pass. A failing eval fails the job, so you can make it a required check. The results go up as the `eval-results` artifact. Without the secret, the workflow skips the evals and says so in a notice.

## Compare pull requests with main

A fresh CI run has no earlier run to compare with. [evalship](https://evalship.com) reads `eval-results.json` as is, compares each pull request with main, tells flaky evals from real drops and comments on the pull request.

## License

MIT
