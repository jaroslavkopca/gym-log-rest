package cz.cvut.fel.ear.posilovna.dao;

import cz.cvut.fel.ear.posilovna.PosilovnaApplication;
import cz.cvut.fel.ear.posilovna.environment.Generator;
import cz.cvut.fel.ear.posilovna.model.Member;
import cz.cvut.fel.ear.posilovna.exception.PersistenceException;
import cz.cvut.fel.ear.posilovna.service.SystemInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

// For explanatory comments, see ProductDaoTest
@DataJpaTest
@ComponentScan(basePackages = "cz.cvut.fel.ear.posilovna.dao", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SystemInitializer.class)})
@ActiveProfiles("test")
public class BaseDaoTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private MemberDao sut;

    private static final Generator generator = Generator.getInstance();

    @Test
    public void persistSavesSpecifiedInstance() {
        final Member member = generator.generateMember();
        sut.persist(member);
        assertNotNull(member.getId());

        final Member result = em.find(Member.class, member.getId());
        assertNotNull(result);
        assertEquals(member.getId(), result.getId());
//        assertEquals(member.getName(), result.getName());
    }

    @Test
    public void findRetrievesInstanceByIdentifier() {
        final Member member = generator.generateMember();
        em.persistAndFlush(member);
        assertNotNull(member.getId());

        final Member result = sut.find(member.getId());
        assertNotNull(result);
        assertEquals(member.getId(), result.getId());
//        assertEquals(member.getName(), result.getName());
    }

    @Test
    public void findAllRetrievesAllInstancesOfType() {
        final Member memberOne = generator.generateMember();
        em.persistAndFlush(memberOne);
        final Member memberTwo = generator.generateMember();
        em.persistAndFlush(memberTwo);

        final List<Member> result = sut.findAll();
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(c -> c.getId().equals(memberOne.getId())));
        assertTrue(result.stream().anyMatch(c -> c.getId().equals(memberTwo.getId())));
    }

    @Test
    public void updateUpdatesExistingInstance() {
        final Member member = generator.generateMember();
        em.persistAndFlush(member);

        final Member update = new Member();
        update.setId(member.getId());
        final String newName = "New member name";
        update.setId(1234131);
        sut.update(update);

        final Member result = sut.find(member.getId());
        assertNotNull(result);
        assertEquals(member.getId(), result.getId());
    }

    @Test
    public void removeRemovesSpecifiedInstance() {
        final Member member = generator.generateMember();
        em.persistAndFlush(member);
        assertNotNull(em.find(Member.class, member.getId()));
        em.detach(member);

        sut.remove(member);
        assertNull(em.find(Member.class, member.getId()));
    }

    @Test
    public void removeDoesNothingWhenInstanceDoesNotExist() {
        final Member member = generator.generateMember();
        member.setId(123);
        assertNull(em.find(Member.class, member.getId()));

        sut.remove(member);
        assertNull(em.find(Member.class, member.getId()));
    }

    @Test
    public void exceptionOnPersistInWrappedInPersistenceException() {
        final Member member = generator.generateMember();
        em.persistAndFlush(member);
        em.remove(member);
        assertThrows(PersistenceException.class, () -> sut.update(member));
    }

    @Test
    public void existsReturnsTrueForExistingIdentifier() {
        final Member member = generator.generateMember();
        em.persistAndFlush(member);
        assertTrue(sut.exists(member.getId()));
        assertFalse(sut.exists(-1));
    }
}
