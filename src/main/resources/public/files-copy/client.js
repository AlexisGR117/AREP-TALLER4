async function fetchMovieData(movieTitle) {
    try {
        const response = await fetch(`/component/movies?title=${movieTitle}`, {
            responseType: 'json'
        });
        const movieData = await response.json();
        return movieData;
    } catch (error) {
        document.getElementById("title").innerHTML = "There was an error, the query could not be performed";
        document.getElementById("movie-information").style.display = "none";
        return null;
    }
}

function displayMovieInformation(movie) {
    if (movie.Response == "True") {
      displayGeneralInformation(movie);
      displayGenres(movie);
      displayCredits(movie);
      displayPlotAndDetails(movie);
      document.getElementById("movie-information").style.display = "block";
    } else {
      document.getElementById("title").innerHTML = movie.Error;
      document.getElementById("movie-information").style.display = "none";
    }
}

function displayGeneralInformation(movie) {
    document.getElementById("title").innerHTML = `${movie.Title} (${movie.Year})`;
    document.getElementById("poster").src = movie.Poster;
    document.getElementById("rated").innerHTML = `<b>Rated: </b>${movie.Rated}`;
    document.getElementById("released").innerHTML = `<b>Released: </b>${movie.Released}`;
}

function displayGenres(movie) {
    const genresDiv = document.getElementById("genres");
    genresDiv.innerHTML = "";
    movie.Genre.split(", ").forEach((genre) => {
      genresDiv.appendChild(document.createElement("span")).textContent = genre;
    });
}

function displayCredits(movie) {
    document.getElementById("director").innerHTML = `<b>Director: </b>${movie.Director}`;
    document.getElementById("writer").innerHTML = `<b>Writer: </b>${movie.Writer}`;
    document.getElementById("actors").innerHTML = `<b>Actors: </b>${movie.Actors}`;
}

function displayPlotAndDetails(movie) {
    document.getElementById("plot").innerHTML = movie.Plot;
    document.getElementById("language").innerHTML = `<b>Language: </b>${movie.Language}`;
    document.getElementById("country").innerHTML = `<b>Country: </b>${movie.Country}`;
    document.getElementById("awards").innerHTML = `<b>Awards: </b>${movie.Awards}`;
    displayRatings(movie.Ratings);
    document.getElementById("metascore").innerHTML = `<b>Metascore: </b>${movie.Metascore}`;
    document.getElementById("imdb-rating").innerHTML = `<b>IMDB Rating: </b>${movie.imdbRating}`;
    document.getElementById("imdb-votes").innerHTML = `<b>IMDB Votes: </b>${movie.imdbVotes}`;
    document.getElementById("imdb-id").innerHTML = `<b>IMDB ID: </b>${movie.imdbID}`;
    document.getElementById("type").innerHTML = `<b>Type: </b>${movie.Type}`;
    document.getElementById("dvd").innerHTML = `<b>DVD: </b>${movie.DVD}`;
    document.getElementById("box-office").innerHTML = `<b>Box Office: </b>${movie.BoxOffice}`;
    document.getElementById("production").innerHTML = `<b>Production: </b>${movie.Production}`;
    document.getElementById("website").innerHTML = `<b>Website: </b>${movie.Website}`;
}

function displayRatings(ratings) {
    const ratingsLi = document.getElementById("ratings");
    ratingsLi.innerHTML = "<b>Ratings: </b>";
    ratings.forEach((rating) => {
      ratingsLi.insertAdjacentText("beforeend", "[" + rating.Source + " - " + rating.Value + "] ");
    });
}

async function loadGetMsg() {
    const movieTitle = document.getElementById("movie-title").value;
    const movieData = await fetchMovieData(movieTitle);
    displayMovieInformation(movieData);
}