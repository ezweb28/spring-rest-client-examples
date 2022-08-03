package guru.springframework.springrestclientexamples.services;

import guru.springframework.api.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class ApiServiceImpl implements ApiService {

    private RestTemplate restTemplate;

    private final String api_url;

    public ApiServiceImpl(RestTemplate restTemplate, @Value("${api.url}") String api_url) {
        this.restTemplate = restTemplate;
        this.api_url = api_url;
    }

    @Override
    public List<User> getUsers(Integer limit) {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(api_url)
                .queryParam("_limit", limit);
        User[] userArray = restTemplate.getForObject(uriBuilder.toUriString(), User[].class);
        return Arrays.asList(Objects.requireNonNull(userArray));

//        User[] userArray = restTemplate.getForObject(api_url + limit, User[].class);
//        return Arrays.asList(Objects.requireNonNull(userArray));
    }

    @Override
    public Flux<User> getUsers(Mono<Integer> limit) {
        return WebClient
                .create(api_url)
                .get()
                .uri(uriBuilder -> uriBuilder.queryParam("_limit", limit.toProcessor().block()).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(resp -> resp.bodyToMono(User[].class))
                .flatMapIterable(Arrays::asList);
    }
}
