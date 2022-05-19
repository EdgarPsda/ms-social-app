package com.in28minutes.rest.webservices.restfulwebservices.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
public class UserJPAResource {

    @Autowired
    UserDaoService usersDao;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    // Get all users
    @GetMapping(path = "/jpa/users")
    public List<User> getUsers(){
        if(usersDao.findAll() == null){
           throw new UserNotFoundException("No users in DB.");
        }
        return userRepository.findAll();
    }

    // Save user
    @PostMapping(path = "/jpa/users")
    public ResponseEntity<Object> saveUser(@Valid @RequestBody User user){
        if(user.getName() == null){
            throw new UserValidationException("User name is required.");
        }
        User savedUser = userRepository.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    // Get user by ID
    @GetMapping(path = "/jpa/users/{id}")
    public EntityModel<User> findUserById(@PathVariable int id){
        Optional<User> user = userRepository.findById(id);
        if(!user.isPresent()){
            throw new UserNotFoundException("User with id: " + id + " Not found.");
        }
        EntityModel<User> resource = EntityModel.of(user.get());
        WebMvcLinkBuilder linkToUsers = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getUsers());
        resource.add(linkToUsers.withRel("all-users"));
        return resource;
    }

//    Delete user by ID
    @DeleteMapping(path = "/jpa/users/{id}")
    public void deleteUserById(@PathVariable int id){
        userRepository.deleteById(id);
    }

    @GetMapping(path = "/jpa/users/{id}/posts")
    public List<Post> retrieveAllPosts(@PathVariable int id){
        Optional<User> userOptional = userRepository.findById(id);
        if(!userOptional.isPresent()){
            throw new UserNotFoundException("id- " + id + " Not found.");
        }

        return userOptional.get().getPosts();
    }

    @PostMapping(path = "/jpa/users/{id}/posts")
    public ResponseEntity<Object> createPost(@PathVariable int id, @RequestBody Post post){
        Optional<User> userOptional = userRepository.findById(id);
        if(!userOptional.isPresent()){
            throw new UserNotFoundException("id - " + id + " Not found.");
        }

        User user = userOptional.get();
        post.setUser(user);
        postRepository.save(post);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(post.getId()).toUri();
        return ResponseEntity.created(location).build();

    }

}
