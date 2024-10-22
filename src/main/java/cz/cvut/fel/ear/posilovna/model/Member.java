package cz.cvut.fel.ear.posilovna.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "Membership.findByClientIdOrderByNewest",
                query = "SELECT m FROM Membership m WHERE m.member.client.id = :clientId ORDER BY m.activationDate ASC"
        )
})
public class Member{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private int age;
    private double weight;
    private double height;
    private boolean membership;

    @OneToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    @JsonBackReference
    private Client client;
    @OneToMany(mappedBy = "member", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private List<Statistics> statistics;

    @OneToMany(mappedBy = "member", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private List<Payment> payments;

    @OrderBy("duration DESC")
    @OneToMany(mappedBy = "member", cascade = CascadeType.MERGE,fetch = FetchType.EAGER)
    private List<WorkoutRecord> workoutRecords;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Membership> memberships = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "Member_GroupWorkout",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "group_workout_id"))
    private List<GroupWorkout> groupWorkouts;


    public Member() {}

    public Member(String name, String surname, Integer age, double weight, double height,Client client) {
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.client = client;
    }


    public void setAge(int age) {
        this.age = age;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public List<Statistics> getStatistics() {
        return statistics;
    }

    public void setStatistics(List<Statistics> statistics) {
        this.statistics = statistics;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public List<WorkoutRecord> getWorkoutRecords() {
        return workoutRecords;
    }

    public void setWorkoutRecords(List<WorkoutRecord> workoutRecords) {
        this.workoutRecords = workoutRecords;
    }

    public List<GroupWorkout> getGroupWorkouts() {
        return groupWorkouts;
    }

    public void setGroupWorkouts(List<GroupWorkout> groupWorkouts) {
        this.groupWorkouts = groupWorkouts;
    }

    public boolean isMembership() {
        return membership;
    }

    public void setMembership(boolean membership) {
        this.membership = membership;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Membership> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<Membership> memberships) {
        this.memberships = memberships;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void addMemebrship(Membership membership) {
        memberships.add(membership);
    }

    public void addWorkoutRecord(WorkoutRecord workoutRecord) {
        Objects.requireNonNull(workoutRecord);
        if (workoutRecords == null) {
            this.workoutRecords = new ArrayList<>();
        }
        workoutRecords.add(workoutRecord);
    }
}

