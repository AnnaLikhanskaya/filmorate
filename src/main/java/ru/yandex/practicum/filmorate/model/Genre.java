package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Genre implements Comparable<Genre> {
    private Integer id;
    private String name;

    @Override
    public int compareTo(Genre other) {
        return Integer.compare(this.id, other.id);
    }
}
