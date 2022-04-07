/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author filip
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "title", "publishedDate", "description", "length", "picturePath", "link", "directors", "genres", "actors"})
public class Movie {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @XmlAttribute
    private int id;
    
    private String title;

    @XmlElement(name = "publisheddate")
    @XmlJavaTypeAdapter(PublishedDateAdapter.class)
    private LocalDateTime publishedDate;

    private String description;
    private int length;

    @XmlElement(name = "picturepath")
    private String picturePath;
    private String link;

    @XmlElementWrapper
    @XmlElement(name = "director")
    private Set<Person> directors;

    @XmlElementWrapper
    @XmlElement(name = "genre")
    private Set<Genre> genres;

    @XmlElementWrapper
    @XmlElement(name = "actor")
    private Set<Person> actors;

    public Movie(String title, LocalDateTime publishedDate, String description, int length, String picturePath, String link) {
        this.title = title;
        this.publishedDate = publishedDate;
        this.description = description;
        this.length = length;
        this.picturePath = picturePath;
        this.link = link;
    }

    public Movie(int id, String title, LocalDateTime publishedDate, String description, int length, String picturePath, String link) {
        this(title, publishedDate, description, length, picturePath, link);
        this.id = id;
    }

    public Movie() {
    }

    public Movie(int id, String title, LocalDateTime publishedDate, String description, Set<Person> directors, int length, String picturePath, Set<Genre> genres, Set<Person> actors, String link) {
        this(title, publishedDate, description, directors, length, picturePath, genres, actors, link);
        this.id = id;

    }

    public Movie(String title, LocalDateTime publishedDate, String description, Set<Person> directors, int length, String picturePath, Set<Genre> genres, Set<Person> actors, String link) {
        this.title = title;
        this.publishedDate = publishedDate;
        this.description = description;
        this.directors = directors;
        this.length = length;
        this.picturePath = picturePath;
        this.genres = genres;
        this.actors = actors;
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(LocalDateTime publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Person> getDirectors() {
        return directors;
    }

    public void setDirectors(Set<Person> directors) {
        this.directors = directors;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public Set<Person> getActors() {
        return actors;
    }

    public void setActors(Set<Person> actors) {
        this.actors = actors;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return id + " - " + title;
    }
}
