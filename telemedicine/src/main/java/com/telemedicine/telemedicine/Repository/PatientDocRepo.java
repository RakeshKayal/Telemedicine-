package com.telemedicine.telemedicine.Repository;

import com.telemedicine.telemedicine.Model.patient;
import com.telemedicine.telemedicine.Model.patientDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientDocRepo extends JpaRepository<patientDocument,String > {
    patientDocument findByPatient(patient patient);
}
