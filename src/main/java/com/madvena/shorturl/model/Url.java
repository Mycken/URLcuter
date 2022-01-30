package com.madvena.shorturl.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        indexes = {
                @Index(name = "url_count_index", columnList = "count DESC")
        }
)
public class Url {
    @Id
    @Column(unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(length = 2000)
    private String original;

    private Integer rank;

    private Integer count = 0;

    public Url(String original) {
        this.original = original;
    }

    public Url(String original,Integer count){
        this.original = original;
        this.count = count;
    }

    public Url(String original,Integer count, Integer rank){
        this.original = original;
        this.count = count;
        this.rank = rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Url url = (Url) o;
        return original.equals(url.original);
    }

    @Override
    public int hashCode() {
        return Objects.hash(original);
    }
}
