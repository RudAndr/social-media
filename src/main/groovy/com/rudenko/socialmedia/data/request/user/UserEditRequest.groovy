package com.rudenko.socialmedia.data.request.user

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class UserEditRequest {
    private UserEditAction action
    private String newValue

    @JsonCreator
    UserEditRequest(@JsonProperty("action") UserEditAction action,
                    @JsonProperty("newValue") String newValue) {
        this.action = action
        this.newValue = newValue
    }

    UserEditAction getAction() {
        return action
    }

    void setAction(UserEditAction action) {
        this.action = action
    }

    String getNewValue() {
        return newValue
    }

    void setNewValue(String newValue) {
        this.newValue = newValue
    }
}
