package cz.cvut.fel.ear.posilovna.environment;

import cz.cvut.fel.ear.posilovna.model.Client;
import cz.cvut.fel.ear.posilovna.model.Member;
import cz.cvut.fel.ear.posilovna.model.Role;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Generator {

    private static final Generator INSTANCE = new Generator();
    private static final Random RAND = new Random();
    private static final String[] NAMES = {"Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace"};
    private static final String[] SURNAMES = {"Smith", "Johnson", "Brown", "Lee", "Davis", "Taylor"};
    private static final Set<String> usedUsernames = new HashSet<>();

    // Private constructor to prevent external instantiation
    private Generator() {
    }

    // Public method to access the singleton instance
    public static Generator getInstance() {
        return INSTANCE;
    }

    public int randomInt() {
        return RAND.nextInt();
    }

    public int randomInt(int max) {
        return RAND.nextInt(max);
    }

    public int randomInt(int min, int max) {
        assert min >= 0;
        assert min < max;

        int result;
        do {
            result = randomInt(max);
        } while (result < min);
        return result;
    }

    public boolean randomBoolean() {
        return RAND.nextBoolean();
    }

    public Member generateMember() {
        final Member member = new Member();
        member.setAge(randomInt());
        return member;
    }

    public Client generateClient() {
        String name = getRandomElement(NAMES);
        String surname = getRandomElement(SURNAMES);
        String username = generateUniqueUsername(name, surname);

        Client client = new Client(name, surname);
        client.setId(0);
        client.setRole(Role.CLIENT);
        client.setUsername(username);
        client.setPassword(username);
        return client;
    }

    private String getRandomElement(String[] array) {
        int randomIndex = new Random().nextInt(array.length);
        return array[randomIndex];
    }

    private String generateUniqueUsername(String name, String surname) {
        String username = (name.charAt(0) + surname).toLowerCase();
        int count = 1;

        while (usedUsernames.contains(username)) {
            username = (name.charAt(0) + surname + count).toLowerCase();
            count++;
        }

        usedUsernames.add(username);
        return username;
    }
}
