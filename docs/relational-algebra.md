# Relational Algebra — Topic 14 University Health Service (D2 draft)

Four of the twelve SQL queries from `sql/03_queries.sql` expressed in relational
algebra. Notation: σ (selection), π (projection), ⋈ (natural join), ρ (rename),
γ (grouping/aggregation), ÷ (division), × (Cartesian product), ∩, ∪, −.

---

## Q-T1: Consultation history of one patient (multi-join)

**SQL:**
```sql
SELECT c.consultation_id, c.consultation_date, c.diagnosis, m.full_name
FROM Consultation c
JOIN MedicalStaff m ON c.med_staff_id = m.med_staff_id
WHERE c.patient_id = 1
ORDER BY c.consultation_date;
```

**Relational algebra:**
```
π consultation_id, consultation_date, diagnosis, full_name (
  σ patient_id = 1 (
    Consultation ⋈ Consultation.med_staff_id = MedicalStaff.med_staff_id MedicalStaff
  )
)
```

**Explanation:** Join Consultation with MedicalStaff on med_staff_id, select
rows where patient_id = 1, project the desired columns. The ORDER BY is a
presentation concern, not part of the relational algebra.

---

## Q-T3: Top-5 diagnoses (GROUP BY/HAVING)

**SQL:**
```sql
SELECT diagnosis, COUNT(*) AS freq
FROM Consultation
GROUP BY diagnosis
HAVING COUNT(*) >= 1
ORDER BY freq DESC
LIMIT 5;
```

**Relational algebra:**
```
π diagnosis, freq (
  γ diagnosis; COUNT(*) → freq (
    Consultation
  )
)
```

**Explanation:** Group Consultation by diagnosis, count rows per group. The
HAVING clause filters groups with count >= 1 (all groups pass). The ORDER BY
+ LIMIT is a top-N operation, which is not standard relational algebra — it is
a presentation-layer concern. The core aggregation is the γ operator.

---

## Q-T4: Patients who received every dose (NOT EXISTS for-all)

**SQL:**
```sql
SELECT DISTINCT vr.patient_id, p.full_name
FROM VaccinationRecord vr
JOIN Patient p ON vr.patient_id = p.patient_id
WHERE NOT EXISTS (
  SELECT 1
  FROM VaccinationCourse vc
  WHERE vc.course_id = vr.course_id
    AND NOT EXISTS (
      SELECT 1
      FROM VaccinationRecord vr2
      WHERE vr2.patient_id = vr.patient_id
        AND vr2.course_id = vr.course_id
        AND vr2.dose_number <= vc.required_doses
    )
);
```

**Relational algebra:**
```
π patient_id, full_name (
  Patient ⋈ (
    π patient_id, course_id, dose_number (VaccinationRecord)
    ÷
    π course_id, dose_number (
      σ dose_number <= required_doses (
        VaccinationCourse × ρ dose_number ({1, 2, 3, 4})
      )
    )
  )
)
```

**Explanation:** This is a division. The dividend is the set of
(patient_id, course_id, dose_number) tuples from VaccinationRecord. The divisor
is the set of (course_id, dose_number) tuples representing all required doses
for each course — computed by expanding VaccinationCourse.required_doses into
individual dose numbers (1, 2, 3, 4) via a Cartesian product with a constant
relation. The division yields patients who have all required doses for every
course they enrolled in. The double NOT EXISTS in SQL is the standard way to
express division in SQL; the relational algebra ÷ operator captures it directly.

---

## Q-T5: Cases of one disease in same building within 14 days (self-join)

**SQL:**
```sql
SELECT c1.case_id, c1.disease_name, c1.date_reported, b.building_name
FROM NotifiableCase c1
JOIN NotifiableCase c2 ON c1.disease_name = c2.disease_name
  AND c1.building_id = c2.building_id
  AND c1.case_id <> c2.case_id
  AND ABS(DATEDIFF(c1.date_reported, c2.date_reported)) <= 14
JOIN Building b ON c1.building_id = b.building_id;
```

**Relational algebra:**
```
π case_id, disease_name, date_reported, building_name (
  σ c1.case_id ≠ c2.case_id ∧ |c1.date_reported − c2.date_reported| ≤ 14 (
    ρ c1 (NotifiableCase)
    ⋈ c1.disease_name = c2.disease_name ∧ c1.building_id = c2.building_id (
      ρ c2 (NotifiableCase)
    )
  )
  ⋈ c1.building_id = Building.building_id Building
)
```

**Explanation:** Self-join NotifiableCase with itself, matching on disease_name
and building_id, excluding self-matches (case_id ≠ case_id), and keeping only
pairs within 14 days. Then join with Building to get the building name. The
date difference is a computed predicate — in a pure relational algebra over
dates, this would be expressed as a selection on a date-difference function.

---

## Summary

| Query | SQL construct | Relational algebra operator |
|---|---|---|
| Q-T1 | Multi-table join | ⋈ (natural join) |
| Q-T3 | GROUP BY/HAVING | γ (grouping/aggregation) |
| Q-T4 | NOT EXISTS for-all | ÷ (division) |
| Q-T5 | Self-join | ⋈ (self-join) + σ (selection) |

All four queries are expressible in standard relational algebra. The SQL
ORDER BY and LIMIT clauses are presentation concerns and do not affect the
relational algebra.
