package cz.cvut.fel.ear.posilovna.model;

import jakarta.persistence.*;
@Entity
public class Statistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private EntryType type;

    private String time;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
