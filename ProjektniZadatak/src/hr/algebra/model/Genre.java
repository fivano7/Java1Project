/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author filip
 */

@XmlAccessorType(XmlAccessType.FIELD)
public class Genre implements Comparable<Genre>{

    private static final String DEL = ",";

    @XmlAttribute
    private int id;
    
    @XmlElement(name = "genrename")
    private String genre;

    public Genre() {
    }
    

    public Genre(int id, String genre) {
        this.id = id;
        this.genre = genre;
    }

    public int getId() {
        return id;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public static Set<Genre> getGenresFromData(String data) {

        Set genres = new HashSet<>();

        String[] items = data.split(DEL);

        for (String item : items) {
            Genre currentGenre = new Genre();

            currentGenre.genre = item.trim();

            genres.add(currentGenre);
        }

        return genres;

    }

    @Override
    public String toString() {
        return genre;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.genre);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Genre other = (Genre) obj;
        if (!Objects.equals(this.genre, other.genre)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Genre o) {
        return this.genre.toLowerCase().compareTo(o.genre.toLowerCase());
    }

    
    
}
