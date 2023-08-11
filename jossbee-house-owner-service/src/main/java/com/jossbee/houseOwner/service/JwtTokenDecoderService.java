package com.jossbee.houseOwner.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jossbee.houseOwner.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JwtTokenDecoderService {

    public String extractIdentifierFormToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            String subject = decodedJWT.getSubject();

            if (subject == null || subject.isEmpty()) {
                throw new ServiceException("User ID can not be null or empty!");
            }

            return subject;
        } catch (Exception e) {
            log.error("Failed to extract identifier form jwt token, error occurred. Error is: {}", e.getMessage(), e);
            throw new ServiceException("Failed to decode jwt token. Error is: " + e.getMessage());
        }
    }

}
