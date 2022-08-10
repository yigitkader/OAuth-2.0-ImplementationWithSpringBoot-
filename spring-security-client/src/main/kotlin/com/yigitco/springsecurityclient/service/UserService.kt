package com.yigitco.springsecurityclient.service

import com.yigitco.springsecurityclient.dto.UserSignupRequestDto
import com.yigitco.springsecurityclient.dto.UserSignupResponseDto
import com.yigitco.springsecurityclient.event.RegistrationCompleteEvent
import com.yigitco.springsecurityclient.model.User
import com.yigitco.springsecurityclient.repository.UserRepository
import com.yigitco.springsecurityclient.util.UtilizationFunctions
import com.yigitco.springsecurityclient.util.mapper.UserSignupRequestMapper
import com.yigitco.springsecurityclient.util.mapper.UserSignupResponseMapper
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userSignupRequestMapper: UserSignupRequestMapper,
    private val userSignupResponseMapper: UserSignupResponseMapper,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val utilizationFunctions: UtilizationFunctions
) {


    fun registerUser(
        userSignupRequestDto: UserSignupRequestDto,
        httpServletRequest: HttpServletRequest
    ): UserSignupResponseDto {
        try {
            val requestUser = userSignupRequestMapper.mapToEntityFromDto(userSignupRequestDto)
            val savedUser = userRepository.save(requestUser)
            applicationEventPublisher.publishEvent(
                RegistrationCompleteEvent(
                    savedUser,
                    utilizationFunctions.getApplicationURL(httpServletRequest)
                )
            )
            return userSignupResponseMapper.mapToDto(savedUser)
        } catch (e: Exception) {
            throw Exception()
        }
    }



    fun updateUserEnabled(user: User) {
        val enableUserRequest = User(user.id,user.firstName,user.lastName,user.email,user.passwd,user.role,true)
        userRepository.save(enableUserRequest)
    }

}