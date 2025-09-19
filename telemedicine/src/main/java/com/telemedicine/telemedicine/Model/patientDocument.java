package com.telemedicine.telemedicine.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class patientDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String pdId;


  private  int systolicBP;
  private int diastolicBP;
  private  int weight;
  private  int height;
  private  int temperature;
  private  int heartRate;
  private int bloodSugar;
    private String patientSymptomp;
    @OneToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "patientId")
    @JsonBackReference("patient-document")
    private patient patient;
}
