package myfeed.feed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import myfeed.TraversonFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

/**
 * @author Spencer Gibb
 */
@Service
public class UserService {
	private static final ParameterizedTypeReference<PagedResources<Resource<User>>> TYPE_USERS = new ParameterizedTypeReference<PagedResources<Resource<User>>>() {};

	@Autowired
	private RestTemplate rest;

	@Autowired
	private TraversonFactory factory;

	@HystrixCommand(fallbackMethod = "defaultId")
	public String findId(String username) {
		ResponseEntity<User> user = rest.getForEntity("http://myfeed-user/@{username}", User.class, username);
		if (user.getStatusCode().equals(HttpStatus.OK)) {
			return user.getBody().getId();
		}
		return null;
	}

	public String defaultId(String username) {
		return "";
	}

	@HystrixCommand(fallbackMethod = "defaultUsers")
	public List<Resource<User>> getUsers() {
		PagedResources<Resource<User>> users = factory.create("myfeed-user").follow("users").toObject(TYPE_USERS);
		return new ArrayList<>(users.getContent());
	}

	public List<Resource<User>> defaultUsers() {
		return Collections.<Resource<User>>emptyList();
	}

}
