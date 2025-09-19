package com.telemedicine.telemedicine.Repository;

import com.telemedicine.telemedicine.Model.prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PrescriptionRepo extends JpaRepository<prescription,String > {
}
