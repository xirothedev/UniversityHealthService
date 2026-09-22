# ADR-12: ConsultSlot weekly template for free-slot query

## 1. Context
The brief asks for the doctors with free appointment slots this week. A text field like "Mon AM" cannot answer that; free time must be computed as planned hours minus booked appointments.

## 2. Decision
Consulting hours live in a ConsultSlot entity: one row per MedicalStaff member per weekday window (weekday + start + end + slot length). Free slots are template slots with no overlapping Appointment in scheduled or completed status.

## 3. Dropped
Dropped free-text consulting hours (simple but unqueryable, fails the required query).

## 4. Consequences
Gain: the free-slot query is answerable. Loss: one more table to fill with sample data.

## 5. Related
Tables MedicalStaff, ConsultSlot, Appointment. Query Q-T2 (free slots this week).
