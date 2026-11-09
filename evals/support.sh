# Evals for the support bot. Build once with ./mvnw -q package, then run ./tinyeval.
# The worker starts the JVM and the client once, and answers every test case sent to it.
worker java -cp "target/classes:target/dependency/*" com.example.support.EvalWorker

test_case "refund after the window" "Can I still return my shoes? They arrived 40 days ago."
  target src/main/java/com/example/support/Support.java#answer
  matches "30[- ]day"   # "30 days" and "30-day window" are both right
  judge "Says a refund isn't possible anymore, politely"

test_case "missing package" "My package never showed up and I'm getting really annoyed."
  target src/main/java/com/example/support/Support.java#answer
  matches "order (number|id|#)"
  judge "Asks for the order number before promising a refund or resend"

test_case "refund inside the window" "My shoes arrived last week and they don't fit. Can I send them back?"
  target src/main/java/com/example/support/Support.java#answer
  judge "Says they can return them for a refund"
