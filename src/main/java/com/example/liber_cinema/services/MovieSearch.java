package com.example.liber_cinema.services;

import com.example.liber_cinema.models.Movie;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieSearch {
    private static final String API_KEY = System.getenv("API_KEY") != null ? System.getenv("API_KEY") : "cf3784fe";
    private static final String BASE_URL = "http://www.omdbapi.com/";public Movie searchMovie(String title) {
        try {
            String url = BASE_URL + "?apikey=" + API_KEY + "&t=" + title.replace(" ", "+");
            String response = sendRequest(url);
            return convertJsonToMovie(response);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private Movie convertJsonToMovie(String json) {
        Gson gson = new Gson();
        JsonObject movieData = gson.fromJson(json, JsonObject.class);
        
        // Check if the response has valid movie data
        if (movieData == null || movieData.has("Error") || !movieData.has("Title")) {
            return null;
        }
        
        Movie movie = new Movie();
        movie.setTitle(getStringValue(movieData, "Title"));
        movie.setDescription(getStringValue(movieData, "Plot"));
        movie.setGenre(getStringValue(movieData, "Genre"));
        movie.setDirector(getStringValue(movieData, "Director"));
        movie.setReleaseDate(getStringValue(movieData, "Released"));
        movie.setDuration(getStringValue(movieData, "Runtime"));
        
        // Convert IMDB rating to double
        String imdbRating = getStringValue(movieData, "imdbRating");
        if (imdbRating != null && !imdbRating.equals("N/A")) {
            try {
                movie.setImdbRating(Double.parseDouble(imdbRating));
            } catch (NumberFormatException e) {
                movie.setImdbRating(0.0);
            }
        }
        
        return movie;
    }
    
    private String getStringValue(JsonObject jsonObject, String key) {
        JsonElement element = jsonObject.get(key);
        if (element != null && !element.isJsonNull() && !element.getAsString().equals("N/A")) {
            return element.getAsString();
        }
        return null;
    }    // Wyszukiwanie listy filmów według gatunku
    public List<Movie> searchMoviesByGenre(String genre) {
        try {
            String url = BASE_URL + "?apikey=" + API_KEY + "&type=movie&s=" + genre.replace(" ", "+");
            String response = sendRequest(url);

            return parseMoviesByGenre(response, genre);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }    private String sendRequest(String url) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }    private List<Movie> parseMoviesByGenre(String response, String genre) {
        Gson gson = new Gson();
        JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
        List<Movie> movieList = new ArrayList<>();

        if (!jsonResponse.has("Search")) {
            return movieList;
        }

        JsonArray searchResults = jsonResponse.getAsJsonArray("Search");

        for (var element : searchResults) {
            JsonObject movieJson = element.getAsJsonObject();

            String movieDetails = fetchMovieDetails(movieJson.get("imdbID").getAsString());
            if (movieDetails == null) continue;

            JsonObject movieDetailsJson = gson.fromJson(movieDetails, JsonObject.class);
            if (!movieDetailsJson.has("Genre")) continue;

            String genres = movieDetailsJson.get("Genre").getAsString();
            if (genres.toLowerCase().contains(genre.toLowerCase())) {
                Movie movie = convertJsonToMovie(movieDetails);
                if (movie != null) {
                    movieList.add(movie);
                }
            }
        }

        return movieList;
    }

    private String fetchMovieDetails(String imdbID) {
        try {
            String url = BASE_URL + "?apikey=" + API_KEY + "&i=" + imdbID;
            return sendRequest(url);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
