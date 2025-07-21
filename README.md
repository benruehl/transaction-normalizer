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

### Testing

Test data for manual testing: `/resources/test_data`

- **JUnit** for running tests
- **Mockk** for mocking
- **Wiremock** for mocking external requests
