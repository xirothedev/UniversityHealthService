# ER checklist (for D2, due W5 6/10-12/10) — R2 draws, whole team checks

- [ ] >=8 entities, >=8 rels, clear singular/plural names.
- [ ] Weak PrescriptionLine + identifying rel + partial key line_no.
- [ ] M-M PatientAllergy with attrs severity/onset_date.
- [ ] ISA Patient <- Student/Staff, marked disjoint/total.
- [ ] Self-rel Patient-contact-Patient via ContactTrace, 2 roles marked.
- [ ] Cardinality + participation on every rel.
- [ ] No list-like string columns, no current price overwriting historical price.
- [ ] Each table notes which mapping rule created it.
- [ ] Each business rule appears in at least 1 place on the ER.
