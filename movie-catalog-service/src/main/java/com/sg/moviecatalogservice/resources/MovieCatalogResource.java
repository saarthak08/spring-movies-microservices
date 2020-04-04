package com.sg.moviecatalogservice.resources;


import com.sg.moviecatalogservice.models.CatalogItem;
import com.sg.moviecatalogservice.models.Movie;
import com.sg.moviecatalogservice.models.Rating;
import com.sg.moviecatalogservice.models.UserRating;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    private RestTemplate restTemplate;
    private WebClient.Builder webClientBuilder;

    @Autowired
    public MovieCatalogResource(RestTemplate restTemplate, WebClient.Builder builder) {
        this.restTemplate = restTemplate;
        this.webClientBuilder = builder;
    }

    @GetMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        UserRating userRating = restTemplate.getForObject("http://movie-ratings-data-service/ratingsData/users/"+userId, UserRating.class);

        //ParameterizedTypeReference<List<Rating>> ratings;

        return userRating.getUserRatings().stream().map(rating -> {
            Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);

            /*
            Movie movie = webClientBuilder.build().get()
                    .uri("http:localhost:8082/movies/"+rating.getMovieId())
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();
            */

             return new CatalogItem(movie.getName(),"Desc",rating.getRating());
        })
        .collect(Collectors.toList());
    }
}
