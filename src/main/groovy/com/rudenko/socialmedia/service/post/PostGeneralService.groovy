package com.rudenko.socialmedia.service.post

import com.rudenko.socialmedia.data.entity.Post
import com.rudenko.socialmedia.data.entity.User
import com.rudenko.socialmedia.data.request.post.EditPostRequest
import com.rudenko.socialmedia.data.request.post.PostEditAction
import com.rudenko.socialmedia.data.response.ApiResponse
import com.rudenko.socialmedia.data.response.CommentResponse
import com.rudenko.socialmedia.repository.post.PostRepository
import com.rudenko.socialmedia.service.user.UserInfoService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

//todo refactor
@Service
class PostService {

    private final PostRepository postRepository
    private final UserInfoService userService

    private def final likeEditHandlers = [
            (PostEditAction.LIKE_ADD)   : addLikeAction(),
            (PostEditAction.LIKE_DELETE): removeLikeAction()
    ]

    PostService(PostRepository postRepository,
                UserInfoService userService) {
        this.postRepository = postRepository
        this.userService = userService
    }

    Flux<Post> getPosts(String username, User currentUser) {
        return getPostTargetUser(username, currentUser)
                .flux()
                .map {it.subscriptions }
                .flatMap {
                    def targetUsers = new HashSet<String>(it)
                    targetUsers.add(username)

                    return postRepository.streamPostsForSubscribers(targetUsers)
                }
    }

    Mono<Post> createPost(Post post) {
        return postRepository.createPost(post)
    }

    Mono<Post> addLike(String postId, User currentUser) {
        return likeEditHandlers[PostEditAction.LIKE_ADD](postId, currentUser.username)
    }

    Mono<Post> removeLike(String postId, User currentUser) {
        return likeEditHandlers[PostEditAction.LIKE_DELETE](postId, currentUser.username)
    }

    Mono<Post> editPost(String postId, EditPostRequest editPostRequest) {
        return postRepository.editPostText(postId, editPostRequest.text)
    }

    Mono<Boolean> deletePost(String postId) {
        return postRepository.deletePost(postId)
    }

    private Mono<User> getPostTargetUser(String username, User currentUser) {
        return username == null ? Mono.just(currentUser) : userService.findUser(username)
    }

    private Closure<Mono<Post>> addLikeAction() {
        return { String postId, String username ->
            postRepository.addLikeFrom(username, postId)
        }
    }

    private Closure<Mono<Post>> removeLikeAction() {
        return { String postId, String username ->
            postRepository.removeLikeFrom(username, postId)
        }
    }

    Flux<CommentResponse> getComments(String postId) {
        null
    }
}
