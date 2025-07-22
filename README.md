# Transaction Normalizer

Demo backend application normalizing financial transaction data.

## Setup

**Prerequisites**

- Java 21
- Gradle

**Run application:**
```
./gradlew bootRun
```

**Run tests:**
```
./gradlew test
```

## Architecture

- **Language:** Kotlin
- **Framework:** Spring Boot
- **Paradigm:** Reactive Programming via [Spring WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- **Persistence:** None / In-Memory
- **Logging:** [kotlin-logging](https://github.com/oshai/kotlin-logging)
- **Caching:** [Caffeine](https://github.com/ben-manes/caffeine)

### Package Structure

The application is layered following the [hexagonal architecture](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)) (aka. ports and adapters architecture).

- **api:** public facing API exposing application features
- **application:** wiring everything together 
- **domain:** core business models and logic
- **infrastructure:** dependencies to external services

### Transaction Normalizers

Uploaded transaction data gets processed by a set of **normalizers**.
Each one of them gets passed the data of one transaction and calculates one value from that.
The normalizer specifies which property this value should be assigned to.
The managing service calls all normalizers and assigns the returned values to the respective properties before continuing.

- **AmountInEurNormalizer:** Calculates the amount of the transaction in EUR. Fetches currency rates from https://frankfurter.dev. Requests get cached for a short duration to avoid spamming the API.
- **PurposeNormalizer:** Removes prefixes from the original `purpose`
- **TransactionTypeNormalizer:** Matches the `transactionCode` against hard-coded patterns to determine a human-readable transaction type.

The implementation relies on the reflection API.
This comes with certain advantages and disadvantages:

**Advantages**

- Data classes can be used; No need to switch to mutable properties on the `Transaction` entity.
- Prevents normalizers from manipulating the `Transaction` entity directly, which could lead to conflicts.
Instead, all normalized data is orchestrated at one place.
- Normalizers could run in parallel; Makes sure the order is irrelevant, and they do not depend on each other.

**Disadvantages**

- Using the reflection API is considered bad practice in general, because ...
  - we lose compile-time safety
  - code is less readable and therefore maintainable
  - it might introduce a performance overhead

It was decided that the advantages outweigh the disadvantages here.
To compensate the less-safe nature of the reflection API, tests were added.

### Testing

Test data for manual testing: `/resources/test_data`

- **JUnit** for running tests
- **Mockk** for mocking
- **Wiremock** for mocking external requests

## Potential Improvements

- Logging indicates that Frankfurter API caching is not working as expected.
Ideally, there should be a maximum of one API call for each upload.
- `PurposeNormalizer` should apply casing.

## Learnings

In retrospective, choosing a reactive approach via Spring WebFlux turned out to be more complex than initially expected.
The idea was to improve request throughput by making request processing non-blocking.
This lead to a few issues that probably hadn't existed with the classic approach, though.

- Deserializing XML request bodies isn't supported out-of-the-box.
See https://github.com/spring-projects/spring-framework/issues/20256
- Adding data to a per-request logging context via SLF4J's MDC didn't work correctly.
See branch `logging-context`.
