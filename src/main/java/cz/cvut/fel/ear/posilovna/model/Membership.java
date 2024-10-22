package cz.cvut.fel.ear.posilovna.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "membership")
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    @JsonBackReference
    private Member member;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "activation_date")
    private LocalDate activationDate;

    private static final int DURATION_DAYS = 30; // Membership is active for 30 days

    // Default constructor for JPA
    public Membership() {
        this.isActive = false;
        this.activationDate = null;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void activate() {
        member.setMembership(true);
        this.isActive = true;
        this.activationDate = LocalDate.now(); // Set the activation date to the current date
    }

    public boolean checkIfActive() {
        if (isActive && activationDate != null) {
            LocalDate currentDate = LocalDate.now();
            // Calculate the number of days since activation
            int daysSinceActivation = Period.between(activationDate, currentDate).getDays();
            if (daysSinceActivation <= DURATION_DAYS) {
                return true; // Membership is still active
            }
        }
        return false; // Membership is not active
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDate getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(LocalDate activationDate) {
        this.activationDate = activationDate;
    }
}
