package com.rudenko.socialmedia.data.entity

import com.rudenko.socialmedia.data.request.user.UserCreateRequest
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.FieldType

@Document(value = "users")
class User {
    @Id
    @Field(targetType = FieldType.OBJECT_ID)
    private String id

    @Field("username")
    private String username

    @Field("password")
    private String password

    @Field("display_name")
    private String displayName

    @Field("subscriptions")
    private List<String> subscriptions

    User() {
    }

    User(String id, String username, String password, String displayName, List<String> subscriptions) {
        this.id = id
        this.username = username
        this.password = password
        this.displayName = displayName
        this.subscriptions = subscriptions
    }

    private User(UserCreateRequest userCreateRequest) {
        this.username = userCreateRequest.username
        this.password = userCreateRequest.password
        this.displayName = userCreateRequest.displayName
    }

    String getId() {
        return id
    }

    void setId(String id) {
        this.id = id
    }

    String getUsername() {
        return username
    }

    void setUsername(String username) {
        this.username = username
    }

    String getPassword() {
        return password
    }

    void setPassword(String password) {
        this.password = password
    }

    String getDisplayName() {
        return displayName
    }

    void setDisplayName(String displayName) {
        this.displayName = displayName
    }

    List<String> getSubscriptions() {
        return subscriptions ?: []
    }

    void setSubscriptions(List<String> subscriptions) {
        this.subscriptions = subscriptions
    }

    static User ofRequest(UserCreateRequest userCreateRequest) {
        return new User(userCreateRequest)
    }
}
