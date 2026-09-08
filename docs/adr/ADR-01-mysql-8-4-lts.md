# ADR-01: Use MySQL 8.4 LTS

## 1. Context
The brief allows MySQL or SQL Server, and labs use MySQL. The DB must actually enforce primary keys, foreign keys, UNIQUE, and CHECK.

## 2. Decision
Use MySQL 8.4 LTS. Record the version in the report + README.

## 3. Dropped
Dropped Postgres (not in the brief, risks lost marks). Dropped MySQL 9.x (bleeding edge, grader machines do not have it).

## 4. Consequences
Gain: matches the labs, easy to grade. Loss: weaker CHECK than Postgres, compensated with manual tests.

## 5. Related
All tables. Queries Q-T2, Q-T3 use indexes.
