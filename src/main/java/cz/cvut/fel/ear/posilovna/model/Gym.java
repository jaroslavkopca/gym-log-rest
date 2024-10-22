package cz.cvut.fel.ear.posilovna.model;

import jakarta.persistence.*;
import java.util.List;
@Entity
public class Gym {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String gymName;

    @OneToMany(mappedBy = "gym", cascade = CascadeType.ALL)
    private List<Room> rooms;

    @OneToMany(mappedBy = "gym")
    private List<Receptionist> receptionists;
}
