package com.yigitco.springsecurityclient.repository

import com.yigitco.springsecurityclient.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User,Long> {

}