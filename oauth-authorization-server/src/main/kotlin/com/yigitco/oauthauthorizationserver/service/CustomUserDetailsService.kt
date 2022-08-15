package com.yigitco.oauthauthorizationserver.service

import com.yigitco.oauthauthorizationserver.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional
import kotlin.collections.ArrayList

@Service
@Transactional
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {


    @Bean
    fun passwordEncoder() : PasswordEncoder{
        return BCryptPasswordEncoder(11)
    }

    override fun loadUserByUsername(email: String?): UserDetails {

        val user = email?.let { userRepository.findUserByEmail(it) }?.orElseThrow { Exception("No User Found") }

        if (user != null) {
            return User(
                user.email,
                user.passwd,
                user.enabled,
                true,
                true,
                true,
                getAuthorities(listOf(user.role))
            )
        }

        throw Exception("No User Found")
    }

    private fun getAuthorities(role: List<String>): Collection<GrantedAuthority> {
        val authorities = ArrayList<GrantedAuthority>()
        role.forEach { authorities.add(SimpleGrantedAuthority(it)) }
        return authorities
    }

}