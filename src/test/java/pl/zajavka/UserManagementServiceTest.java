package pl.zajavka;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    @InjectMocks
    private UserManagementService userManagementService;

    @Mock
    private UserManagementRepository userManagementRepository;

    @Test
    void shouldCreateUserCorrectly() {

        //given
        var user = someUser();
        Mockito.when(userManagementRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(user));

        //when
        userManagementService.create(user);

        var result = userManagementService.findByEmail(user.getEmail());

        //then
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(user, result.get());

    }

    @Test
    void shouldCreateMultipleUsersCorrectly() {

        //given
        var user1 = someUser().withEmail("email1@gmail.com");
        var user2 = someUser().withEmail("email2@gmail.com");
        var user3 = someUser().withEmail("email3@gmail.com");

        // metoda uzyta thenReturn x 2 oznacza, ze stubbing userManagementRepository.findByEmail(user1.getEmail()))
        // uzyty za pierwszym razem zwroci -> thenReturn(Optional.empty())
        // a uzyty za drugim razem - thenReturn(Optional.of(user1));
        Mockito.when(userManagementRepository.findByEmail(user1.getEmail()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(user1));
        Mockito.when(userManagementRepository.findByEmail(user2.getEmail()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(user2));
        Mockito.when(userManagementRepository.findByEmail(user3.getEmail()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(user3));

        Mockito.when(userManagementRepository.findAll()).thenReturn(List.of(user1, user2, user3));

        //when
        userManagementService.create(user1);
        userManagementService.create(user2);
        userManagementService.create(user3);

        var result1 = userManagementService.findByEmail(user1.getEmail());
        var result2 = userManagementService.findByEmail(user2.getEmail());
        var result3 = userManagementService.findByEmail(user3.getEmail());
        var all = userManagementService.findAll();

        //then
        Assertions.assertEquals(3, all.size());
        Assertions.assertTrue(result1.isPresent());
        Assertions.assertEquals(user1, result1.get());
        Assertions.assertTrue(result2.isPresent());
        Assertions.assertEquals(user2, result2.get());
        Assertions.assertTrue(result3.isPresent());
        Assertions.assertEquals(user3, result3.get());

    }

    @Test
    void shouldFailWhenDuplicatedUserIsCreated() {

        //given
        String duplicatedEmail = "someemail@gmail.com";
        var user1 = someUser().withEmail(duplicatedEmail);
        var user2 = someUser().withEmail(duplicatedEmail);

        Mockito.when(userManagementRepository.findByEmail(duplicatedEmail))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(user1));

        //when, then
        userManagementService.create(user1);

        Throwable exception = Assertions.assertThrows(RuntimeException.class, () -> userManagementService.create(user2));
        Assertions.assertEquals(
                String.format("User with email: [%s] is already created", user1.getEmail()), exception.getMessage());
    }

    @Test
    void shouldFailWhenAddingUsersToRepositoryFails() {

        //given
        String errorMessage = "Error while creating user";
        var user = someUser();

        Mockito.when(userManagementRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        doThrow(new RuntimeException(errorMessage)).when(userManagementRepository).create(user);

        //when, then
        Throwable exception = Assertions.assertThrows(RuntimeException.class, () -> userManagementService.create(user));
        Assertions.assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void shouldFindUsersWithName() {

        //given
        var user1 = someUser().withEmail("email1@gmail.com");
        var user2 = someUser().withEmail("email2@gmail.com");
        var user3 = someUser().withName("newName").withEmail("email3@gmail.com");
        Mockito.when(userManagementRepository.findByName(user1.getName())).thenReturn(List.of(user1, user2));
        Mockito.when(userManagementRepository.findByName(user3.getName())).thenReturn(List.of(user3));
        Mockito.when(userManagementRepository.findAll()).thenReturn(List.of(user1, user2, user3));

        //when
        userManagementService.create(user1);
        userManagementService.create(user2);
        userManagementService.create(user3);

        var result1 = userManagementService.findByName(user1.getName());
        var result2 = userManagementService.findByName(user2.getName());
        var result3 = userManagementService.findByName(user3.getName());
        var all = userManagementService.findAll();

        //then
        Assertions.assertEquals(3, all.size());
        Assertions.assertEquals(
                Stream.of(user1, user2).sorted().collect(Collectors.toList()),
                result1.stream().sorted().collect(Collectors.toList())
        );
        Assertions.assertEquals(
                Stream.of(user1, user2).sorted().collect(Collectors.toList()),
                result2.stream().sorted().collect(Collectors.toList())
        );
        Assertions.assertEquals(List.of(user3), result3);

    }

    @Test
    void shouldModifyUserDataCorrectly() {

        //given
        String email1 = "email1@gmail.com";
        String email2 = "email2@gmail.com";
        String email3 = "email3@gmail.com";
        String emailNew = "newEamil@gmail.com";
        var user1 = someUser().withEmail(email1);
        var user2 = someUser().withEmail(email2);
        var user3 = someUser().withEmail(email3);

        Mockito.when(userManagementRepository.findByEmail(email1))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(user1))
                .thenReturn(Optional.of(user1))
                .thenReturn(Optional.empty());
        Mockito.when(userManagementRepository.findByEmail(email2))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(user1));
        Mockito.when(userManagementRepository.findByEmail(email3))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(user3));
        Mockito.when(userManagementRepository.findByEmail(emailNew))
                .thenReturn(Optional.of(user1.withEmail(emailNew)));
        Mockito.when(userManagementRepository.findAll()).thenReturn(List.of(user1, user2, user3));

        //when
        userManagementService.create(user1);
        userManagementService.create(user2);
        userManagementService.create(user3);

        var all = userManagementService.findAll();
        Assertions.assertEquals(3, all.size());

        var result1 = userManagementService.findByEmail(user1.getEmail());
        userManagementService.update(user1.getEmail(), user1.withEmail(emailNew));
        var result2 = userManagementService.findByEmail(user1.getEmail());
        var result3 = userManagementService.findByEmail(emailNew);
        var allAgain = userManagementService.findAll();

        //then
        Assertions.assertEquals(3, all.size());
        Assertions.assertTrue(result1.isPresent());
        Assertions.assertEquals(user1, result1.get());
        Assertions.assertTrue(result2.isEmpty());
        Assertions.assertTrue(result3.isPresent());
        Assertions.assertEquals(user1.withEmail(emailNew), result3.get());

    }

    @Test
    void shouldThrowWhenModifyingNonExistingUser() {

        //given
        var user1 = someUser();
        String newEmail = "email1@gmail.com";

        //when, then
        Throwable exception = Assertions.assertThrows(RuntimeException.class,
                () -> userManagementService.update(user1.getEmail(), user1.withEmail(newEmail)));
        Assertions.assertEquals(
                String.format("User with email: [%s] doesn't exist", user1.getEmail()), exception.getMessage());

    }

    @Test
    void shouldDeleteUserCorrectly() {

        //given
        var user1 = someUser().withEmail("email1@gmail.com");
        var user2 = someUser().withEmail("email2@gmail.com");
        var user3 = someUser().withEmail("email3@gmail.com");

        Mockito.when(userManagementRepository.findByEmail(user1.getEmail()))
                .thenReturn(Optional.of(user1))
                .thenReturn(Optional.empty());
        Mockito.when(userManagementRepository.findByEmail(user2.getEmail()))
                .thenReturn(Optional.of(user2));
        Mockito.when(userManagementRepository.findByEmail(user3.getEmail()))
                .thenReturn(Optional.of(user3));
        Mockito.when(userManagementRepository.findAll())
                .thenReturn(List.of(user1, user2, user3))
                .thenReturn(List.of(user2, user3));

        //when
        var all = userManagementService.findAll();
        Assertions.assertEquals(3, all.size());

        userManagementService.delete(user1.getEmail());

        var result1 = userManagementService.findByEmail(user1.getEmail());
        var result2 = userManagementService.findByEmail(user2.getEmail());
        var result3 = userManagementService.findByEmail(user3.getEmail());
        var allAgain = userManagementService.findAll();

        //then
        Assertions.assertEquals(2, allAgain.size());
        Assertions.assertTrue(result1.isEmpty());
        Assertions.assertTrue(result2.isPresent());
        Assertions.assertEquals(user2, result2.get());
        Assertions.assertTrue(result3.isPresent());
        Assertions.assertEquals(user3, result3.get());

    }

    @Test
    void shouldThrowWhenDeletingNonExistingUser() {

        //given
        var user1 = someUser().withEmail("email1@gmail.com");

        //when, then
        Throwable exception = Assertions.assertThrows(RuntimeException.class,
                () -> userManagementService.delete(user1.getEmail()));
        Assertions.assertEquals(
                String.format("User with email: [%s] doesn't exist", user1.getEmail()), exception.getMessage());

        // Tutaj mamy pewna kwestie do zastanowienia.
        // Dlaczego ten test przechodzi, pomimo ze nie zdefiniowlaimsy zadnego stuba?

    }

    private static User someUser() {

        return User.builder()
                .name("name")
                .surname("surname")
                .email("email")
                .build();
    }

}