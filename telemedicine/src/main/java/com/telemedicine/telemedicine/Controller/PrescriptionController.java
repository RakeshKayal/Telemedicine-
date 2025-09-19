package com.telemedicine.telemedicine.Controller;

import com.telemedicine.telemedicine.Model.consultation;
import com.telemedicine.telemedicine.Model.prescription;
import com.telemedicine.telemedicine.Service.ConsultationService;
import com.telemedicine.telemedicine.Service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;

@RestController
@RequestMapping("/api/v1/prescription")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"},
        allowCredentials = "true")
public class PrescriptionController {

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private PrescriptionService prescriptionService;


    @PostMapping("/add/{consultationId}")
    public ResponseEntity<?> addPrescription(
            @PathVariable String consultationId,
            @RequestParam(value = "image", required = false) MultipartFile image) {



        Optional<consultation> consult = consultationService.findById(consultationId);

        if (consult.isEmpty()) {
            return ResponseEntity.badRequest().body("Consultation not found");
        }


        if (!"APPROVED".equalsIgnoreCase(consult.get().getAppointmentStatus())) {
            return ResponseEntity.badRequest().body("Prescription can only be added for approved consultations");
        }


        prescription p = new prescription();
        p.setConsultation(consult.get());

        p.setIssuedAt(LocalDateTime.now());


        if (image != null && !image.isEmpty()) {
            try {
                String uploadDir = "src/main/resources/static/Prescription/";
                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                Path filePath = Paths.get(uploadDir + fileName);

                Files.createDirectories(filePath.getParent());
                Files.write(filePath, image.getBytes());

                p.setImageUrl("/Prescription/" + fileName);
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Error saving image: " + e.getMessage());
            }
        }


        prescription saved = prescriptionService.save(p);

        return ResponseEntity.ok(saved);
    }
}