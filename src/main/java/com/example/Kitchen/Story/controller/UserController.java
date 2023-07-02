package com.example.Kitchen.Story.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.example.Kitchen.Story.entity.Users;
import com.example.Kitchen.Story.repository.UserRepo;
import com.example.Kitchen.Story.Security.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
public class UserController {

	@Autowired
	UserRepo ur;

	@PostMapping("user/add")
	@CrossOrigin(origins = "http://localhost:4200/")
	public ResponseEntity<Map<String, Object>> addUser(@RequestBody Map<String, Object> req) {
		System.out.println("Adding user");
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

		String Hpassword = bCryptPasswordEncoder.encode(req.get("password").toString());
		Users user = new Users(req.get("name").toString(), req.get("username").toString(), Hpassword,
				req.get("role").toString(), req.get("email").toString(), req.get("phone").toString());
		Users newUser = ur.save(user);
		System.out.println(newUser);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("message", "User added successfully");
		map.put("user_ID", newUser.getId());
		map.put("status", true);
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	@GetMapping("/user/list")
	public ResponseEntity<Map<String, Object>> listUsers(@RequestHeader("token") String token) {
		Map<String, Object> map = new HashMap<String, Object>();
		System.out.println("Listing users");
		Security s = new Security(ur);
		Users user = s.decodeToken(token);
		if (!user.getRole().equals("Admin")) {
			map.put("status", false);
			map.put("message", "Unauthorized");
			return new ResponseEntity<Map<String, Object>>(map, HttpStatus.UNAUTHORIZED);
		}

		List<Users> users = ur.findAll();
		map.put("data", users);
		map.put("status", true);
		map.put("message", "Data found");
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	@PutMapping("/user/changepassword")
	@CrossOrigin(origins = "http://localhost:4200/")
	public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, Object> req) {
		System.out.println("Changing password");
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		Map<String, Object> map = new HashMap<String, Object>();
		Users u = new Users();
		u.setEmail(req.get("email").toString());
		Example example = Example.of(u);
		Optional<Users> user = ur.findOne(example);
		System.out.println(user);
		Users updatingUser = user.get();
		String Hpassword = bCryptPasswordEncoder.encode(req.get("password").toString());
		updatingUser.setPassword(Hpassword);
		ur.save(updatingUser);

		map.put("status", true);
		map.put("message", "Password updated successfully");
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	@PostMapping("/login")
	@CrossOrigin(origins = "http://localhost:4200/")
	public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> req) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", true);
		Users u = new Users();
		u.setEmail(req.get("email").toString());
		Example example = Example.of(u);
		Optional<Users> user = ur.findOne(example);
		System.out.println(user);
		Users findUser = user.get();
		String enteredPassord = req.get("password").toString();
		String bcryptPassword = findUser.getPassword();

		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		boolean match = bCryptPasswordEncoder.matches(enteredPassord, bcryptPassword);
		if (match) {
			Map<String, Object> claims = new HashMap<>();
			String jwtToken = Jwts.builder().setClaims(claims).setSubject(findUser.getEmail())
					.setIssuedAt(new Date(System.currentTimeMillis())).signWith(SignatureAlgorithm.HS512, "Secret")
					.compact();
			map.put("message", "Login successful");
			map.put("User", findUser);
			map.put("token", jwtToken);
			return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
		} else {
			map.put("message", "Incorrect email/password");
			return new ResponseEntity<Map<String, Object>>(map, HttpStatus.BAD_REQUEST);
		}

	}
}
