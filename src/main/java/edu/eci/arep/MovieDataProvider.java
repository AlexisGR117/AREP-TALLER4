package edu.eci.arep;

/**
 * Interface for movie information providers
 *
 * @author Jefer Alexis Gonzalez Romero
 * @version 1.0
 * @since 2024-02-12
 */
public interface MovieDataProvider {

    /**
     * Fetches movie data from the OMDb API using the given title value.
     *
     * @param titleValue The title of the movie to fetch data for.
     * @return The fetched movie data in JSON format, or null if an error occurs.
     */
    String fetchMovieData(String titleValue);

}
