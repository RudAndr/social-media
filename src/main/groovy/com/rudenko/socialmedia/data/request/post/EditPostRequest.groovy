package com.rudenko.socialmedia.data.request.post

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class EditPostRequest {
    private String text

    EditPostRequest() {
    }

    @JsonCreator
    EditPostRequest(@JsonProperty("text") String text) {
        this.text = text
    }

    String getText() {
        return text
    }
}
