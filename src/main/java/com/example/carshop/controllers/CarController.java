package com.example.carshop.controllers;

import com.example.carshop.dao.CarDAO;
import com.example.carshop.models.Car;
import com.example.carshop.service.CarService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/car")
public class CarController {
    private CarDAO carDAO;
    private CarService carService;

    @PostMapping("/save")
    public ResponseEntity create(@RequestBody Car car, @RequestHeader("Authorization") String header) {
        try {
            Car addedCar = carService.addCarInCurrentUser(car, header);
            if (addedCar != null) {
                return new ResponseEntity<Car>(addedCar, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
        }
        return new ResponseEntity<String>("Error", HttpStatus.METHOD_NOT_ALLOWED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity getCarByUser
            (@RequestHeader("Authorization") String header) {
        if (header != null) {
            try {
                List<Car> carList = carService.getCarCurrentUser(header);
                return new ResponseEntity<List<Car>>(carList, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<List<Car>>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Car>> getAllCar() {
        return new ResponseEntity<List<Car>>(carDAO.findAll(), HttpStatus.OK);
    }

    @GetMapping("/byModel")
    public ResponseEntity<List<Car>> getByModel(@RequestBody String model) {
        return new ResponseEntity<>(carDAO.findCarByModel(model), HttpStatus.OK);
    }

    @GetMapping("/byMame")
    public ResponseEntity<List<Car>> getByName(@RequestBody String name) {
        return new ResponseEntity<>(carDAO.findCarByName(name), HttpStatus.OK);
    }

    @GetMapping("/byAveragePrice")
    public ResponseEntity<String> getAveragePrice(@RequestBody Car car, @RequestHeader("Authorization") String header) {
        int averagePriceByModelAndName = carService.findAveragePriceByModelAndName(car.getModel(), car.getName(), header);
        if (averagePriceByModelAndName != 0) {
            return new ResponseEntity<String>("Average price of " + car.getModel() + " " + car.getName() + " is "
                    + averagePriceByModelAndName, HttpStatus.OK);
        }
        return new ResponseEntity<String>("Error, no statistic for current model", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{carId}/visit")
    public ResponseEntity<Integer> countVisit(@PathVariable int carId) {
        Car car = carDAO.findCarById(carId);
        var updatedCountVisit = car.getVisit() + 1;
        car.setVisit(updatedCountVisit);
        carDAO.save(car);
        return new ResponseEntity<Integer>(updatedCountVisit, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteCar(@PathVariable int id) {
        carDAO.deleteById(id);
    }
}
