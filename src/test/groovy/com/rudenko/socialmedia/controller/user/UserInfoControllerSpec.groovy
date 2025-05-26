package com.rudenko.socialmedia.controller

import com.rudenko.socialmedia.data.entity.User
import com.rudenko.socialmedia.service.user.UserInfoService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@SpringBootTest
@AutoConfigureMockMvc
class UserInfoControllerSpec extends Specification {

    @Autowired
    private MockMvc mockMvc

    @SpringBean
    private UserInfoService userService = Mock()

    def "get user by unique username"() {
        given:
            def username = "MegaMind"
            userService.findUser(username) >> new User()
        expect:
            mockMvc.perform(get("/api/v1/users/${username}"))
                    .andExpect { it.response.status == 200 }
    }

}
