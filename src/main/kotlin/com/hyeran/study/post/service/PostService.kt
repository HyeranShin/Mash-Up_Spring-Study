package com.hyeran.study.post.service

import com.hyeran.study.comment.domain.CommentRepository
import com.hyeran.study.like.domain.LikeRepository
import com.hyeran.study.post.dto.ReqWriteDto
import com.hyeran.study.post.dto.ResAllPostsDto
import com.hyeran.study.post.domain.Post
import com.hyeran.study.post.domain.PostRepository
import com.hyeran.study.user.domain.UserRepository
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.util.stream.Collectors

@Service
class PostService(val postRepository: PostRepository, val userRepository: UserRepository,
                  val commentRepository: CommentRepository, val likeRepository: LikeRepository) {

    fun getAllPosts(): MutableList<ResAllPostsDto>? {
        return postRepository.findAll()
                .stream()
                .map { post: Post -> ResAllPostsDto(post.id!!, post.title, post.content, post.writer, post.likeCnt, post.dislikeCnt) }
                .collect(Collectors.toList())
    }

    fun writePost(reqWriteDto: ReqWriteDto): Long {
        val user = userRepository.findById(reqWriteDto.userId)
                .orElseThrow { RuntimeException() }
        val post = Post(title = reqWriteDto.title, content = reqWriteDto.content, writer = user.name, userId = user.id!!)
        return postRepository.save(post).id!!
    }

    fun deletePost(postId: Long) {
        postRepository.deleteById(postId)
        deleteRelatedComments(postId)
        deleteRelatedLikes(postId)
    }

    fun deleteRelatedComments(postId: Long) {
        val comments = commentRepository.findAllByPostId(postId)
        for(comment in comments) {
            commentRepository.delete(comment)
        }
    }

    fun deleteRelatedLikes(postId: Long) {
        val likes = likeRepository.findAllByPostId(postId)
        for(like in likes) {
            likeRepository.delete(like)
        }
    }
}