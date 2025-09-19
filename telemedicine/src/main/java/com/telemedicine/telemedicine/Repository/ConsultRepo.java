package com.telemedicine.telemedicine.Repository;

import com.telemedicine.telemedicine.Model.doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.telemedicine.telemedicine.Model.consultation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface ConsultRepo extends JpaRepository<consultation, String> {

//    Optional<consultation> findByDoc_Id(String id);

   // List<consultation> findAllByDoc_Id(String id);

   // List<consultation> findAllByDoctorId(String id);

    List<consultation> findAllByDoctor_DocId(String id);

   // List<consultation> findAllByDoctor_DocIdAndStatus(String doctorId, String request);

    List<consultation> findAllByDoctor_DocIdAndAppointmentStatus(String doctorId, String request);
    consultation findTopByDoctorOrderByScheduledDateDesc(doctor doctor);

    List<consultation> findByScheduledDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<consultation> findByDoctor_DocId(String docId);
}
