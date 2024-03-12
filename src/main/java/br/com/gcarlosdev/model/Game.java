package br.com.gcarlosdev.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    private String id;
    private String title;
    private String genre;
    private int releaseYear;
    private String publisher;
    private double rating;
    private Set<String> platforms;
    private boolean multiplayer;

}
