package com.sg.moviecatalogservice.services;


import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.sg.moviecatalogservice.models.CatalogItem;
import com.sg.moviecatalogservice.models.Movie;
import com.sg.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieInfo {

    private RestTemplate restTemplate;

    @Autowired
    public MovieInfo(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @HystrixCommand(fallbackMethod = "getFallbackMethod")
    public List<CatalogItem> getCatalogItems(UserRating userRating) {
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




    public List<CatalogItem> getFallbackMethod(UserRating userRating) {
        return Arrays.asList(new CatalogItem("No Movie Info","",0));
    }
}
