package cz.cvut.fel.ear.posilovna.service;

import cz.cvut.fel.ear.posilovna.dao.ClientDao;
import cz.cvut.fel.ear.posilovna.model.*;
import cz.cvut.fel.ear.posilovna.dao.MemberDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class MemberService {

    private final MemberDao dao;
    private final ClientDao clientDao;

    @Autowired
    public MemberService(MemberDao dao, ClientDao clientDao) {
        this.dao = dao;
        this.clientDao = clientDao;
    }

    @Transactional
    public List<Membership> getMembershipsByClientIdOrderByNewest(Integer clientId) {
        return dao.getMembershipsByClientIdOrderByNewest(clientId);
    }



    @Transactional
    public Member createMemberFromClient(Client client) {
        Member newMember = new Member();
        if  (client.isRegistered()){
            return  null;
        }
        newMember.setClient(client);

        client.setRegistered(true);
        client.setMember(newMember);
        client.setRole(Role.MEMBER);
        clientDao.update(client);

//        dao.persist(newMember);
        return newMember;
    }

    @Transactional
    public boolean activateMembership(Member member){
        if (dao.hasActiveMembership(member.getId())){
            System.out.println("Jsme tu");
            return false;
        }else{
            dao.addMembership(member);
            dao.update(member);
            clientDao.update(member.getClient());
            return true;
        }
    }
    public void deactivateMembership(Member member) throws Exception {
        dao.deactivateMembership(member.getId());
    }
    public void prolongMembership(Member member){
        dao.updateMembership(member.getId());
    }
    public void checkMembershipTimeLeft(){}

    public Member getMemberById(Long memberId) {
        return dao.find(memberId.intValue());
    }

    public List<Member> getAllMembers() {
        return dao.findAll();
    }
    @Transactional
    public void persist(Member member) {
        Objects.requireNonNull(member);
        dao.persist(member);
    }

    @Transactional
    public void addWorkoutRecord(Member member, WorkoutRecord workoutRecord){
        Objects.requireNonNull(workoutRecord);
        Objects.requireNonNull(member);
        workoutRecord.setMember(member);
        member.addWorkoutRecord(workoutRecord);
        dao.update(member);
    }


}



