/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.parsers;

import hr.algebra.factory.ParserFactory;
import hr.algebra.factory.UrlConnectionFactory;
import hr.algebra.model.Genre;
import hr.algebra.model.Movie;
import hr.algebra.model.Person;
import hr.algebra.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author filip
 */
public class MovieParser {

    private static final String RSS_URL = "https://www.blitz-cinestar.hr/rss.aspx?najava=1";

    private static final String TAG_REGEX = "<[^>]*>";
    private static final String DESCRIPTION_END_WORD = "Početak prikazivanja";

    private static final String EXT = ".jpg";
    private static final String DIR = "assets";

    private static int counter = 0;

    private MovieParser() {
    }

    public static List<Movie> parse() throws IOException, XMLStreamException {
        List<Movie> movies = new ArrayList<>();

        HttpURLConnection con = UrlConnectionFactory.getHttpUrlConnection(RSS_URL);

        try (InputStream is = con.getInputStream()) {
            XMLEventReader reader = ParserFactory.createStaxParser(is);

            Optional<TagType> tagType = Optional.empty();
            Movie movie = null;
            StartElement startElement = null;

            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();

                switch (event.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT:
                        startElement = event.asStartElement();
                        String qName = startElement.getName().getLocalPart();
                        tagType = TagType.from(qName);
                        break;

                    case XMLStreamConstants.CHARACTERS:
                        if (tagType.isPresent()) {
                            Characters characters = event.asCharacters();
                            String data = characters.getData().trim();

                            switch (tagType.get()) {
                                case ITEM:
                                    movie = new Movie();
                                    movies.add(movie);
                                    System.out.println(++counter + ". movie added");
                                    break;

                                case TITLE:
                                    if (movie != null && !data.isEmpty()) {
                                        movie.setTitle(data);
                                    }
                                    break;

                                case PUB_DATE:
                                    if (movie != null && !data.isEmpty()) {
                                        LocalDateTime publishedDate = null;
                                        try {
                                            publishedDate = LocalDateTime.parse(data, DateTimeFormatter.RFC_1123_DATE_TIME);
                                        } catch (Exception e) {
                                            publishedDate = LocalDateTime.now();
                                            System.out.println("Error parsing date of movie - " + movie.getTitle());
                                        }
                                        movie.setPublishedDate(publishedDate);
                                    }
                                    break;

                                case DESCRIPTION:
                                    if (movie != null && !data.isEmpty()) {
                                        movie.setDescription(data.substring(0, data.indexOf(DESCRIPTION_END_WORD)).replaceAll(TAG_REGEX, ""));
                                    }
                                    break;

                                case LENGTH:
                                    if (movie != null && !data.isEmpty()) {
                                        try {
                                            movie.setLength(Integer.valueOf(data));
                                        } catch (NumberFormatException numberFormatException) {
                                            movie.setLength(0);
                                        }
                                    }
                                    break;

                                case LINK:
                                    if (movie != null && !data.isEmpty()) {
                                        movie.setLink(data);
                                    }
                                    break;

                                case POSTER:
                                    if (movie != null && startElement != null && movie.getPicturePath() == null) {
                                        handlePicture(movie, data);
                                    }

                                    break;

                                case DIRECTOR:
                                    if (movie != null && !data.isEmpty()) {
                                        movie.setDirectors(Person.getPersonsFromData((String) data));
                                    }
                                    break;

                                case ACTORS:
                                    if (movie != null && !data.isEmpty()) {
                                        movie.setActors(Person.getPersonsFromData(data));
                                    }
                                    break;

                                case GENRE:
                                    if (movie != null && !data.isEmpty()) {
                                        movie.setGenres(Genre.getGenresFromData(data));
                                    }
                                    break;
                            }
                        }

                        break;
                }

            }
        }

        return movies;
    }

    //https://www.blitz-cinestar.hr/UserDocsImages/Plakats/22.6.2021_16_41_47_chaos_rs.jpg
    private static void handlePicture(Movie movie, String url) {
        try {
            String ext = url.substring(url.lastIndexOf("."));

            if (ext.length() > 5) {
                ext = EXT;
            }

            String pictureName = UUID.randomUUID() + ext;
            String path = DIR + File.separator + pictureName;

            FileUtils.copyFromUrl(url, path);

            movie.setPicturePath(path);
        } catch (IOException ex) {

            System.out.println("Error while handling picture in handlePicture method!");
            movie.setPicturePath("");
        }
    }

    private enum TagType {

        ITEM("item"),
        TITLE("title"),
        PUB_DATE("pubDate"),
        DESCRIPTION("description"),
        DIRECTOR("redatelj"),
        ACTORS("glumci"),
        LENGTH("trajanje"),
        GENRE("zanr"),
        POSTER("plakat"),
        LINK("link");

        private final String name;

        private TagType(String name) {
            this.name = name;
        }

        private static Optional<TagType> from(String name) {
            for (TagType value : values()) {
                if (value.name.equals(name)) {
                    return Optional.of(value);
                }
            }
            return Optional.empty();
        }
    }

}
