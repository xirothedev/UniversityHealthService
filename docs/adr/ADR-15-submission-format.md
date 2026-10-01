# ADR-15: Submission-format mapping (guideline PDF ↔ D1–D6)

## 1. Context
The course has two requirement sources: `docs/assignment.md` (SSOT, CI-blocked, D1–D6 with
dates/lengths/sections) and `Project Submittion Guideline.pdf` (3 stages: Proposal W2,
Midterm W5, Final W11 with Cover/Abstract/Task-table/Tools, TOC/ER/Schema,
Report+slides+code+DB). Newcomers need one mapping so a submission passes both checklists.

## 2. Decision
`docs/assignment.md` stays SSOT for technical content and is never edited. The guideline
governs packaging/format only. Mapping: Proposal = D1, Midterm = D2, Final = D3+D5+D6
(+ D4 slides/demo). Superset cover (course + topic 14 + instructor + name/ID/phone/topic
table) on all three stages. Merged section orders and the final package layout live in
`docs/deliverables-detail.md`; per-stage tick boxes in `docs/submission-checklist.md`;
copy-paste covers/tables in `docs/templates/`. A dated instructor announcement
overrides the earlier planning dates: D1 is due 27/9/2026 at 22:30 and is submitted as
one PDF through Blackboard; D2 is due 12/10/2026 (W5); D3 is due 16/11/2026 (W10).

## 3. Dropped
Dropped editing `docs/assignment.md` to match the guideline (blocked by CI, destroys SSOT).
Dropped guideline-order-only reports (would fail the assignment's D1 content and D3
12-section-order checks). Dropped adding submission terms (Proposal, Midterm, Task
Allocation Table) to `CONTEXT.md` — that file is the UHS domain glossary only, not a spec
scratch pad.

## 4. Consequences
Gain: one crosswalk both graders accept; no missing-cover or wrong-order rejections.
Loss: writers maintain superset sections (slightly longer D1/D2). Owners: Tester & Writer
owns the checklist/templates; every role owns its D3 section.

## 5. Related
`docs/deliverables-detail.md`, `docs/D1-outline.md`, `docs/submission-checklist.md`,
`docs/templates/cover.md`, `docs/templates/task-allocation.md`, `CONTRIBUTING.md`,
`README.md`. SSOT `docs/assignment.md` §1.2, §2.1, §2.5–§2.7 unchanged.
