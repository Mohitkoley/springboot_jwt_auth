package com.JWT.example.dummy.service;

import java.util.ArrayList;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.JWT.example.dummy.model.User;

@Service
public class UserService {
	
	private List<User> store = new ArrayList<>();
	
	private String getID() {
        return UUID.randomUUID().toString();
    }
	
	public UserService() {
		store.add(new User( getID(), "Rockey bhai", "rhser6049@gmail.com"));
		store.add(new User( getID(), "user second", "abc@dev.com"));
		store.add(new User( getID(), "user third", "no@mail.com"));
		store.add(new User( getID(), "user fourth", "some@gmail.com"));
		store.add(new User( getID(), "user fifth", ""));
		
	}
	
	
	public List<User> getUsers() {
		return store;
	}
}
