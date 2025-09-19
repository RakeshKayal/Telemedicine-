package com.telemedicine.telemedicine.Service;

import com.telemedicine.telemedicine.Helper.PatientHelper;
import com.telemedicine.telemedicine.Repository.PatientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.telemedicine.telemedicine.Model.patient;

import java.util.List;

@Service
public class PatientService {
    @Autowired
    PatientRepo patientRepo;


public patient save(patient patient){
    return patientRepo.save(patient);
}



    public List<patient> findAll() {
    return  patientRepo.findAll();
    }

    public patient findName(String pname) {
    return  patientRepo.findByName(pname);
    }

    public patient findByAadhaar(int addharId) {
        patient by = patientRepo.findByAddharId(addharId);
        return by;

    }

    public patient findByPatientId(String pi) {
    return patientRepo.findByPatientId(pi);
    }


    public PatientHelper getPatientHistory(String patientId) {
        patient p = patientRepo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + patientId));

        PatientHelper helper = new PatientHelper();

        // patient entity
        helper.setPatient(p);

        // doctor names from consultations
        List<String> doctorNames = p.getConsultations()
                .stream()
                .map(c -> c.getDoctor().getName())
                .toList();
        helper.setDoctorName(doctorNames);

        // consultations list
        helper.setConsultations(p.getConsultations());

        return helper;
    }

}
