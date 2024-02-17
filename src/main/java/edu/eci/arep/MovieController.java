package edu.eci.arep;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MovieController {

    private static final ConcurrentHashMap<String, String> CACHE = new ConcurrentHashMap<>();
    private static final MovieDataProvider movieDataProvider = new OMDbMovieDataProvider();

    @RequestMapping("/movies")
    public static String movies(Map<String, String> p) {
        MovieInfoServer.responseType("application/json");
        return CACHE.computeIfAbsent(p.get("title"), movieDataProvider::fetchMovieData);
    }
}
