package com.example.carshop.dao;

import com.example.carshop.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@EnableJpaRepositories
public interface CarDAO extends JpaRepository<Car, Integer> {

    Car save(Car car);

    Car findCarById(int id);

    List<Car> findAll();

    List<Car> findCarByUserId(int id);

    List<Car> findCarByName(String name);

    List<Car> findCarByModel(String model);

    List<Car> findCarByModelAndName(String model, String name);

}
