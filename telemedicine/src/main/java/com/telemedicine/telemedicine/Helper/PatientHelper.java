package com.telemedicine.telemedicine.Helper;

import com.telemedicine.telemedicine.Model.patient;
import lombok.Data;

import java.util.List;
import  com.telemedicine.telemedicine.Model.patientDocument;
import  com.telemedicine.telemedicine.Model.consultation;

@Data
public class PatientHelper {

    patient  patient;
    List<String > doctorName;
   List< consultation> consultations;


}
