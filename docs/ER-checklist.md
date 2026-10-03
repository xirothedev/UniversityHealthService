# ER checklist (for D2, due W5 6/10-12/10) — R2 draws, whole team checks

- [x] >=8 entities, >=8 rels, clear singular/plural names.
- [x] Weak PrescriptionLine + identifying rel + partial key line_no.
- [x] M-M PatientAllergy with attrs severity/onset_date.
- [x] ISA Patient <- Student/Staff, marked disjoint/total.
- [x] Self-rel Patient-contact-Patient via ContactTrace, 2 roles marked.
- [ ] Cardinality + participation on every rel.
- [x] No list-like string columns, no current price overwriting historical price.
- [x] Each table notes which mapping rule created it.
- [ ] Each business rule appears in at least 1 place on the ER.

Notes (ADR-17; honest gaps for the follow-up PR before 12/10):
- Cardinality is marked on every rel, but three minima have no BR behind them and
  stay under review: `attending` (every MedicalStaff >= 1 Consultation),
  `exposure site` (every Building >= 1 NotifiableCase), and BR-04 "one or more"
  ConsultSlots vs the `0..n` edge. The box stays unticked until they are resolved.
- Per-element BR tags are not on the diagram yet (Q8 deferred); every column has a
  BR in `docs/business-rules.md` (incl. BR-19/BR-20), but the diagram does not show
  the mapping. The box stays unticked until the grey BR strips land.
