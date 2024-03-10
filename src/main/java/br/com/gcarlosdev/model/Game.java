package br.com.gcarlosdev.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private List<String> platforms;
    private boolean multiplayer;

}
