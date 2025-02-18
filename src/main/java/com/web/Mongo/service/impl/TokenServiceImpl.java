package com.web.Mongo.service.impl;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import com.web.Mongo.model.collection.User;
import com.web.Mongo.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private SecretKey secretKey;

    @Override
    public String generateToken(User user) {
        String username = user.getUsername();
        String role = user.getRole().getName();
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .expirationTime(new Date(new Date().getTime() + 1000 * 60 * 15))
                .claim("roles", List.of(role))
                .build();
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), jwtClaimsSet);
        try{
            JWSSigner jwsSigner = new MACSigner(secretKey);
            signedJWT.sign(jwsSigner);
            return signedJWT.serialize();
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public String generateRefreshToken(User user) {
        String username = user.getUsername();
        String role = user.getRole().getName();
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .expirationTime(new Date(new Date().getTime() + 1000 * 60 * 60 * 2))
                .claim("roles", List.of(role))
                .build();
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), jwtClaimsSet);
        try{
            JWSSigner jwsSigner = new MACSigner(secretKey);
            signedJWT.sign(jwsSigner);
            return signedJWT.serialize();
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public String getUsername(String token) {
        try{
           SignedJWT signedJWT = SignedJWT.parse(token);
           JWSVerifier jwsVerifier = new MACVerifier(secretKey);
           if(signedJWT.verify(jwsVerifier) && new Date(new Date().getTime()).before(signedJWT.getJWTClaimsSet().getExpirationTime())){
               return signedJWT.getJWTClaimsSet().getSubject();
           }
           return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean validateRefreshToken(String refreshToken) {
        try{
            SignedJWT signedJWT = SignedJWT.parse(refreshToken);
            JWSVerifier jwsVerifier = new MACVerifier(secretKey);
            if(signedJWT.verify(jwsVerifier)
                    && new Date(new Date().getTime()).before(signedJWT.getJWTClaimsSet().getExpirationTime())){
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
