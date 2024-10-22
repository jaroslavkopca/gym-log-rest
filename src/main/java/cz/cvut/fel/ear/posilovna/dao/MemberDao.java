package cz.cvut.fel.ear.posilovna.dao;

import cz.cvut.fel.ear.posilovna.model.Client;
import cz.cvut.fel.ear.posilovna.model.Member;
import cz.cvut.fel.ear.posilovna.model.Membership;
import cz.cvut.fel.ear.posilovna.model.Role;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public class MemberDao extends BaseDao<Member>{
    public MemberDao(){super(Member.class);}

    public Client findClient(int id) {
        Client client = em.find(Client.class, id);
        if (client != null && client.isRegistered()) {
            return null;
        }
        return client;
    }

    @Transactional
    public void addMembership(Member member) {
        if (member != null) {
            Membership membership = new Membership();
            membership.setMember(member);
            membership.activate();
            member.addMemebrship(membership);
            member.getClient().setRole(Role.MEMBER_WITH_MEMBERSHIP);

            em.merge(member);
            em.merge(membership);  // Changed this part
        }
    }

    @Transactional
    public List<Membership> getMemberships(int memberId) {
        Member member = em.find(Member.class, memberId);
        if (member != null) {
            return member.getMemberships();
        }
        return null;
    }
    @Transactional
    public void updateMembership(int memberId) {
        Member member = em.find(Member.class, memberId);
        Membership membership = member.getMemberships().get(0);
        if (membership != null && membership.checkIfActive()){
            membership.setActivationDate(LocalDate.now());
        }else {
            membership.activate();
        }
        em.merge(membership);
    }
    @Transactional
    public boolean hasActiveMembership(int memberId) {
        Member member = em.find(Member.class,memberId);
        return member.isMembership();

//        List<Membership> memberships = getMemberships(memberId);
//        if (!memberships.isEmpty()) {
//            return memberships.stream().anyMatch(Membership::checkIfActive);
//        }
//        return false;
    }

    public void activateMembership(Integer id) {
        Membership membership = em.find(Membership.class, id);
        if (membership != null) {
            membership.activate();
            em.merge(membership);
        }
    }

    @Transactional
    public List<Membership> getMembershipsByClientIdOrderByNewest(Integer clientId) {
        TypedQuery<Membership> query = em.createNamedQuery(
                "Membership.findByClientIdOrderByNewest",
                Membership.class
        );
        query.setParameter("clientId", clientId);
        return query.getResultList();
    }

    @Transactional
    public void deactivateMembership(Integer memberId) throws Exception {
        Member member = em.find(Member.class, memberId);
        if (!member.isMembership()){
            throw new Exception("Does not have membership");
        }
        if (member != null) {
            List<Membership> memberships = member.getMemberships();
            if (!memberships.isEmpty()) {
                Membership membership = memberships.get(memberships.size() - 1);
                if (membership.checkIfActive()) {
                    membership.setActivationDate(null);
                    membership.setIsActive(false);
                    member.setMembership(false);
                    member.getClient().setRole(Role.MEMBER);
                    em.merge(member.getClient());
                    em.merge(member);
                    em.merge(membership);
                }else throw new Exception("Membership is not active");
            }else throw new Exception("Member does not have membership");
        }
//        Membership membership = em.find(Membership.class, id);
//        if (membership != null) {
//            if (membership.checkIfActive()){
//                membership.setActivationDate(null);
//                membership.setIsActive(false);
//                em.merge(membership);
//            }
//        }
    }
}
