package com.rudenko.socialmedia.service.post

import com.rudenko.socialmedia.data.entity.Post
import com.rudenko.socialmedia.data.entity.User
import com.rudenko.socialmedia.data.request.post.EditPostRequest
import com.rudenko.socialmedia.repository.post.PostGeneralRepository
import com.rudenko.socialmedia.service.user.UserInfoService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class PostGeneralService {

    private final PostGeneralRepository postRepository
    private final UserInfoService userService

    PostGeneralService(PostGeneralRepository postRepository,
                       UserInfoService userService) {
        this.postRepository = postRepository
        this.userService = userService
    }

    //this is with subs. We need with no subs
    Flux<Post> getPosts(String userId, User currentUser) {
        return getPostTargetUser(userId, currentUser)
                .map {
                    it.id == currentUser.id ? it.subscriptions : []
                }
                .flatMapMany {
                    def targetUsers = new HashSet<String>(it)
                    targetUsers.add(userId)

                    return postRepository.getPostsFromUsers(targetUsers)
                }
    }

    Mono<Post> createPost(Post post) {
        return postRepository.createPost(post)
    }

    Mono<Post> editPost(String postId, EditPostRequest editPostRequest, String userId) {
        return postRepository.editPostText(postId, userId, editPostRequest.text)
    }

    Mono<Boolean> deletePost(String postId, String userId) {
        return postRepository.deletePost(postId, userId)
    }

    private Mono<User> getPostTargetUser(String userId, User currentUser) {
        return userId == currentUser.id ? Mono.just(currentUser) : userService.findUserById(userId)
    }

}
