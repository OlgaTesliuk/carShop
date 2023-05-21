package com.example.carshop.service;

import com.example.carshop.dao.CarDAO;
import com.example.carshop.dao.UserDAO;
import com.example.carshop.exceptions.NotFoundCarsException;
import com.example.carshop.exceptions.NotPremiumUserException;
import com.example.carshop.models.Car;
import com.example.carshop.models.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CarService {
    private UserDAO userDAO;
    private CarDAO carDAO;

    public Car addCarInCurrentUser(Car car, String header) {
        if (header != null) {
            String clearToken = header.substring(7);
            User userFromDB = userDAO.findUserByToken(clearToken);
            if (userFromDB != null && userFromDB.isIsPremium() || userFromDB != null && userFromDB.getCar().isEmpty()) {
                if (car != null) {
                    car.setUser(userFromDB);
                    carDAO.save(car);
                    return car;
                }
            }
            if (userFromDB != null && !userFromDB.getCar().isEmpty()) {
                throw new NotPremiumUserException("User is not premium, already has 1 registered car");
            }
        }
        return null;
    }

    public List<Car> getCarCurrentUser(String header) {
        if (header != null) {
            String clearToken = header.substring(7);
            User userFromDB = userDAO.findUserByToken(clearToken);
            if (userFromDB != null) {
                List<Car> carList = carDAO.findCarByUserId(userFromDB.getId());
                if (carList.isEmpty()) {
                    throw new NotFoundCarsException("No cars found");
                }
                return carList;
            }
        }
        return null;
    }

    public Car deleteCartInCurrentUser(Car car, String header) {
        if (header != null) {
            String clearToken = header.substring(7);
            User userFromDB = userDAO.findUserByToken(clearToken);
            Car byId = carDAO.findCarById(car.getId());
            if (byId != null && userFromDB != null) {
                if (byId.getUser().getId() == userFromDB.getId()) {
                    carDAO.save(byId);
                    return byId;
                }
            }
        }
        return null;
    }

    public int findAveragePriceByModelAndName(String model, String name, String header) {
        if (header != null) {
            String clearToken = header.substring(7);
            User userFromDB = userDAO.findUserByToken(clearToken);
            if (userFromDB != null && userFromDB.isIsPremium()) {
                List<Car> cars = carDAO.findCarByModelAndName(model, name);
                int sum = cars.stream().map(Car::getPrice).reduce(0, Integer::sum);
                if (!cars.isEmpty()) {
                    return sum / cars.size();
                }
            }
        }
        return 0;
    }
}
