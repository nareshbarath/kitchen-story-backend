package com.example.Kitchen.Story.Security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;

import com.example.Kitchen.Story.entity.Users;
import com.example.Kitchen.Story.repository.UserRepo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class Security {

	UserRepo ur;

	public Security(UserRepo ur) {
		this.ur = ur;
	}

	public Users decodeToken(String token) {
		Claims claims = Jwts.parser().setSigningKey("Secret").parseClaimsJws(token).getBody();
		String tokenValue = claims.getSubject();
		Users u = new Users();
		u.setEmail(tokenValue);
		Example example = Example.of(u);
		Optional<Users> user = this.ur.findOne(example);
		Users User = user.get();
		return User;
	}
}
