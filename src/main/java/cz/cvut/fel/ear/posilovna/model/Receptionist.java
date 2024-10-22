package cz.cvut.fel.ear.posilovna.model;

import jakarta.persistence.*;
@Entity
public class Receptionist extends Client {
    private double salary;

    @ManyToOne
    @JoinColumn(name = "gym_id")
    private Gym gym;

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public Gym getGym() {
        return gym;
    }

    public void setGym(Gym gym) {
        this.gym = gym;
    }
}
