## testy jednostkowe z wykorzystaniem Mockito do zaslepienia repozytorium odpowiedialengo za baze danych.

### metoda uzyta thenReturn x 2 oznacza, ze stubbing userManagementRepository.findByEmail(user1.getEmail())) uzyty za pierwszym razem zwroci -> thenReturn(Optional.empty()),  a uzyty za drugim razem -> thenReturn(Optional.of(user1));

```java
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
}
```