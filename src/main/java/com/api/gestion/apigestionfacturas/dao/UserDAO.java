package com.api.gestion.apigestionfacturas.dao;

import com.api.gestion.apigestionfacturas.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDAO extends JpaRepository<UserModel, Integer>{

    UserModel findByEmail(@Param("email") String email);
}
