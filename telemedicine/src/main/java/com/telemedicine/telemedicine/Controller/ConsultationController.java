package com.telemedicine.telemedicine.Controller;

import com.telemedicine.telemedicine.Model.consultation;
import com.telemedicine.telemedicine.Model.doctor;
import com.telemedicine.telemedicine.Model.patient;
import com.telemedicine.telemedicine.Service.ConsultationService;
import com.telemedicine.telemedicine.Service.DoctorService;
import com.telemedicine.telemedicine.Service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/consult")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"},
        allowCredentials = "true")
public class ConsultationController {

    @Autowired
    ConsultationService consultationService;

    @Autowired
    PatientService patientService;

    @Autowired
    DoctorService doctorService;

    // Number of days between scheduled consultations
    private static final int CONSULTATION_INTERVAL_DAYS = 2;
    private static final List<String> VALID_STATUSES = List.of("REQUEST", "ONGOING", "COMPLETED");
    @PostMapping("/consult")
    public ResponseEntity<?> consult(@RequestBody consultation consultation) {

        patient patientEntity = patientService.findByAadhaar(consultation.getPatient().getAddharId());
        doctor doctorEntity = doctorService.find(consultation.getDoctor().getEmail());

        if (patientEntity == null || doctorEntity == null) {
            return ResponseEntity.badRequest().body("Invalid patient or Doctor");
        }

        consultation c = new consultation();
        c.setPatient(patientEntity);
        c.setDoctor(doctorEntity);
        c.setAppointmentStatus("REQUEST");
        c.setTime(LocalDateTime.now());

        // Schedule logic with working hours
        consultation lastConsultation = consultationService.findLatestScheduledForDoctor(doctorEntity);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime scheduledDate;

        LocalTime fromTime = doctorEntity.getAvailableFrom(); // already LocalTime
        LocalTime toTime = doctorEntity.getAvailableTo();     // already LocalTime

        LocalDateTime todayStart = now.with(fromTime);
        LocalDateTime todayEnd = now.with(toTime);

        if (lastConsultation == null || lastConsultation.getScheduledDate().isBefore(now)) {
            // Doctor is free, schedule now if within working hours
            if (now.isBefore(todayStart)) {
                scheduledDate = todayStart; // schedule at start of workday
            } else if (now.isAfter(todayEnd)) {
                scheduledDate = todayStart.plusDays(1); // schedule next day at start of workday
            } else {
                scheduledDate = now; // schedule immediately
            }
        } else {
            // Doctor is busy, schedule after last consultation
            LocalDateTime nextAvailable = lastConsultation.getScheduledDate().plusDays(2);
            LocalDateTime nextAvailableStart = nextAvailable.with(fromTime);
            LocalDateTime nextAvailableEnd = nextAvailable.with(toTime);

            if (nextAvailable.isBefore(nextAvailableStart)) {
                scheduledDate = nextAvailableStart;
            } else if (nextAvailable.isAfter(nextAvailableEnd)) {
                scheduledDate = nextAvailableStart.plusDays(1);
            } else {
                scheduledDate = nextAvailable;
            }
        }

        c.setScheduledDate(scheduledDate);

        consultationService.save(c);

        Map<String, Object> result = new HashMap<>();
        result.put("conId", c.getConId());
        result.put("appointmentStatus", c.getAppointmentStatus());
        result.put("time", c.getTime());
        result.put("scheduledDate", c.getScheduledDate());
        result.put("doctor", Map.of("name", doctorEntity.getName(), "email", doctorEntity.getEmail()));
        result.put("patient", Map.of("name", patientEntity.getName(), "adharId", patientEntity.getAddharId()));

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }



    @PutMapping("/updateStatus/{consultId}")
    public ResponseEntity<?> updateConsultationStatus(
            @PathVariable String consultId,
            @RequestParam String status) {

        if (!VALID_STATUSES.contains(status.toUpperCase())) {
            return ResponseEntity.badRequest().body("Invalid status. Allowed: " + VALID_STATUSES);
        }

        consultation consult = consultationService.findById(consultId)
                .orElseThrow(() -> new RuntimeException("Consultation not found"));

        consult.setAppointmentStatus(status.toUpperCase());
        consultationService.save(consult);

        return ResponseEntity.ok(Map.of(
                "consultId", consult.getConId(),
                "status", consult.getAppointmentStatus()
        ));
    }

    @GetMapping("/videoLink/{consultId}")
    public ResponseEntity<?> getVideoLink(
            @PathVariable String consultId,
            @RequestParam int patientAdhar) {

        consultation consult = consultationService.findById(consultId)
                .orElseThrow(() -> new RuntimeException("Consultation not found"));

        // Validate consultation status
        if ("COMPLETED".equalsIgnoreCase(consult.getAppointmentStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Consultation already completed");
        }

        if ("ONGOING".equalsIgnoreCase(consult.getAppointmentStatus())) {
            // Only the same patient can join
            if (consult.getPatient().getAddharId() != patientAdhar) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not allowed to join this session");
            }
        }

        // Mark consultation as ongoing when video link is first fetched
        if ("REQUEST".equalsIgnoreCase(consult.getAppointmentStatus())) {
            consult.setAppointmentStatus("ONGOING");
            consultationService.save(consult);
        }

        return ResponseEntity.ok(Map.of(
                "consultId", consult.getConId(),
                "status", consult.getAppointmentStatus(),
                "meetLink", consult.getDoctor().getGoogleMeetLink()
        ));
    }

    @PutMapping("/complete/{consultId}")
    public ResponseEntity<?> completeConsultation(@PathVariable String consultId) {
        consultation consult = consultationService.findById(consultId)
                .orElseThrow(() -> new RuntimeException("Consultation not found"));

        consult.setAppointmentStatus("COMPLETED");
        consultationService.save(consult);

        return ResponseEntity.ok(Map.of(
                "consultId", consult.getConId(),
                "status", "COMPLETED",
                "message", "Consultation completed successfully"
        ));
    }



    @GetMapping("/todayAppointments")
    public ResponseEntity<?> todayAppointments() {


        // Fetch all consultations scheduled today
        List<consultation> todayConsultations = consultationService.findByScheduledDateBetween();

        // Map consultations to a response DTO
        List<Map<String, Object>> result = new ArrayList<>();
        for (consultation c : todayConsultations) {
            Map<String, Object> map = new HashMap<>();
            map.put("scheduledDate", c.getScheduledDate());
            map.put("time", c.getTime());
            map.put("status", c.getAppointmentStatus());
            map.put("doctor", Map.of(
                    "name", c.getDoctor().getName(),
                    "email", c.getDoctor().getEmail()
            ));
            map.put("patient", Map.of(
                    "name", c.getPatient().getName(),
                    "adharId", c.getPatient().getAddharId(),
                    "symptoms", c.getPatient().getPatientDocument().getPatientSymptomp() // or c.getSymptoms() depending on your model
            ));
            result.add(map);
        }

        return ResponseEntity.ok(result);
    }

}
