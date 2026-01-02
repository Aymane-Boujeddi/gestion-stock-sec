package com.gestion.stock.repository;

import com.gestion.stock.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    

    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.role r " +
           "LEFT JOIN FETCH u.userPermissions up " +
           "LEFT JOIN FETCH up.permission " +
           "WHERE u.username = :username")
    Optional<User> findUserByUsername(@Param("username") String username);


    boolean existsByUsername(String username);

    Optional<User> findByAuthProviderAndClientIdSub(String authProvider, String clientIdSub);

    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.role r " +
           "LEFT JOIN FETCH u.userPermissions up " +
           "LEFT JOIN FETCH up.permission " +
           "WHERE u.authProvider = :authProvider AND u.clientIdSub = :clientIdSub")
    Optional<User> findByAuthProviderAndClientIdSubWithPermissions(
            @Param("authProvider") String authProvider,
            @Param("clientIdSub") String clientIdSub
    );

}
