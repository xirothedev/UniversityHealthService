# ADR-18: Modern Swing shell with FlatLaf

## 1. Context
The original UI is seven separate windows opened from a menu bar. For the D4 presentation the team wants one modern-looking application that reads as a university health management system. ADR-02 chose Swing and recorded "ugly UI, fine since the brief does not grade looks". The decision to use Swing stays; that consequence is now reversed.

## 2. Decision
Rewrite the `com.uhs.ui` layer only. Use one `JFrame` with a left navigation rail and a card-style content area. Apply FlatLaf 3.8 with a dark light-and-shadow theme and a teal accent. Translate the Astryx `Side Nav`, `Executive Summary Dashboard`, `Searchable Table` and `Order Detail` templates by hand into Swing components. Keep the DAO, model, util, SQL scripts and database design unchanged.

## 3. Dropped
Dropped an embedded browser engine (JCEF, JavaFX WebView): large native dependencies and a demo that can fail on the grader machine. Dropped a Java web app: it changes the D6 deliverable type and conflicts with the pinned Swing stack. Dropped built-in Nimbus: it cannot reach the requested visual quality. Dropped a login screen: the brief has no authentication requirement.

## 4. Consequences
Gain: one coherent application, modern look, every feature one click away, no extra setup on the grader machine. Loss: one new Maven dependency; the UI is close to the Astryx templates but not pixel-identical; the old screen classes are removed after parity. Owner: UI layer only.

## 5. Related
Reopens the consequence in ADR-02. Keeps ADR-07 (external DB settings, PreparedStatement, try-with-resources). Touches forms F0-F5 and queries Q-T1-Q-T5 from README.md. Does not touch `sql/` or the ERD.
