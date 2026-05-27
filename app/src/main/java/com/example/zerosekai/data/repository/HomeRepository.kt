package com.example.zerosekai.data.repository

import com.example.zerosekai.data.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class HomeRepository {

    private val db = FirebaseFirestore.getInstance()

    // 🔥 PEGAR POSTS (FEED)
    suspend fun getPosts(): List<Post> {
        return try {

            val snapshot = db.collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Post::class.java)?.copy(id = doc.id)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // 🔥 CRIAR POST
    suspend fun createPost(post: Post) {
        try {
            db.collection("posts")
                .add(post)
                .await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 🔥 CURTIR POST
    suspend fun likePost(postId: String, userId: String) {
        try {
            db.collection("posts")
                .document(postId)
                .collection("likes")
                .document(userId)
                .set(mapOf("liked" to true))
                .await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 🔥 REMOVER CURTIDA
    suspend fun removeLike(postId: String, userId: String) {
        try {
            db.collection("posts")
                .document(postId)
                .collection("likes")
                .document(userId)
                .delete()
                .await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}