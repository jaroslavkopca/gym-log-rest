package cz.cvut.fel.ear.posilovna.rest.facade;

import cz.cvut.fel.ear.posilovna.model.Member;


/// moznost pridat strategy/state patern pro ruzne stavy membershipu
public class MemberStatisticsDTO {
    private String memberName;
    private int id;
    private int overallDuration;
    private double averageIntensity;

    public void setId(Member member){
        this.id = member.getClient().getId();
    }
    public void setMemberName(Member member){
        this.memberName = member.getClient().getName()+" "+member.getClient().getSurname();
    }
    public void setOverallDuration(int overallDuration){
        this.overallDuration = overallDuration;
    }
    public void setAverageIntensity(double averageIntensity) {
        this.averageIntensity = averageIntensity;
    }
    public int getId(){
        return this.id;
    }
    public String getMemberName(){
        return this.memberName;
    }
    public int getOverallDuration(){
        return this.overallDuration;
    }
    public double getAverageIntensity(){
        return this.averageIntensity;
    }
}
