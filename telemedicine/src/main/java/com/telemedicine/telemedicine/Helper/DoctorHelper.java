package com.telemedicine.telemedicine.Helper;

import com.telemedicine.telemedicine.Model.patient;
import com.telemedicine.telemedicine.Model.patientDocument;
import lombok.Data;

import java.util.List;
@Data
public class DoctorHelper {
    private String doctorId;
    private List<ConsultationInfo> consultations; // list of consultations

    @Data
    public static class ConsultationInfo {
        private String consultationId;
        private patientDocument patientDocument;
    }
}

