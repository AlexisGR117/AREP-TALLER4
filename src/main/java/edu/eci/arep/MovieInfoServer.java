package edu.eci.arep;



import org.reflections.Reflections;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Application to consult information about movies.
 *
 * @author Jefer Alexis Gonzalez Romero
 * @version 1.0
 * @since 2024-02-12
 */
public class MovieInfoServer {

    private static final MovieInfoServer _instance = new MovieInfoServer();
    protected static HashMap<String, Method> components = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger(MovieInfoServer.class.getName());
    private static String directory = "target/classes/public";
    private static String contentType = "text/html";

    /*
    Private constructor to prevent direct instantiation of the class.
    Enforces the use of the singleton pattern.
     */
    private MovieInfoServer() {
    }

    /**
     * Returns the singleton instance of the MovieInfoServer class.
     *
     * @return The MovieInfoServer instance.
     */
    public static MovieInfoServer getInstance() {
        return _instance;
    }

    /**
     * Start the server and start listening to client requests.
     */
    public static void main(String[] args) throws URISyntaxException, InvocationTargetException, IllegalAccessException {
        Reflections reflections = new Reflections("edu.eci.arep");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Component.class);
        for (Class<?> c : classes) {
            for (Method method : c.getDeclaredMethods()) {
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    components.put(method.getAnnotation(RequestMapping.class).value(), method);
                }
            }
        }
        try (ServerSocket serverSocket = new ServerSocket(35000)) {
            while (true) {
                LOGGER.info("Listo para recibir ...");
                handleClientRequest(serverSocket.accept());
            }
        } catch (IOException e) {
            LOGGER.info("Could not listen on port: 35000.");
            System.exit(1);
        }
    }

    /**
     * Handles a single client request.
     *
     * @param clientSocket The Socket object representing the client connection.
     */
    public static void handleClientRequest(Socket clientSocket) throws URISyntaxException, InvocationTargetException, IllegalAccessException {
        try (OutputStream outputStream = clientSocket.getOutputStream();
             PrintWriter out = new PrintWriter(outputStream, true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String requestLine = in.readLine();
            LOGGER.log(Level.INFO, "Received:{0}", requestLine);
            if (requestLine != null) {
                URI fileUrl = new URI(requestLine.split(" ")[1]);
                String params = fileUrl.getRawQuery();
                String path = fileUrl.getPath();
                LOGGER.log(Level.INFO, "Path: {0}", path);
                String outputLine;
                if (path.startsWith("/component")) {
                    String webUri = path.replace("/component", "");
                    if (components.containsKey(webUri)) {
                        Map<String, String> parameters = parseParams(params);
                        if (components.get(webUri).getParameterCount() == 0) {
                            outputLine = (String) components.get(webUri).invoke(null);
                        } else outputLine = (String) components.get(webUri).invoke(null, parameters);
                        out.println(httpHeader(contentType).append(outputLine));
                    }
                } else if (path.contains(".")) {
                    String contentType = contentType(path);
                    if (contentType.contains("image")) outputStream.write(httpClientImage(path));
                    else out.println(httpClientFiles(path));
                } else out.println(httpClientError());
            }
            clientSocket.close();
        } catch (IOException e) {
            LOGGER.info("Accept failed.");
            System.exit(1);
        }
    }

    /**
     * Retrieves a specified file from the "target/classes/public" directory and constructs an HTTP response.
     *
     * @return A string containing the HTTP response, including headers and file content.
     */
    public static String httpClientError() {
        StringBuilder outputLine = new StringBuilder();
        outputLine.append("HTTP/1.1 404 Not Found\r\n");
        outputLine.append("Content-Type:text/html\r\n\r\n");
        Path file = Paths.get(directory + "/error.html");
        Charset charset = StandardCharsets.UTF_8;
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            String line;
            while ((line = reader.readLine()) != null) outputLine.append(line);
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
        return outputLine.toString();
    }


    /**
     * Parses query parameters from the given query string.
     *
     * @param queryString The query string containing key-value pairs.
     * @return A Map containing the parsed parameters, where keys are parameter names and values are parameter values.
     */
    public static Map<String, String> parseParams(String queryString) {
        if (queryString != null) {
            Map<String, String> params = new HashMap<>();
            for (String param : queryString.split("&")) {
                String[] nameValue = param.split("=");
                params.put(nameValue[0], nameValue[1]);
            }
            return params;
        } else return Collections.emptyMap();
    }

    /**
     * Retrieves a specified file from the "target/classes/public" directory and constructs an HTTP response.
     *
     * @param path The path to the file, including its extension.
     * @return A string containing the HTTP response, including headers and file content.
     */
    public static String httpClientFiles(String path) {
        StringBuilder outputLine = httpHeader(contentType(path));
        Path file = Paths.get(directory + path);
        Charset charset = StandardCharsets.UTF_8;
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            String line;
            while ((line = reader.readLine()) != null) outputLine.append(line);
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
        return outputLine.toString();
    }

    /**
     * Retrieves a specified image from the "target/classes/public" directory and constructs an HTTP response.
     *
     * @param path The path to the file, including its extension.
     * @return A string containing the HTTP response, including headers and file content.
     */
    public static byte[] httpClientImage(String path) {
        Path file = Paths.get(directory + path);
        byte[] imageData = null;
        try {
            imageData = Files.readAllBytes(file);
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
        byte[] headerBytes = httpHeader(contentType(path)).toString().getBytes();
        assert imageData != null;
        int totalLength = headerBytes.length + imageData.length;
        byte[] combinedBytes = new byte[totalLength];
        System.arraycopy(headerBytes, 0, combinedBytes, 0, headerBytes.length);
        System.arraycopy(imageData, 0, combinedBytes, headerBytes.length, imageData.length);
        return combinedBytes;
    }

    /**
     * Constructs the HTTP response header based on the given file extension.
     *
     * @param contentType The content type of the file.
     * @return A StringBuilder containing the HTTP response header.
     */
    public static StringBuilder httpHeader(String contentType) {
        StringBuilder header = new StringBuilder();
        header.append("HTTP/1.1 200 OK\r\n");
        header.append("Content-Type:");
        header.append(contentType);
        header.append("\r\n");
        header.append("\r\n");
        return header;
    }

    /**
     * Determines the content type of file based on its path.
     *
     * @param path The path to the file.
     * @return The content type of the file, or an empty string if the content type could not be determined.
     */
    public static String contentType(String path) {
        File file = new File(path);
        String contentType = "";
        try {
            contentType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
        return contentType;
    }

    /**
     * Sets the directory to serve static files from.
     *
     * @param directoryPath The path to the directory containing static files.
     */
    public static void staticDirectory(String directoryPath) {
        directory = "target/classes/" + directoryPath;
    }

    /**
     * Sets the response type for subsequent web service responses.
     *
     * @param responseType The content type to use for the response, such as "application/json" or "text/html".
     */
    public static void responseType(String responseType) {
        contentType = responseType;
    }

    public static String getDirectory() {
        return directory;
    }

    public static String getContentType() {
        return contentType;
    }
}