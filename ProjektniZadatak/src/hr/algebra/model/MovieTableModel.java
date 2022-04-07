/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model;

import java.util.Iterator;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author filip
 */
public class MovieTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {"Id", "Title", "Published date", "Description", "Directors", "Length", "Picture path", "Genres", "Actors", "Link"};
    StringBuilder sb = new StringBuilder();

    private List<Movie> movies;

    public MovieTableModel(List<Movie> movies) {
        this.movies = movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return movies.size();
    }

    @Override
    public int getColumnCount() {
        return Movie.class.getDeclaredFields().length - 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return movies.get(rowIndex).getId();
            case 1:
                return movies.get(rowIndex).getTitle();
            case 2:
                return movies.get(rowIndex).getPublishedDate().format(Movie.DATE_FORMATTER);
            case 3:
                return movies.get(rowIndex).getDescription();
            case 4:

                sb.setLength(0);
                Iterator<Person> iteratorDirector = movies.get(rowIndex).getDirectors().iterator();

                while (iteratorDirector.hasNext()) {
                    sb.append(iteratorDirector.next());

                    if (iteratorDirector.hasNext()) {
                        sb.append(", ");
                    }
                }
                return sb.toString();

            case 5:
                return movies.get(rowIndex).getLength();
            case 6:
                return movies.get(rowIndex).getPicturePath();
            case 7:
                sb.setLength(0);
                Iterator<Genre> iteratorGenre = movies.get(rowIndex).getGenres().iterator();

                while (iteratorGenre.hasNext()) {
                    sb.append(iteratorGenre.next());

                    if (iteratorGenre.hasNext()) {
                        sb.append(", ");
                    }
                }
                return sb.toString();
            case 8:

                sb.setLength(0);
                Iterator<Person> iteratorActor = movies.get(rowIndex).getActors().iterator();

                while (iteratorActor.hasNext()) {
                    sb.append(iteratorActor.next());

                    if (iteratorActor.hasNext()) {
                        sb.append(", ");
                    }
                }
                return sb.toString();

            case 9:
                return movies.get(rowIndex).getLink();
            default:
                throw new RuntimeException("No such column");
        }
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: //ID
                return Integer.class;
            case 5: //Length
                return Integer.class;
        }
        return super.getColumnClass(columnIndex);
    }

}
