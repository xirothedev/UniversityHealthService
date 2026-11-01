package com.uhs.model;

import java.time.LocalDateTime;

/**
 * One row of the Consultation table. The two name fields are only filled when a row is read (joined
 * from Patient and MedicalStaff); they are ignored when a row is written.
 */
public record Consultation(
    Integer id,
    int patientId,
    int medStaffId,
    Integer appointmentId,
    LocalDateTime consultationDate,
    String symptoms,
    String diagnosis,
    String notes,
    String patientName,
    String doctorName) {}
