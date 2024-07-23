package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class User {
    private Integer id;
    private List<Integer> friends;
    @NotNull(message = "Email не может отсутствовать")
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат электронной почты")
    private String email;
    @NotNull(message = "Логин не может быть null")
    @NotBlank(message = "Логин не может быть пустым")
    private String login;
    private String name;
    @NotNull(message = "День рождения отсутствует")
    @Past(message = "День рождения не должен быть в будущем")
    private LocalDate birthday;


    public List<Integer> getFriends() {
        createFriends();
        return friends;
    }

    public void addFriend(Integer id) {
        createFriends();
        friends.add(id);
    }

    public void deleteFriend(Integer id) {
        createFriends();
        friends.remove(id);
    }

    private void createFriends(){
        if (friends == null) {
            friends = new ArrayList<>();
        }
    }
}




