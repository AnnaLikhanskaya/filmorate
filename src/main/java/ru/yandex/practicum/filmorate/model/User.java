package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class User {
    private Integer id;
    @Email(message = "Некорректный формат электронной почты")
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}




