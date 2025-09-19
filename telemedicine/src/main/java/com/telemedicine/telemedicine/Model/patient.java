package com.telemedicine.telemedicine.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
@Entity
@Data
public class patient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String patientId;

    private String name;

private  int age;
private  String  gender;
private String mobileNo;
private  String  address;
    private int addharId;

    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL)
    private patientDocument patientDocument;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "consultation-patient")
    private List<consultation> consultations = new ArrayList<>();
}
