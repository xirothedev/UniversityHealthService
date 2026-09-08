# ADR-07: Externalized connection + PreparedStatement

## 1. Context
The grader points the app at a different DB with no rebuild. An apostrophe `'` breaks concatenated SQL.

## 2. Decision
DB settings live in `db.properties` (overridable via ENV/`.env`, see ADR-11) outside the code. Every statement with user input uses `PreparedStatement ?` + try-with-resources.

## 3. Dropped
Dropped string concatenation into SQL (fast but injection-prone, penalized). Dropped hard-coded user/pass in `.java`.

## 4. Consequences
Gain: safe, easy to grade. Loss: slightly more verbose code.

## 5. Related
All DAOs. Forms F1-F5. Files `db.properties.example`, `.env.example`.
