package edu.eci.arep;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Map;

/**
 * Unit test for simple App.
 */
public class MovieInfoServerTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MovieInfoServerTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( MovieInfoServerTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }


    public void testParseParams() {
        String queryString = "title=Guardians%20Of%20The%20Galaxy&year=2010";
        Map<String, String> params = MovieInfoServer.parseParams(queryString);
        assertEquals(2, params.size());
        assertTrue(params.containsKey("title"));
        assertTrue(params.containsKey("year"));
        assertEquals("Guardians%20Of%20The%20Galaxy", params.get("title"));
        assertEquals("2010", params.get("year"));
    }

    public void testContentType() {
        assertEquals("image/png", MovieInfoServer.contentType("cinema.png"));
        assertEquals("text/html", MovieInfoServer.contentType("client.html"));
        assertEquals("text/css", MovieInfoServer.contentType("client.css"));
        assertEquals("image/x-icon", MovieInfoServer.contentType("favicon.ico"));
        assertEquals("image/jpeg", MovieInfoServer.contentType("cinema.jpg"));
    }

    public void testHttpHeader() {
        String header = "HTTP/1.1 200 OK\r\nContent-Type:image/png\r\n\r\n";
        assertEquals(header, MovieInfoServer.httpHeader("image/png").toString());
    }

    public void testFetchMovieData() {
        MovieDataProvider movieDataProvider = new OMDbMovieDataProvider();
        String movieData = movieDataProvider.fetchMovieData("Guardians%20Of%20The%20Galaxy");
        assertTrue(movieData.contains("{\"Title\":\"Guardians of the Galaxy\",\"Year\":\"2014\""));
    }

    public void testStaticDirectory() {
        MovieInfoServer.staticDirectory("public/test");
        assertEquals("target/classes/public/test", MovieInfoServer.getDirectory());
    }

    public void testResponseType() {
        MovieInfoServer.responseType("application/json");
        assertEquals("application/json", MovieInfoServer.getContentType());
    }
}
