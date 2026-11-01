# UniversityHealthService (IT079IU — Topic 14)

University health station: visit records + early outbreak detection.

## Pinned stack

JDK 25 LTS + MySQL 8.4 LTS + Connector/J 8.4.x + Maven 3.9.x + Swing + draw.io.

## Requirements

| Item | Version | Notes |
|------|---------|-------|
| JDK | 25 LTS (`java -version` must say 25) | `pom.xml` compiles with `maven.compiler.source/target = 25` |
| Maven | 3.9.x | downloads the dependencies below on first build (needs internet once) |
| JDBC driver | MySQL Connector/J 8.4.0 (`com.mysql:mysql-connector-j`) | already declared in `pom.xml`, no manual download |
| Database | MySQL 8.4 LTS (local) or the temporary TiDB Cloud dev DB | the app only needs the `uhs_db` schema to exist |

## Connection settings (no recompiling needed)

The app never hard-codes the database. It reads, in this order (first one wins):
real environment variables > `.env` file > `db.properties` > built-in defaults.
The files are read from the **folder you start the app in** (the project root).

| `.env` key | `db.properties` key | Default | Meaning |
|------------|---------------------|---------|---------|
| `DB_HOST` | `db.host` | `localhost` | server name or IP |
| `DB_PORT` | `db.port` | `3306` | TCP port (TiDB Cloud uses `4000`) |
| `DB_DATABASE` | `db.name` | `uhs_db` | schema name |
| `DB_USERNAME` | `db.user` | none (required) | login user |
| `DB_PASSWORD` | `db.password` | none | login password |
| `DB_PARAMS` | `db.params` | `useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true` | extra JDBC URL options |

To point the app at another server, edit `.env` and start it again. Never commit `.env`.

## Build and run

1. **Create the database.** Run the three scripts in order (the first one drops and recreates `uhs_db`):

   ```bash
   mysql -u root -p < sql/01_schema.sql
   mysql -u root -p < sql/02_data.sql
   mysql -u root -p uhs_db < sql/03_queries.sql   # optional: view, index, transaction demo
   ```

2. **Create a login** (skip if you use root). Example for local MySQL 8.4:

   ```sql
   CREATE USER 'uhs_app'@'localhost' IDENTIFIED BY 'change_me';
   GRANT ALL ON uhs_db.* TO 'uhs_app'@'localhost';
   ```

3. **Configure the connection.** Copy `.env.example` to `.env` and edit host, user and password.
   For local MySQL use `DB_PARAMS=useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`.
   For TiDB Cloud use `DB_PARAMS=sslMode=VERIFY_IDENTITY&serverTimezone=UTC` and port `4000`.

4. **Build** (compiles, runs the unit tests, the formatter check and the style check):

   ```bash
   mvn -B verify
   ```

   Quick build without tests: `mvn -q -DskipTests package`.

5. **Run** from the project root so `.env` is found:

   ```bash
   mvn -q exec:java
   ```

   The console prints the database target (never the password) and the main window opens.
   If you see `Missing DB config` the `.env` file or `DB_USERNAME` is missing.

## Screens

| Where in the menu | Screen | What it does |
|-------------------|--------|--------------|
| Forms > F4 | Edit consultations | Grid of all consultations; New / Save / Delete / Refresh. Insert and update validate patient, doctor, date (`yyyy-MM-dd HH:mm`) and diagnosis first. Delete asks for confirmation and warns when a prescription or notifiable case still depends on the row. |
| Forms > F5 | Report: Top-5 diagnoses | Aggregate report (`GROUP BY` / `HAVING` / `LIMIT 5`) with optional From / To dates, a minimum count, a table and a bar chart. |
| Queries > Q-T1 | Consultation history of one patient | Multi-table join, patient chosen from a list. |
| Queries > Q-T2 | Free consultation slots | Outer join; slots of the weekly template on a chosen date that no scheduled/completed appointment covers. |
| Queries > Q-T3 | Top-5 diagnoses | Opens form F5. |
| Queries > Q-T4 | Patients who received every required dose | `NOT EXISTS` for-all pattern. |
| Queries > Q-T5 | Same disease in one building within 14 days | Self-join on `NotifiableCase`. |

Every query screen shows the SQL behind it at the bottom, so it can be copied into the report.

Code rules followed everywhere: `PreparedStatement` for every user value (an apostrophe in a text
field is safe), `try-with-resources` for connections, statements and result sets, validation before
the database is touched, and database errors turned into plain-language messages
(`com.uhs.util.DbErrors`). Source folders: `com.uhs.ui` (Swing), `com.uhs.dao` (SQL), `com.uhs.model`
(records), `com.uhs.util` (connection, dates, error text).

## Troubleshooting

| Message | Cause and fix |
|---------|---------------|
| `Missing DB config` | No `.env` / `db.properties` in the folder you started from, or `DB_USERNAME` is empty. |
| "Cannot reach the database..." | Server is not running, or wrong `DB_HOST` / `DB_PORT`. For TiDB Cloud also check `DB_PARAMS`. |
| "The database refused the user name or password" | Wrong `DB_USERNAME` / `DB_PASSWORD`. |
| "The database name was not found" / "A table is missing" | Run `sql/01_schema.sql` and `sql/02_data.sql` first. |
| `Public Key Retrieval is not allowed` | Add `allowPublicKeyRetrieval=true` to `DB_PARAMS` (local MySQL 8.x with `caching_sha2_password`). |
| Q-T2 shows no rows | The chosen date falls on a weekday with no `ConsultSlot` rows (the sample data has slots Monday to Friday only). |

Grading details: `CONTEXT.md`, `docs/adr/`, `docs/D1-outline.md`.

## Submission format

Proposal = D1, Midterm = D2, Final = D3+D5+D6 (+ D4 slides/demo). Mapping + cover rule: `docs/deliverables-detail.md` (ADR-15). Pre-submit tick list: `docs/submission-checklist.md`. Templates: `docs/templates/cover.md`, `docs/templates/task-allocation.md`.

## Team (12 members, fill in)

| # | Name | Student ID | Phone | Claimed role |
|---|------|------------|-------|--------------|
| 1 | Lê Thành Trung |ITCSIU25055 |0359186887 |Query Developer |
| 2 | Nguyễn Trần Quốc Vinh |MAMAIU25041 |0837991399 |Java Developer |
| 3 | Phan Nguyên Vũ |ITITIU25047 |0945719666 |Java Developer |
| 4 | Nguyễn Hoàng Thiên Tứ |ITDSIU25050 |0374749542 |Query Developer |
| 5 | Nguyễn Quốc Duy Lộc |ITITIU25018 |0906648951 |Data Modeller |
| 6 | Trần Pham Quốc Nam |ITCSIU25037 |0375314749 |Database Developer |
| 7 | Lê Đức Phát |ITCSIU25042 |0767393239 |Database Developer |
| 8 | Nguyễn Minh Khôi |SESEIU24013 |0902305920 |Tester & Writer/Integrator |
| 9 | Lê Thảo My |SESEIU23018 |0835931468 |Normalization lead |
| 10 | Nguyễn Trần Anh Tuấn |SESEIU24030 |0972235718 |Normalization lead |
| 11 | Nguyễn Trọng Đức |ITITIU25003 |0775327007 |Data Modeller |
| 12 | Nguyễn Hiền Minh |ITITWE25011 |0919150220 |Group leader |
