package com.sg.moviecatalogservice.resources;


import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.sg.moviecatalogservice.models.CatalogItem;
import com.sg.moviecatalogservice.models.Movie;
import com.sg.moviecatalogservice.models.Rating;
import com.sg.moviecatalogservice.models.UserRating;
import com.sg.moviecatalogservice.services.MovieInfo;
import com.sg.moviecatalogservice.services.UserRatingInfo;
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

    private UserRatingInfo userRatingInfo;

    private MovieInfo movieInfo;

    @Autowired
    public MovieCatalogResource(RestTemplate restTemplate, WebClient.Builder builder, UserRatingInfo userRatingInfo, MovieInfo movieInfo) {
        this.restTemplate = restTemplate;
        this.webClientBuilder = builder;
        this.userRatingInfo = userRatingInfo;
        this.movieInfo = movieInfo;
    }

    @GetMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        UserRating userRating = userRatingInfo.getUserRating(userId);
        //ParameterizedTypeReference<List<Rating>> ratings;

        return movieInfo.getCatalogItems(userRating);
    }
}
