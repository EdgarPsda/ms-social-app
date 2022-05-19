package com.in28minutes.rest.webservices.restfulwebservices.user;

import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

@RestController
public class UserResource {

    @Autowired
    UserDaoService usersDao;

    // Get all users
    @GetMapping(path = "/users")
    public List<User> getUsers(){
        if(usersDao.findAll() == null){
           throw new UserNotFoundException("No users in DB.");
        }
        return usersDao.findAll();
    }

    // Save user
    @PostMapping(path = "/users")
    public ResponseEntity<Object> saveUser(@Valid @RequestBody User user){
        if(user.getName() == null){
            throw new UserValidationException("User name is required.");
        }
        User savedUser = usersDao.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    // Get user by ID
    @GetMapping(path = "/users/{id}")
    public EntityModel<User> findUserById(@PathVariable int id){
        User user = usersDao.findOne(id);
        if(user == null){
            throw new UserNotFoundException("User with id: " + id + " Not found.");
        }
        EntityModel<User> model = EntityModel.of(user);
        WebMvcLinkBuilder linkToUsers = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getUsers());
        model.add(linkToUsers.withRel("all-users"));
        return model;
    }

//    Delete user by ID
    @DeleteMapping(path = "/users/{id}")
    public void deleteUserById(@PathVariable int id){
        User user = usersDao.deleteUser(id);
        if(user == null){
            throw new UserNotFoundException("User with id: " + id + " Not found.");
        }
    }

}
