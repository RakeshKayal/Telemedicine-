package com.telemedicine.telemedicine.Service;

import com.telemedicine.telemedicine.Model.prescription;
import com.telemedicine.telemedicine.Repository.PrescriptionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrescriptionService {

    @Autowired
    PrescriptionRepo prescriptionRepo;
    public prescription save(prescription p) {
      return   prescriptionRepo.save(p);

    }
}
