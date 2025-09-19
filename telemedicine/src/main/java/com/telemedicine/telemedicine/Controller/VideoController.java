package com.telemedicine.telemedicine.Controller;

import com.telemedicine.telemedicine.Model.doctor;
import com.telemedicine.telemedicine.Repository.DoctorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@RestController
@RequestMapping("/api/v1/video")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"},
        allowCredentials = "true")
public class VideoController {

    @Autowired
    private DoctorRepo doctorRepo;

    @PostMapping("/start")
    public ResponseEntity<?> startCall(@RequestParam String doctorEmail) {
        doctor doctor = doctorRepo.findByEmail(doctorEmail);
               if(doctor==null){
                   return ResponseEntity.status(HttpStatus.NOT_FOUND).body("doctor not found");
               }

        if (doctor.isBusy()) {
            return ResponseEntity.badRequest()
                    .body("Doctor is busy, please try later.");
        }

        doctor.setBusy(true);
        doctorRepo.save(doctor);

        return ResponseEntity.ok(Collections.singletonMap("meetingUrl", doctor.getGoogleMeetLink()));
    }

    @PostMapping("/end")
    public ResponseEntity<?> endCall(@RequestParam String doctorEmail) {
        doctor doctor = doctorRepo.findByEmail(doctorEmail);

        if(doctor==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("doctor not found");
        }
        doctor.setBusy(false);
        doctorRepo.save(doctor);

        return ResponseEntity.ok("Doctor is now available.");
    }
}
