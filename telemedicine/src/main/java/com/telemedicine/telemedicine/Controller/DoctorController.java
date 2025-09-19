package com.telemedicine.telemedicine.Controller;

import com.telemedicine.telemedicine.Helper.DoctorHelper;
import com.telemedicine.telemedicine.Model.consultation;
import com.telemedicine.telemedicine.Model.doctor;
import com.telemedicine.telemedicine.Model.patient;
import com.telemedicine.telemedicine.Service.ConsultationService;
import com.telemedicine.telemedicine.Service.DoctorService;
import com.telemedicine.telemedicine.Service.PatientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/v1/doctor")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"},
        allowCredentials = "true")
public class DoctorController {

    @Autowired
    DoctorService doctorService;
    @Autowired
    ConsultationService consultationService;
    @Autowired
    PatientService patientService;

    @PostMapping("/register")
    public ResponseEntity<?> reg(@RequestBody doctor d){
        doctor doctor = doctorService.find(d.getEmail());
        if(doctor==null){
            doctorService.save(d);
            return ResponseEntity.status(HttpStatus.CREATED).body("Doctor Save Sucessfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("doctor email already exits");

    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody doctor d) {
        // Find doctor by email and password
        doctor loggedDoctor = doctorService.findByEmailAndPassword(d.getEmail(), d.getPassword());

        if (loggedDoctor == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Invalid email or password"));
        }

        // Return full doctor info as JSON
        Map<String, Object> response = new HashMap<>();
        response.put("docId", loggedDoctor.getDocId());
        response.put("name", loggedDoctor.getName());
        response.put("email", loggedDoctor.getEmail());
        response.put("mobileNo", loggedDoctor.getMobileNo());
        response.put("password", loggedDoctor.getPassword());
        response.put("specialization", loggedDoctor.getSpecialization());
        response.put("yearOfExperience", loggedDoctor.getYearOfExperience());
        response.put("googleMeetLink", loggedDoctor.getGoogleMeetLink());
        response.put("consultations", loggedDoctor.getConsultations());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }



    @GetMapping("/get")
    public ResponseEntity<?> allDoc(){
        List<doctor> all = doctorService.findAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/getDoctor/{id}")
    public ResponseEntity<DoctorHelper> getDoctorConsult(@PathVariable String id) {

        List<consultation> consultationList = consultationService.findAllByDoctorId(id);

        if (consultationList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DoctorHelper helper = new DoctorHelper();
        helper.setDoctorId(id);

        List<DoctorHelper.ConsultationInfo> consultationInfos = new ArrayList<>();
        for (consultation c : consultationList) {
            DoctorHelper.ConsultationInfo info = new DoctorHelper.ConsultationInfo();
            info.setConsultationId(c.getConId());
            if (c.getPatient() != null) info.setPatientDocument(c.getPatient().getPatientDocument());
            consultationInfos.add(info);
        }

        helper.setConsultations(consultationInfos);
        return ResponseEntity.ok(helper);
    }

    @GetMapping("/requests/{doctorId}")
    public ResponseEntity<List<DoctorHelper.ConsultationInfo>> getPendingRequests(@PathVariable String doctorId) {

//         Fetch all consultations of this doctor with status "REQUEST"
        List<consultation> requests = consultationService.findAllByDoctorIdAndStatus(doctorId, "REQUEST");

        if (requests.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<DoctorHelper.ConsultationInfo> consultationInfos = new ArrayList<>();
        for (consultation c : requests) {
            DoctorHelper.ConsultationInfo info = new DoctorHelper.ConsultationInfo();
            info.setConsultationId(c.getConId());
            if (c.getPatient() != null) info.setPatientDocument(c.getPatient().getPatientDocument());
            consultationInfos.add(info);
        }

        return ResponseEntity.ok(consultationInfos);
    }



    @PutMapping("/updateStatus/{consultationId}")
    public ResponseEntity<String> updateConsultationStatus(
            @PathVariable String consultationId,
            @RequestParam String status // e.g., "APPROVED", "REJECTED", "SCHEDULED"
    ) {
        Optional<consultation> consultationOpt = consultationService.findById(consultationId);

        if (!consultationOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        consultation consultation = consultationOpt.get();
        consultation.setAppointmentStatus(status.toUpperCase());
        consultationService.save(consultation);

        return ResponseEntity.ok("Consultation status updated successfully to " + status);
    }

    @GetMapping("/specializations")
    public List<String> getAllSpecializations() {
        return doctorService.getAllSpecializations();
    }
    @GetMapping("/sp/{specialist}")
    public List<doctor> getfromSpecialist(@PathVariable String specialist ){
        List<doctor> bySpecialization = doctorService.findBySpecialization(specialist);
        return  bySpecialization;

    }


    @GetMapping("/patient/{docId}")
    public ResponseEntity<List<Map<String, Object>>> getPatientsByDoctor(@PathVariable String docId) {
        // Get today's date (without time)
        LocalDate today = LocalDate.now();

        // Fetch all consultations for this doctor
        List<consultation> consultations = consultationService.findByDoctorId(docId);

        // Filter consultations for today
        List<Map<String, Object>> result = consultations.stream()
                .filter(c -> c.getScheduledDate() != null && c.getScheduledDate().toLocalDate().isEqual(today))
                .map(c -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("patientId", c.getPatient().getPatientId());
                    map.put("patientAge", c.getPatient().getAge());
                    map.put("patientName", c.getPatient().getName());
                    map.put("patientAdhar", c.getPatient().getAddharId());
                    map.put("patientSymptomp", c.getPatient().getPatientDocument().getPatientSymptomp());
                    map.put("status", c.getAppointmentStatus()); // ✅ from your entity
                    map.put("scheduledAt", c.getScheduledDate()); // ✅ today's scheduled time
                    return map;
                })
                .toList();

        return ResponseEntity.ok(result);
    }





}
