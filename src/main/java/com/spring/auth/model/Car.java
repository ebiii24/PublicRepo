package com.spring.auth.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "Cars")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="vehId")
    private Long vehId;

    @Column(name="make")
    private String make;

    @Column(name="model")
    private String model;

}
