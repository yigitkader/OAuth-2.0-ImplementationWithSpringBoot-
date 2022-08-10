package com.yigitco.springsecurityclient.controller

import com.yigitco.springsecurityclient.dto.UserSignupRequestDto
import com.yigitco.springsecurityclient.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
class RegistrationController(
    private val userService: UserService
) {

    @PostMapping("/register")
    fun registerUser(@RequestBody userSignupRequestDto: UserSignupRequestDto,httpServletRequest: HttpServletRequest): String {
        userService.registerUser(userSignupRequestDto,httpServletRequest)
        return "Success"
    }



    @GetMapping("/hi")
    fun sayHi() : String{
        return "Hi :)"
    }

}