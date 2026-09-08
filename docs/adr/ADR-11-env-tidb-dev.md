# ADR-11: ENV + .env for the DB, TiDB Cloud as dev-only

## 1. Context
The grader points the app at a different DB with no rebuild (ADR-07). Docker/CI/TiDB Cloud pass credentials via ENV (DB_HOST...), while local devs prefer editing a file. TiDB Cloud mandates TLS, so the old `useSSL=false` cannot connect.

## 2. Decision
Lookup order: real ENV > `.env` file > `db.properties` > defaults. ENV keys: DB_HOST, DB_PORT, DB_DATABASE, DB_USERNAME, DB_PASSWORD, DB_PARAMS. Hand-rolled `.env` parser, no new dependency. TiDB Cloud is a dev-only DB for connector testing; the submission stays MySQL 8.4 LTS (ADR-01 unchanged). TiDB TLS: `sslMode=VERIFY_IDENTITY`.

## 3. Dropped
Dropped `db.properties`-only reading (poor fit for Docker/CI). Dropped dotenv-java (overkill for 6 keys). Dropped `useSSL=false` against TiDB (server refuses). Never commit the real `.env` (leaks the password); only `.env.example` is committed.

## 4. Consequences
Gain: local + grader + CI share one code path; no recompile. Loss: `buildUrl` no longer leaves a dangling `?` on empty params (old test fixed for correct JDBC). Owners: R6 (Java) owns `DBConnection`, everyone copies `.env.example`.

## 5. Related
`src/main/java/com/uhs/util/DBConnection.java`, `src/main/java/com/uhs/App.java`, `.env.example`, `db.properties.example`, ADR-01, ADR-07. SSOT `docs/assignment.md` §2.4 (settings outside the code) still satisfied.
