# ADR-09: Minimal GitHub Actions CI

## 1. Context
A 12-person team pushes PRs to `main` with no automated checks. Breaks in `pom.xml` or `src/` must surface early, before D1 (W3 22/9-28/9). `sql/*.sql` are still placeholders, so CI cannot demand a DB yet.

## 2. Decision
GitHub Actions: `.github/workflows/ci.yml`, runs on `push` + `pull_request` to `main`. Job `build` on `ubuntu-latest`: `actions/checkout@v7` + `actions/setup-java@v6` (Temurin 21, `cache: maven`) + `mvn -B package`. No DB, no branch protection in V1.

## 3. Dropped
Dropped GitLab CI/Jenkins (extra infra, repo already on GitHub). Dropped a `mysql:8.4` service in V1 (no schema yet, CI would be red from day 1). Dropped Maven wrapper/pinned 3.9.x (runner already ships 3.9.x, a wrapper only confuses newcomers).

## 4. Consequences
Gain: broken-compile PRs show up immediately, few minutes spent. Loss: no SQL errors caught, no missing-`db.properties` check; compensated with manual review until R4/R5 deliver the schema (see ADR-10).

## 5. Related
`pom.xml`, `src/main/java/com/uhs/`. SSOT `docs/assignment.md` §§2.3-2.4 does not require CI, so no conflict. Once the schema runs twice with no manual repair, extend CI with a MySQL job.
