package com.rudenko.socialmedia.data

import lombok.Getter
import lombok.Setter
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.FieldType

@Getter
@Setter
@Document(value = "users")
class User {
    @Id
    @Field(targetType = FieldType.OBJECT_ID)
    private String id
    private String username
    private String password
}
