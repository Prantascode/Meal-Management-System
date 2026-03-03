package com.pranta.MealManagement.Security;

import com.pranta.MealManagement.Entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class MemberUserDetails implements UserDetails {

    private final Member member;

    public MemberUserDetails(Member member) {
        this.member = member;
    }
    
    public Long getMessId() {
        return member.getMess().getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = member.getRole().name(); // e.g., "ADMIN"
        
        return List.of(
            new SimpleGrantedAuthority(roleName),            // Matches .hasAuthority("ADMIN")
            new SimpleGrantedAuthority("ROLE_" + roleName)   // Matches .hasRole("ADMIN")
        );
    }

    @Override
    public String getPassword() { return member.getPassword(); }

    @Override
    public String getUsername() { return member.getEmail(); }

    @Override
    public boolean isEnabled() { return member.isActive(); }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
}