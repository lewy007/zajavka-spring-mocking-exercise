package pl.zajavka;

public class Main {
    public static void main(String[] args) {
        UserManagementService userManagementService = new UserManagementService(new UserManagementInMemoryRepository());

        User user = new User("Adam", "Tabaka", "email@email.pl");
        userManagementService.create(user);

        System.out.println(userManagementService.findByEmail("email1@wp.pl"));
    }
}