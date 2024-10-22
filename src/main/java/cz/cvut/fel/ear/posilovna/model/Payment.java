package cz.cvut.fel.ear.posilovna.model;

import jakarta.persistence.*;
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String date;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    // Constructors, getters, setters
}
