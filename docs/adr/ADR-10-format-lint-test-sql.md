# ADR-10: Format + lint + test + SQL checks in CI

## 1. Context
PRs need automated checks beyond compile: consistent format, basic lint, runnable tests, valid SQL syntax. A 12-person newcomer team must fit every check into a single local command.

## 2. Decision
Format: Spotless 3.10.2 + google-java-format. Fix locally with `mvn spotless:apply`, CI enforces with `spotless:check` (bound to `verify`). Lint: maven-checkstyle-plugin 3.6.0 + `google_checks.xml` (checkstyle lib 14.1.0), runs at `verify`, green only with 0 violations. Test: JUnit 5.14.4 (`scope: test`); `DBConnection.buildUrl(Properties)` is kept testable (no real DB needed), `App.main` shares it to avoid duplication; 2 tests in `DBConnectionTest`. SQL: sqlfluff (`dialect = mysql` in `.sqlfluff`), CI runs `sqlfluff lint sql/`. CI splits into 2 jobs: `java` (`mvn -B verify`) and `sql` (pip + lint).

## 3. Dropped
Dropped PMD/ErrorProne (strong but heavy config, hard for newcomers). Dropped JUnit 6 (new, little Vietnamese documentation, default Surefire support uncertain). Dropped a live-DB MySQL service at this step (no R4 schema yet, a container would only fake green; reserved for after the schema runs twice with no manual fix). Dropped bare `sqlfluff parse` syntax-only checks (loses style signals like LT05).

## 4. Consequences
Gain: PRs breaking format/lint/test/SQL go red at once; locally everyone only remembers `mvn -B verify` + `sqlfluff lint sql/`. Loss: google 2-space style feels odd to 4-space users; sqlfluff is strict about long lines — fix by wrapping, never by disabling rules. Owners: whole team, R6 (Java) owns format/tests, R4/R5 own green SQL.

## 5. Related
`pom.xml`, `src/main/java/com/uhs/App.java`, `src/main/java/com/uhs/util/DBConnection.java`, `src/test/java/com/uhs/util/DBConnectionTest.java`, `.sqlfluff`, `sql/*.sql`, `.github/workflows/ci.yml`. SSOT `docs/assignment.md` §§2.3-2.4 does not require CI, so no conflict.
