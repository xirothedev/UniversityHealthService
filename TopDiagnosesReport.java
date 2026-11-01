package com.uhs.model;

import java.util.List;

/** Result of the Top-5 report: the ranked rows plus the denominator used for the share. */
public record TopDiagnosesReport(List<DiagnosisCount> rows, int totalConsultations) {}
