package com.telemedicine.telemedicine.Service;

import com.telemedicine.telemedicine.Model.doctor;
import com.telemedicine.telemedicine.Repository.DoctorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {

    @Autowired
    DoctorRepo doctorRepo;


    public doctor save(doctor d){
        return doctorRepo.save(d);
    }

    public doctor find(String email) {
        return  doctorRepo.findByEmail(email);

    }

//    public doctor findpassword(String password) {
//        return  doctorRepo.findByPassword(password);
//    }

    public List<doctor> findAll() {
        return  doctorRepo.findAll();
    }

    public doctor findName(String dname) {
        return  doctorRepo.findByName(dname);
    }

    public doctor findByEmailAndPassword(String email, String password) {

        return doctorRepo.findByEmailAndPassword(email,password);
    }

    public List<String> getAllSpecializations() {
        return doctorRepo.findDistinctSpecializations();
    }

    public List<doctor> findBySpecialization(String specialist) {
        List<doctor> bySpecialization = doctorRepo.findBySpecialization(specialist);
        return  bySpecialization;
    }
}
