package com.study.jpa.repository;

import com.study.jpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by IntelliJ IDEA.
 * User: KingRainGrey
 * Date: 2020/8/31
 */
@Repository
public interface UserDao extends JpaRepository<User, Long> {

}

