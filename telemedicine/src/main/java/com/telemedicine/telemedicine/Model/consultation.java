package com.telemedicine.telemedicine.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String conId;

    @ManyToOne
    @JoinColumn(name = "docId", nullable = false)
    @JsonBackReference(value = "doctor-consultations")
    private doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patientId", nullable = false)
    @JsonBackReference(value = "consultation-patient")
    private patient patient;

    private String appointmentStatus;

    private LocalDateTime time;

    private LocalDateTime scheduledDate; // Scheduled consultation date

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "consultation-prescriptions")
    private List<prescription> prescriptions = new ArrayList<>();
}
