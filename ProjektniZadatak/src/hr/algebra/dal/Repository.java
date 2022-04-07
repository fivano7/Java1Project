/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.dal;

import hr.algebra.model.Genre;
import hr.algebra.model.Movie;
import hr.algebra.model.Person;
import hr.algebra.model.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author filip
 */
public interface Repository {

    //User
    int createUser(User user) throws Exception;
    Optional<User> checkUser(User user) throws Exception;

    //Movie
    public void createMovies(List<Movie> movies) throws Exception;
    int createMovie(Movie movie) throws Exception;
    public List<Movie> selectMovies() throws Exception;
    Optional<Movie> selectMovie(int id) throws Exception;
    public void deleteMovie(int id) throws Exception;
    public void updateMovie(int id, Movie movie) throws Exception;
    public void deleteActorsGenresAndDirectorsForMovie(int id) throws Exception;

    //Genre
    int createOrGetGenreID(Genre genre) throws Exception;
    Optional<Genre> selectGenre(int id) throws Exception;
    void createGenreForMovie(int MovieID, int GenreID) throws Exception;
    Set<Genre> selectGenresForMovie (int MovieID) throws Exception;
     Set<Genre> selectAllGenres() throws Exception;
     
    //Person
    Set<Person> selectAllPersons() throws Exception;
    /*    Optional<Person> selectActor(int id) throws Exception;
    Optional<Person> selectDirector(int id) throws Exception;*/
    Optional<Person> selectPerson(int id) throws Exception;
    
    void createActorForMovie(int MovieID, int ActorID) throws Exception;
    void createDirectorForMovie(int MovieID, int DirectorID) throws Exception;
    int createOrGetPersonID(Person person) throws Exception;
    boolean createOrGetPersonIDBool(Person person) throws Exception;
    
    Set<Person> selectActorsForMovie (int MovieID) throws Exception;
    Set<Person> selectDirectorsForMovie (int MovieID) throws Exception;
    
    public void updatePerson(int id, Person person) throws Exception;
    public void deletePerson(int id) throws Exception;

    //Delete
    void deleteEverything() throws Exception;

}
