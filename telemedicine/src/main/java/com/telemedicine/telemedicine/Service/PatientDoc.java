package com.telemedicine.telemedicine.Service;

import com.telemedicine.telemedicine.Model.patient;
import com.telemedicine.telemedicine.Model.patientDocument;
import com.telemedicine.telemedicine.Repository.PatientDocRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientDoc {
    @Autowired
    PatientDocRepo patientDocRepo;

    public patientDocument save(patientDocument p){
        return  patientDocRepo.save(p);
    }

    public patientDocument findByPatient(patient patient) {
        return  patientDocRepo.findByPatient(patient);
    }
}
