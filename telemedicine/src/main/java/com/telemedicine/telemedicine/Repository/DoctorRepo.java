package com.telemedicine.telemedicine.Repository;

import com.telemedicine.telemedicine.Model.doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepo extends JpaRepository<doctor,String> {
    doctor findByEmail(String email);

    //doctor findByPassword(String password);

    doctor findByName(String dname);

    doctor findByEmailAndPassword(String email, String password);

    @Query("SELECT DISTINCT d.specialization FROM doctor d")
    List<String> findDistinctSpecializations();

    List<doctor> findBySpecialization(String specialist);
}
