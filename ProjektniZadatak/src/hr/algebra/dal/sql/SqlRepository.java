/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.dal.sql;

import hr.algebra.dal.Repository;
import hr.algebra.model.Genre;
import hr.algebra.model.Movie;
import hr.algebra.model.Person;
import hr.algebra.model.User;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import javax.sql.DataSource;

/**
 *
 * @author filip
 */
public class SqlRepository implements Repository {

    //Variables
    private static final String EMAIL = "Email";
    private static final String PASSWORD = "PasswordHash";
    private static final String IS_ADMINISTRATOR = "IsAdministrator";

    private static final String ID_GENRE = "IDGenre";
    private static final String GENRE_NAME = "GenreName";

    private static final String ID_MOVIE = "IDMovie";
    private static final String TITLE = "Title";
    private static final String PUBLISHED_DATE = "PublishedDate";
    private static final String MOVIE_DESCRIPTION = "MovieDescription";
    private static final String MOVIE_LENGTH = "MovieLength";
    private static final String PICTURE_PATH = "PicturePath";
    private static final String LINK = "Link";

    private static final String ID_PERSON = "IDPerson";
    private static final String FIRST_NAME = "FirstName";
    private static final String LAST_NAME = "LastName";

    //Procedures
    private static final String CREATE_USER = "{ CALL createUser (?,?,?,?) }";
    private static final String CHECK_USER = "{ CALL checkUser (?,?) }";

    private static final String CREATE_MOVIE = "{ CALL createMovie (?,?,?,?,?,?,?) }";
    private static final String SELECT_MOVIES = "{ CALL selectMovies }";
    private static final String SELECT_MOVIE = "{ CALL selectMovie (?) }";
    private static final String DELETE_MOVIE = "{ CALL deleteMovie (?) }";
    private static final String UPDATE_MOVIE = "{ CALL updateMovie (?,?,?,?,?,?,?) }";
    private static final String DELETE_ACTORS_GENRES_AND_DIRECTORS_FOR_MOVIE = "{ CALL deleteActorsGenresAndDirectorsForMovie (?) }";

    private static final String CREATE_GENRE_FOR_MOVIE = "{ CALL createGenreForMovie (?,?)}";
    private static final String SELECT_GENRES_FOR_MOVIE = "{ CALL selectGenresForMovie (?)}";
    private static final String CREATE_OR_GET_GENRE_ID = "{ CALL createOrGetGenreID (?,?) }";
    private static final String SELECT_GENRE = "{ CALL selectGenre (?) }";
    private static final String SELECT_ALL_GENRES = "{ CALL selectAllGenres }";

    private static final String CREATE_ACTOR_FOR_MOVIE = "{ CALL createActorForMovie (?,?)}";
    private static final String SELECT_ACTORS_FOR_MOVIE = "{ CALL selectActorsForMovie (?)}";
    //private static final String SELECT_ACTOR = "{ CALL selectActor (?) }";

    private static final String CREATE_DIRECTOR_FOR_MOVIE = "{ CALL createDirectorForMovie (?,?)}";
    private static final String SELECT_DIRECTORS_FOR_MOVIE = "{ CALL selectDirectorsForMovie (?)}";
    //private static final String SELECT_DIRECTOR = "{ CALL selectDirector (?) }";

    private static final String CREATE_OR_GET_PERSON_ID = "{ CALL createOrGetPersonID (?,?,?) }";
    private static final String CREATE_OR_GET_PERSON_ID_BOOL = "{ CALL createOrGetPersonIDBool (?,?,?) }";
    private static final String UPDATE_PERSON = "{ CALL updatePerson (?,?,?) }";
    private static final String DELETE_PERSON = "{ CALL deletePerson (?) }";
    private static final String SELECT_PERSON = "{ CALL selectPerson (?) }";
    private static final String SELECT_ALL_PERSONS = "{ CALL selectAllPersons }";

    private static final String DELETE_EVERYTHING = "{ CALL deleteEverything }";

    @Override
    public int createUser(User user) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_USER)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setInt(3, user.getIsAdmin() ? 1 : 0);

            stmt.registerOutParameter(4, Types.INTEGER);

            stmt.executeUpdate();
            return stmt.getInt(4);
        }

    }

    @Override
    public Optional<User> checkUser(User user) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CHECK_USER)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(new User(
                            rs.getString(EMAIL),
                            rs.getString(PASSWORD),
                            rs.getBoolean(IS_ADMINISTRATOR)));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void createMovies(List<Movie> movies) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();

        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_MOVIE)) {

            for (Movie movie : movies) {

                stmt.setString(1, movie.getTitle());
                stmt.setString(2, movie.getPublishedDate().format(Movie.DATE_FORMATTER));
                stmt.setString(3, movie.getDescription());
                stmt.setInt(4, movie.getLength());
                stmt.setString(5, movie.getPicturePath());
                stmt.setString(6, movie.getLink());

                stmt.registerOutParameter(7, Types.INTEGER); //ID

                stmt.executeUpdate();

                try {
                    //Dodavanje genreova
                    if (!movie.getGenres().isEmpty()) {
                        for (Genre genre : movie.getGenres()) {
                            int genreID = createOrGetGenreID(genre);
                            int movieID = stmt.getInt(7);

                            createGenreForMovie(movieID, genreID);

                        }
                    }

                    //Dodavanje glumaca
                    if (!movie.getActors().isEmpty()) {

                        for (Person actor : movie.getActors()) {
                            int personID = createOrGetPersonID(actor);
                            int movieID = stmt.getInt(7);

                            createActorForMovie(movieID, personID);

                        }
                    }

                    //Dodavanje direktora
                    if (!movie.getDirectors().isEmpty()) {

                        for (Person director : movie.getDirectors()) {
                            int personID = createOrGetPersonID(director);
                            int movieID = stmt.getInt(7);

                            createDirectorForMovie(movieID, personID);

                        }
                    }
                } catch (Exception exception) {
                    System.out.println("Movie " + movie.getTitle() + " doesn't have actors, directors or genre!");
                }

            }

        }
    }

    @Override
    public int createMovie(Movie movie) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();

        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_MOVIE)) {

            stmt.setString(1, movie.getTitle());
            stmt.setString(2, movie.getPublishedDate().format(Movie.DATE_FORMATTER));
            stmt.setString(3, movie.getDescription());
            stmt.setInt(4, movie.getLength());
            stmt.setString(5, movie.getPicturePath());
            stmt.setString(6, movie.getLink());

            stmt.registerOutParameter(7, Types.INTEGER); //ID

            stmt.executeUpdate();

            try {
                //Dodavanje genreova
                if (!movie.getGenres().isEmpty()) {
                    for (Genre genre : movie.getGenres()) {
                        int genreID = createOrGetGenreID(genre);
                        int movieID = stmt.getInt(7);

                        createGenreForMovie(movieID, genreID);

                    }
                }

                //Dodavanje glumaca
                if (!movie.getActors().isEmpty()) {

                    for (Person actor : movie.getActors()) {
                        int personID = createOrGetPersonID(actor);
                        int movieID = stmt.getInt(7);

                        createActorForMovie(movieID, personID);

                    }
                }

                //Dodavanje direktora
                if (!movie.getDirectors().isEmpty()) {

                    for (Person director : movie.getDirectors()) {
                        int personID = createOrGetPersonID(director);
                        int movieID = stmt.getInt(7);

                        createDirectorForMovie(movieID, personID);

                    }
                }
            } catch (Exception exception) {
                System.out.println("Movie " + movie.getTitle() + " doesn't have actors, directors or genre!");
            }

            return stmt.getInt(7);
        }
    }

    @Override
    public List<Movie> selectMovies() throws Exception {
        List<Movie> movies = new ArrayList<>();
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_MOVIES);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Set<Person> actors = selectActorsForMovie(rs.getInt(ID_MOVIE));
                Set<Genre> genres = selectGenresForMovie(rs.getInt(ID_MOVIE));
                Set<Person> directors = selectDirectorsForMovie(rs.getInt(ID_MOVIE));

                movies.add(new Movie(
                        rs.getInt(ID_MOVIE),
                        rs.getString(TITLE),
                        LocalDateTime.parse(rs.getString(PUBLISHED_DATE), Movie.DATE_FORMATTER),
                        rs.getString(MOVIE_DESCRIPTION),
                        directors,
                        rs.getInt(MOVIE_LENGTH),
                        rs.getString(PICTURE_PATH),
                        genres,
                        actors,
                        rs.getString(LINK)));

            }
        }
        return movies;
    }

    @Override
    public Optional<Movie> selectMovie(int id) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_MOVIE)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    Set<Person> actors = selectActorsForMovie(rs.getInt(ID_MOVIE));
                    Set<Genre> genres = selectGenresForMovie(rs.getInt(ID_MOVIE));
                    Set<Person> directors = selectDirectorsForMovie(rs.getInt(ID_MOVIE));

                    return Optional.of(new Movie(
                            rs.getInt(ID_MOVIE),
                            rs.getString(TITLE),
                            LocalDateTime.parse(rs.getString(PUBLISHED_DATE), Movie.DATE_FORMATTER),
                            rs.getString(MOVIE_DESCRIPTION),
                            directors,
                            rs.getInt(MOVIE_LENGTH),
                            rs.getString(PICTURE_PATH),
                            genres,
                            actors,
                            rs.getString(LINK)));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void deleteMovie(int id) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(DELETE_MOVIE)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();
        }
    }

    @Override
    public void updateMovie(int id, Movie data) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(UPDATE_MOVIE)) {

            deleteActorsGenresAndDirectorsForMovie(id);

            stmt.setString(1, data.getTitle());
            stmt.setString(2, data.getPublishedDate().format(Movie.DATE_FORMATTER));
            stmt.setString(3, data.getDescription());
            stmt.setInt(4, data.getLength());
            stmt.setString(5, data.getPicturePath());
            stmt.setString(6, data.getLink());

            stmt.setInt(7, id);

            stmt.executeUpdate();

            try {
                //Dodavanje genreova
                if (!data.getGenres().isEmpty()) {
                    for (Genre genre : data.getGenres()) {
                        int genreID = createOrGetGenreID(genre);
                        int movieID = id;

                        createGenreForMovie(movieID, genreID);

                    }
                }

                //Dodavanje glumaca
                if (!data.getActors().isEmpty()) {

                    for (Person actor : data.getActors()) {
                        int personID = createOrGetPersonID(actor);
                        int movieID = id;

                        createActorForMovie(movieID, personID);

                    }
                }

                //Dodavanje direktora
                if (!data.getDirectors().isEmpty()) {

                    for (Person director : data.getDirectors()) {
                        int personID = createOrGetPersonID(director);
                        int movieID = id;

                        createDirectorForMovie(movieID, personID);

                    }
                }
            } catch (Exception exception) {
                System.out.println("Movie " + data.getTitle() + " doesn't have actors, directors or genre!");
            }
        }

    }

    @Override
    public void deleteActorsGenresAndDirectorsForMovie(int id) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(DELETE_ACTORS_GENRES_AND_DIRECTORS_FOR_MOVIE)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();
        }
    }

    @Override
    public int createOrGetGenreID(Genre genre) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_OR_GET_GENRE_ID)) {

            stmt.setString(1, genre.getGenre());

            stmt.registerOutParameter(2, Types.INTEGER);

            stmt.executeUpdate();

            return stmt.getInt(2);

        }
    }

    @Override
    public Optional<Genre> selectGenre(int id) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();

        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_GENRE)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(new Genre(
                            rs.getInt(ID_GENRE),
                            rs.getString(GENRE_NAME)));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public void createGenreForMovie(int MovieID, int GenreID) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();

        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_GENRE_FOR_MOVIE)) {

            stmt.setInt(1, MovieID);
            stmt.setInt(2, GenreID);

            stmt.executeUpdate();
        }
    }

    @Override
    public Set<Genre> selectGenresForMovie(int MovieID) throws Exception {

        Set<Genre> genres = new TreeSet<>();

        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_GENRES_FOR_MOVIE)) {

            stmt.setInt(1, MovieID);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    genres.add(new Genre(
                            rs.getInt(ID_GENRE),
                            rs.getString(GENRE_NAME)));
                }
            }
        }
        return genres;
    }

    @Override
    public Set<Genre> selectAllGenres() throws Exception {
        Set<Genre> genres = new TreeSet<>();

        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_ALL_GENRES)) {

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    genres.add(new Genre(
                            rs.getInt(ID_GENRE),
                            rs.getString(GENRE_NAME)));

                }
            }
        }
        return genres;
    }

    @Override
    public Set<Person> selectAllPersons() throws Exception {
        Set<Person> persons = new TreeSet<>();

        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_ALL_PERSONS)) {

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    persons.add(new Person(
                            rs.getInt(ID_PERSON),
                            rs.getString(FIRST_NAME),
                            rs.getString(LAST_NAME)));
                }
            }
        }
        return persons;
    }

    /* @Override
    public Optional<Person> selectActor(int id) throws Exception {
    DataSource dataSource = DataSourceSingleton.getInstance();
    
    try (Connection con = dataSource.getConnection();
    CallableStatement stmt = con.prepareCall(SELECT_ACTOR)) {
    
    stmt.setInt(1, id);
    
    try (ResultSet rs = stmt.executeQuery()) {
    
    if (rs.next()) {
    return Optional.of(new Person(
    rs.getInt(ID_PERSON),
    rs.getString(FIRST_NAME),
    rs.getString(LAST_NAME)));
    }
    }
    }
    
    return Optional.empty();
    }*/

    /*@Override
    public Optional<Person> selectDirector(int id) throws Exception {
    DataSource dataSource = DataSourceSingleton.getInstance();
    
    try (Connection con = dataSource.getConnection();
    CallableStatement stmt = con.prepareCall(SELECT_DIRECTOR)) {
    
    stmt.setInt(1, id);
    
    try (ResultSet rs = stmt.executeQuery()) {
    
    if (rs.next()) {
    return Optional.of(new Person(
    rs.getInt(ID_PERSON),
    rs.getString(FIRST_NAME),
    rs.getString(LAST_NAME)));
    }
    }
    }
    
    return Optional.empty();
    }*/

    @Override
    public Optional<Person> selectPerson(int id) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();

        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_PERSON)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(new Person(
                            rs.getInt(ID_PERSON),
                            rs.getString(FIRST_NAME),
                            rs.getString(LAST_NAME)));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public void createActorForMovie(int MovieID, int ActorID) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();

        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_ACTOR_FOR_MOVIE)) {

            stmt.setInt(1, MovieID);
            stmt.setInt(2, ActorID);

            stmt.executeUpdate();
        }
    }

    @Override
    public void createDirectorForMovie(int MovieID, int DirectorID) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();

        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_DIRECTOR_FOR_MOVIE)) {

            stmt.setInt(1, MovieID);
            stmt.setInt(2, DirectorID);

            stmt.executeUpdate();
        }
    }

    @Override
    public int createOrGetPersonID(Person person) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_OR_GET_PERSON_ID)) {

            stmt.setString(1, person.getFirstName());
            stmt.setString(2, person.getLastName());

            stmt.registerOutParameter(3, Types.INTEGER);

            stmt.executeUpdate();

            return stmt.getInt(3);

        }

    }

    @Override
    public boolean createOrGetPersonIDBool(Person person) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_OR_GET_PERSON_ID_BOOL)) {

            stmt.setString(1, person.getFirstName());
            stmt.setString(2, person.getLastName());

            stmt.registerOutParameter(3, Types.INTEGER);

            stmt.executeUpdate();

            return stmt.getInt(3) == 1;

        }
    }

    @Override
    public Set<Person> selectActorsForMovie(int MovieID) throws Exception {
        Set<Person> persons = new TreeSet<>();

        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_ACTORS_FOR_MOVIE)) {

            stmt.setInt(1, MovieID);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    persons.add(new Person(
                            rs.getInt(ID_PERSON),
                            rs.getString(FIRST_NAME),
                            rs.getString(LAST_NAME)));
                }
            }
        }
        return persons;
    }

    @Override
    public Set<Person> selectDirectorsForMovie(int MovieID) throws Exception {
        Set<Person> persons = new TreeSet<>();

        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_DIRECTORS_FOR_MOVIE)) {

            stmt.setInt(1, MovieID);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    persons.add(new Person(
                            rs.getInt(ID_PERSON),
                            rs.getString(FIRST_NAME),
                            rs.getString(LAST_NAME)));
                }
            }
        }
        return persons;
    }
    
        @Override
    public void updatePerson(int id, Person person) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(UPDATE_PERSON)) {

            stmt.setString(1, person.getFirstName());
            stmt.setString(2, person.getLastName());

            stmt.setInt(3, id);

            stmt.executeUpdate();
        }
    }

    @Override
    public void deletePerson(int id) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(DELETE_PERSON)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteEverything() throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();

        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(DELETE_EVERYTHING)) {

            for (Movie movie : selectMovies()) {
                if (movie.getPicturePath() != null) {
                    Files.delete(Paths.get(movie.getPicturePath()));
                }
            }

            stmt.executeUpdate();
        }

    }



}
