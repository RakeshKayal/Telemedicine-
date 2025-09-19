package com.telemedicine.telemedicine.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String docId;

    private String name;
    private String email;
    private String mobileNo;
    private String password;
    private String specialization;
    private int yearOfExperience;

    private String googleMeetLink;
    private LocalTime availableFrom;
    private LocalTime availableTo;

    // 👇 New fields for controlling video calls
    private boolean busy = false;              // true if doctor is in a call
    private LocalDateTime busySince;           // when the call started
    private LocalDateTime expectedFreeTime;    // optional: auto release after X mins

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "doctor-consultations")
    private List<consultation> consultations = new ArrayList<>();
}
