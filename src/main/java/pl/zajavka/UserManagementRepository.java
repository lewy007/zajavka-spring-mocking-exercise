package pl.zajavka;

import java.util.List;
import java.util.Optional;

public interface UserManagementRepository {
    Optional<User> findByEmail(String email);

    List<User> findAll();

    void create(User user);

    List<User> findByName(String name);

    void delete(String email);

    void update(String email, User withEmail);
}
