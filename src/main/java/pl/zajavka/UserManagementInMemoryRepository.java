package pl.zajavka;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserManagementInMemoryRepository implements UserManagementRepository {

    private final Map<String, User> userMap = new HashMap<>();

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userMap.get(email));
    }

    @Override
    public List<User> findAll() {
        return userMap.values().stream().toList();
        // lub return new ArrayList<>(userMap.values());
    }

    @Override
    public void create(User user) {
        userMap.put(user.getEmail(), user);
    }

    @Override
    public List<User> findByName(String name) {
        return userMap.values().stream()
                .filter(user -> name.equals(user.getName()))
                .toList();
    }

    @Override
    public void delete(String email) {
        userMap.remove(email);
    }

    @Override
    public void update(String email, User withEmail) {
//        userMap.replace(email, withEmail);
        userMap.put(email, withEmail);
    }
}
