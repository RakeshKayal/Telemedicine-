package com.telemedicine.telemedicine.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Entity
@Data
public class prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String prescriptionId;

    @ManyToOne
    @JoinColumn(name = "conId", nullable = false)
    @JsonBackReference("consultation-prescriptions")
    private consultation consultation;

    private LocalDateTime issuedAt;
    private String imageUrl;
}
