package com.telemedicine.telemedicine.Service;

import com.telemedicine.telemedicine.Model.consultation;
import com.telemedicine.telemedicine.Model.doctor;
import com.telemedicine.telemedicine.Repository.ConsultRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ConsultationService {
    @Autowired
    ConsultRepo consultRepo;

    public consultation  save(consultation c){

        return consultRepo.save(c);
    }

    public Optional<consultation> findById(String consultationId) {
        return  consultRepo.findById(consultationId);
    }

//    public Optional<consultation> findByDoc_Id(String id) {
//        return  consultRepo.findByDoc_Id(id);
//    }

    public List<consultation> findAllByDoctorId(String id) {
        return  consultRepo.findAllByDoctor_DocId(id);
    }

    public List<consultation> findAllByDoctorIdAndStatus(String doctorId, String request) {
        return  consultRepo.findAllByDoctor_DocIdAndAppointmentStatus(doctorId,request);
    }

    public consultation findLatestScheduledForDoctor(doctor doctorName) {
        return consultRepo.findTopByDoctorOrderByScheduledDateDesc(doctorName);
    }

    public List<consultation> findByScheduledDateBetween() {
        LocalDateTime startOfDay1 = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay1 = startOfDay1.plusDays(1).minusNanos(1);
        List<consultation> byScheduledDateBetween = consultRepo.findByScheduledDateBetween(startOfDay1, endOfDay1);
        return byScheduledDateBetween;
    }

    public List<consultation> findByDoctorId(String docId) {
        return consultRepo.findByDoctor_DocId(docId);
    }
}
