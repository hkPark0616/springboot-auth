package com.ssafy.springbootauth.dto;

import com.ssafy.springbootauth.entity.UserEntity;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Data
public class CustomUserDetails implements UserDetails {

	private final Optional<UserEntity> userEntity;
	private final String type;

	public CustomUserDetails(Optional<UserEntity> userEntity) {

		this.userEntity = userEntity;
		this.type = "user";
	}
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(type.equals("user") ? "ROLE_USER" : ""));
		return authorities;
	}

	@Override
	public String getPassword() {
		return type.equals("user") ? userEntity.get().getUserPw() : "";
	}

	@Override
	public String getUsername() {
		return type.equals("user") ? String.valueOf(userEntity.get().getUserSeq()) : "";
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
