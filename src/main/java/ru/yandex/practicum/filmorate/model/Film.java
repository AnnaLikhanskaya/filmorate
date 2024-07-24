package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private Integer id;
    private List<Integer> likes;
    @NotBlank(message = "Название отсутствует")
    private String name;
    @NotNull(message = "Фильм не может существовать без описания")
    @Size(max = 200, message = "Максимальное кол-во символов - 200")
    private String description;
    @NotNull(message = "Дата не может быть пустой")
    private LocalDate releaseDate;
    @NotNull(message = "Продолжительность не может отсутствовать")
    @Positive(message = "Продолжитьльность не может быть отрицательной")
    private long duration;

    public List<Integer> getLikes() {
        createLikes();
        return likes;
    }

    public void addLike(Integer id) {
        createLikes();
        likes.add(id);
    }

    public void deleteLike(Integer id) {
        createLikes();
        likes.remove(id);
    }

    public int getCountLikes() {
        createLikes();
        return likes.size();
    }

    private void createLikes() {
        if (likes == null) {
            likes = new ArrayList<>();
        }
    }
}



