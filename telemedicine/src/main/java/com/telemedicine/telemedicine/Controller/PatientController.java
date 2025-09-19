package com.telemedicine.telemedicine.Controller;

import com.telemedicine.telemedicine.Helper.PatientHelper;
import com.telemedicine.telemedicine.Model.patient;
import com.telemedicine.telemedicine.Model.patientDocument;
import com.telemedicine.telemedicine.Service.PatientDoc;
import com.telemedicine.telemedicine.Service.PatientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/patient")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"},
        allowCredentials = "true")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientDoc patientDoc;

    @PostMapping("/register")
    public ResponseEntity<?> registerPatient(@RequestBody patient p) {
        // Check if patient with same Aadhaar exists
        patient existingPatient = patientService.findByAadhaar(p.getAddharId());
        if (existingPatient != null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "Patient Aadhaar ID already exists"));
        }

        // Save patient
        patientService.save(p);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("status", "success", "message", "Patient registered successfully", "patientId", p.getPatientId()));
    }

@PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody patient p, HttpSession session) {
        patient patient = patientService.findByAadhaar(p.getAddharId());
        if (patient == null) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        } else {
            session.setMaxInactiveInterval(60 * 30); // 30 minutes
//            session.setAttribute("patientName", patient.getName());
            session.setAttribute("Ad", patient.getAddharId());
            return ResponseEntity.ok("Patient login successfully");
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getPatient(){
        List<patient> all = patientService.findAll();
        return  ResponseEntity.ok(all);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestBody patientDocument p, HttpSession session) {

        String patientId = p.getPatient().getPatientId();
        patient patient = patientService.findByPatientId(patientId);

        if (patient == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "Patient not found"));
        }

        // check if existing doc (implement findByPatient in repo/service)
        patientDocument existing = patientDoc.findByPatient(patient);
        if (existing != null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "Patient document already exists"));
        }

        p.setPatient(patient); // attach managed entity to avoid detached problems
        patientDoc.save(p);

        return ResponseEntity
                .ok(Map.of("status", "success", "message", "Patient details uploaded successfully"));
    }

    @GetMapping("/getHistory/{patientId}")
    public ResponseEntity<?> getPatientHistory(@PathVariable String patientId) {
        PatientHelper history = patientService.getPatientHistory(patientId);
        return ResponseEntity.ok(history);
    }





}








