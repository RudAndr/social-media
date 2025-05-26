package com.rudenko.socialmedia.service.post

import com.rudenko.socialmedia.data.entity.Post
import com.rudenko.socialmedia.data.entity.User
import com.rudenko.socialmedia.data.request.post.PostEditAction
import com.rudenko.socialmedia.repository.post.PostLikeRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class PostLikeService {
    private def final likeEditHandlers = [
            (PostEditAction.LIKE_ADD)   : addLikeAction(),
            (PostEditAction.LIKE_DELETE): removeLikeAction()
    ]

    private final PostLikeRepository postLikeRepository

    PostLikeService(PostLikeRepository postLikeRepository) {
        this.postLikeRepository = postLikeRepository
    }

    Mono<Post> addLike(String postId, User currentUser) {
        return performLikeAction(PostEditAction.LIKE_ADD, postId, currentUser.id)
    }

    Mono<Post> removeLike(String postId, User currentUser) {
        return performLikeAction(PostEditAction.LIKE_DELETE, postId, currentUser.id)
    }

    private Mono<Post> performLikeAction(PostEditAction action, String postId, String userId) {
        return likeEditHandlers[action](postId, userId)
    }

    private Closure<Mono<Post>> addLikeAction() {
        return { String postId, String userId ->
            postLikeRepository.addLikeFrom(userId, postId)
        }
    }

    private Closure<Mono<Post>> removeLikeAction() {
        return { String postId, String userId ->
            postLikeRepository.removeLikeFrom(userId, postId)
        }
    }

}
