# Relational Algebra — Topic 14 University Health Service

Four of the twelve submitted queries expressed in relational algebra. The
canonical SQL is in `sql/03_queries.sql` (Q01, Q03, Q04 and Q05); this file does
not duplicate it. Each section names the query, states the English question and
lists the SQL clauses that the algebra expresses.

**Notation:** σ selection, π projection, ⋈ join, ⟕ left outer join, ρ rename,
γ grouping and aggregation, ÷ division, × Cartesian product, τ top-N.

A note on scope: pure relational algebra has no ORDER BY, LIMIT, recursive CTE,
window function or date-difference operator. Where a submitted query uses one
of these, the algebra below uses a clearly named extension (τ, `rank`, `days`)
and the note says so. The core relational operation is still exact.

---

## Q01 — Consultation history of one patient

**Question:** What is the consultation history of a selected patient, including
the attending clinician and any prescribed medicines?

**Submitted SQL clauses:** SELECT 17 columns from Consultation, Patient,
MedicalStaff, Prescription, PrescriptionLine and Medicine; `INNER JOIN` to the
first three tables, `LEFT JOIN` to the last three; `WHERE c.patient_id =
@patient_id`; `ORDER BY` three columns. Full text: `sql/03_queries.sql`, Q01.

**Relational algebra:**

```
π_{c.consultation_id, c.consultation_date, p.patient_id, p.full_name,
   ms.full_name, c.symptoms, c.diagnosis, c.notes,
   pr.prescription_id, pr.issue_date, pl.line_no, m.medicine_name,
   pl.dosage, pl.frequency, pl.duration_days, pl.quantity, pl.unit_price} (
  σ_{c.patient_id = @patient_id} (
    (((ρ_c(Consultation) ⋈_{c.patient_id = p.patient_id} ρ_p(Patient))
       ⋈_{c.med_staff_id = ms.med_staff_id} ρ_ms(MedicalStaff))
       ⟕_{c.consultation_id = pr.consultation_id} ρ_pr(Prescription))
       ⟕_{pr.prescription_id = pl.prescription_id} ρ_pl(PrescriptionLine))
       ⟕_{pl.medicine_id = m.medicine_id} ρ_m(Medicine)
  )
)
```

**Explanation:** The three outer joins keep visits that have no prescription,
no line or no medicine row. Renames give the two `full_name` columns distinct
names. `ORDER BY` is a presentation concern and is omitted.

---

## Q03 — Five most frequent diagnoses

**Question:** What are the five most frequent diagnoses in the selected semester?

**Submitted SQL clauses:** SELECT diagnosis and `COUNT(*) AS consultation_count`
from Consultation; WHERE filters a half-open semester range and removes blank
diagnoses; `GROUP BY c.diagnosis`; `HAVING COUNT(*) >= @min_diagnosis_count`;
`ORDER BY consultation_count DESC, c.diagnosis ASC`; `LIMIT 5`. Full text:
`sql/03_queries.sql`, Q03.

**Relational algebra:**

```
τ_{5; consultation_count DESC, diagnosis ASC} (
  π_{diagnosis, consultation_count} (
    σ_{consultation_count >= @min_diagnosis_count} (
      γ_{diagnosis; COUNT(*) → consultation_count} (
        σ_{consultation_date >= @semester_start
           ∧ consultation_date < @semester_end
           ∧ diagnosis IS NOT NULL
           ∧ TRIM(diagnosis) <> ''} (Consultation)
      )
    )
  )
)
```

**Explanation:** γ groups and counts. σ on the aggregate result is the HAVING
clause. τ is the top-N extension that stands for ORDER BY and LIMIT; it is not
part of core relational algebra.

---

## Q04 — Patients who completed a vaccination course

**Question:** Which patients have completed the selected vaccination course
under the group's distinct-dose-count rule?

**Submitted SQL clauses:** a recursive CTE builds positions `1..required_doses`;
a `DISTINCT` subquery keeps distinct dose numbers; `ROW_NUMBER()` ranks those
numbers per patient; the outer query is `CROSS JOIN` Patient with the selected
course and applies a double `NOT EXISTS`. Full text: `sql/03_queries.sql`, Q04.

**Relational algebra:**

```
Required(course_id, dose_position) :=
  π_{course_id, dose_position} (
    σ_{dose_position <= required_doses} (
      σ_{course_id = @course_id}(VaccinationCourse) × ρ_{dose_position}(Pos_{1..4})
    )
  )

ReceivedPos(patient_id, course_id, dose_position) :=
  rank_{patient_id, course_id} (
    π_{patient_id, course_id, dose_number} (σ_{course_id = @course_id}(VaccinationRecord))
  )

Complete(patient_id) :=
  π_{patient_id, course_id, dose_position}(ReceivedPos) ÷ Required

Result :=
  π_{p.patient_id, p.full_name, vc.course_id, vc.course_name, vc.required_doses} (
    (Patient × σ_{course_id = @course_id}(VaccinationCourse))
    ⋈_{patient_id} Complete
  )
```

**Explanation:** The double NOT EXISTS is a division. `Required` is the set of
positions in the selected course; `ReceivedPos` is the set of positions each
patient received. `rank` stands for ROW_NUMBER, an extension; `Pos_{1..4}` is a
constant relation large enough for the seed courses. Under the agreed rule the
labels 1, 2 and 99 satisfy a three-dose course.

---

## Q05 — Reports of one disease in one building within fourteen days

**Question:** Which pairs of reports of a selected disease occurred in the same
building no more than fourteen days apart?

**Submitted SQL clauses:** self-join NotifiableCase on `nc1.case_id < nc2.case_id`,
equal building and equal disease; join Building and two copies of Consultation;
WHERE adds `@disease_name` and `ABS(DATEDIFF(...)) <= 14`. Full text:
`sql/03_queries.sql`, Q05.

**Relational algebra:**

```
π_{b.building_id, b.building_name, nc1.disease_name,
   nc1.case_id, nc2.case_id, c1.patient_id, c2.patient_id,
   nc1.date_reported, nc2.date_reported,
   |days(nc1.date_reported) − days(nc2.date_reported)| AS days_apart} (
  σ_{nc1.disease_name = @disease_name
     ∧ |days(nc1.date_reported) − days(nc2.date_reported)| <= 14} (
    (((ρ_nc1(NotifiableCase)
       ⋈_{nc1.case_id < nc2.case_id
          ∧ nc1.building_id = nc2.building_id
          ∧ nc1.disease_name = nc2.disease_name} ρ_nc2(NotifiableCase))
       ⋈_{nc1.building_id = b.building_id} ρ_b(Building))
       ⋈_{nc1.consultation_id = c1.consultation_id} ρ_c1(Consultation))
       ⋈_{nc2.consultation_id = c2.consultation_id} ρ_c2(Consultation)
  )
)
```

**Explanation:** Renames `nc1` and `nc2` turn one relation into two roles, so a
self-join is written like any other join. `days` is a date-to-day-number
extension standing for DATEDIFF. The strict `<` on case ids makes each unordered
pair appear once.

---

## Summary

| Query | SQL construct | Core algebra operator | Extension used |
|---|---|---|---|
| Q01 | Multi-table join with LEFT JOIN | ⋈ and ⟕ | none |
| Q03 | GROUP BY, HAVING, LIMIT | γ and σ | τ (top-N) |
| Q04 | Double NOT EXISTS for-all | ÷ | rank, constant Pos |
| Q05 | Self-join, date predicate | ⋈ and σ | days (date difference) |

The four algebra expressions cover the required query types in assignment §2.2:
a multi-table join, aggregation with HAVING, a for-all query and a self-join.

The RA1–RA4 labels in this file are local to this file: RA1=Q01, RA2=Q03,
RA3=Q04 and RA4=Q05. A second T17 deliverable,
`docs/query-development-report.docx`, uses its own RA1–RA4 labels for Q06,
Q07, Q09 and Q10. Both sets are retained and cited separately in
`docs/D3-final-report.md` and `sql/03_queries.sql`.
