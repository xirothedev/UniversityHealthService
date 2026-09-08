# ADR-06: ContactTrace self-relationship

## 1. Context
Outbreak tracing needs to know who contacted whom. Both ends are Patients.

## 2. Decision
ContactTrace table joins Patient to Patient. Mark both roles explicitly: source case / contact + contact date.

## 3. Dropped
Dropped a Doctor-manages-Doctor relationship (also a self-rel but useless for tracing).

## 4. Consequences
Gain: answers the same-building 14-day outbreak query. Loss: self-contact must be blocked via CHECK/test.

## 5. Related
Tables NotifiableCase, ContactTrace, Building. Query Q-T5.
