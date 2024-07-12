package com.spring.auth.controller;

import com.spring.auth.model.Car;
import com.spring.auth.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.image.ReplicateScaleFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/main")
public class CarController {

    @Autowired
    private CarRepository carRepository;

    @GetMapping
    public String home(){
        return "You are home";
    }

    @GetMapping("/getAllCars")
    public ResponseEntity<List<Car>> getAllCars(){
        try{
            List<Car> list = new ArrayList<>();
            carRepository.findAll().forEach(list::add);

            if(list.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(list, HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getCarById/{vehId}")
    public ResponseEntity<Car> getPersonById(@PathVariable Long vehId){
        Optional<Car> listData = carRepository.findById(vehId);

        if(listData.isPresent()){
            return new ResponseEntity<>(listData.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/addCars")
    public ResponseEntity<List<Car>> addCars(@RequestBody ArrayList<Car> carList){
        List<Car> newCarList = new ArrayList<>();
        try{
            for(Car car: carList){
                Car carObj = carRepository.save(car);
                newCarList.add(car);
            }

            return new ResponseEntity<>(newCarList, HttpStatus.OK);
        }
        catch(Exception e){
            e.getMessage();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/updatePersonById/{id}")
    public ResponseEntity<Car> updatePersonById(Long id, @RequestBody Car newCarData){
        Optional<Car> oldCarData = carRepository.findById(id);

        if(oldCarData.isPresent()){
            Car updatedCar = oldCarData.get();
            updatedCar.setMake(newCarData.getMake());
            updatedCar.setModel(newCarData.getModel());

            Car carObj = carRepository.save(updatedCar);
            return new ResponseEntity<>(carObj, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/deletePersonById/{id}")
    public ResponseEntity<String> deletePersonById(@PathVariable Long id){
        carRepository.deleteById(id);
        return new ResponseEntity<>("Car Deleted",HttpStatus.OK);
    }

}
