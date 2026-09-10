# ADR-02: JDK 25 + Maven + Swing

## 1. Context
The Java app must run on the grader machine with no rebuild. A 12-person newcomer team needs the fewest setup failures.

## 2. Decision
JDK 25 LTS + Maven 3.9.x + Swing (ships with the JDK) + Connector/J 8.4.x.

## 3. Dropped
Dropped JavaFX (prettier but needs extra installs, demo can break). Dropped new Gradle, dropped old LTS JDK 21 (superseded), dropped non-LTS JDKs.

## 4. Consequences
Gain: everyone can run it, few dependencies. Loss: ugly UI, fine since the brief does not grade looks.

## 5. Related
6 forms F0-F5. Files `db.properties` + `pom.xml`.
