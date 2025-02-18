package com.web.Mongo.model.collection;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Unwrapped;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user")
public class User implements UserDetails {
    @Id
    private String id;

    private String username;

    private String email;

    private String password;

    @DocumentReference(lookup = "{'userId': ?#{#self._id}}")
    private List<RefreshToken> refreshTokens;

    @DocumentReference
    private Role role;

    @DocumentReference(lazy = true)
    private List<MyFavouriteBook> myFavouriteBooks;

    private Boolean enabled;

    @Unwrapped(onEmpty = Unwrapped.OnEmpty.USE_NULL)
    private Verify verify;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        return authorities;
    }

}
