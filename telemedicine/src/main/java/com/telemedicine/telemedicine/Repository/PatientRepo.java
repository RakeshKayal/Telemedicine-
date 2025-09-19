package com.telemedicine.telemedicine.Repository;

import com.telemedicine.telemedicine.Model.patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepo extends JpaRepository<patient, String > {




    patient findByAddharId(int addharId);

    patient findByName(String pname);

    patient findByPatientId(String pi);
}
