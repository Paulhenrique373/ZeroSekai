package com.example.zerosekai.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log

import com.example.zerosekai.data.model.Comment
import com.example.zerosekai.data.model.Post
import com.example.zerosekai.supabase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

import io.github.jan.supabase.storage.storage

import kotlinx.coroutines.tasks.await

import java.util.UUID

class PostRepository {

    private val auth = FirebaseAuth.getInstance()

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun uploadPost(
        context: Context,
        imageUri: Uri,
        caption: String
    ): Result<Unit> {

        return try {

            // VALIDAR LEGENDA
            if (caption.isBlank()) {

                return Result.failure(
                    Exception("Digite uma legenda")
                )
            }

            // USUÁRIO LOGADO
            val currentUser = auth.currentUser
                ?: return Result.failure(
                    Exception("Usuário não logado")
                )

            val uid = currentUser.uid

            // NOME DO ARQUIVO
            val fileName =
                "${UUID.randomUUID()}.jpg"

            // LER IMAGEM
            val inputStream =
                context.contentResolver
                    .openInputStream(imageUri)

            val bytes =
                inputStream?.use {
                    it.readBytes()
                } ?: return Result.failure(
                    Exception("Erro ao ler imagem")
                )

            Log.d(
                "ZEROSK",
                "Imagem lida: ${bytes.size} bytes"
            )

            // UPLOAD SUPABASE
            supabase.storage
                .from("zerosekai-storage")
                .upload(
                    path = fileName,
                    data = bytes
                )

            Log.d(
                "ZEROSK",
                "Upload Supabase OK"
            )

            // URL PÚBLICA REAL
            val imageUrl =
                "https://hnxhkrzjbueoootxinvt.supabase.co/storage/v1/object/public/zerosekai-storage/$fileName"

            Log.d(
                "ZEROSK",
                "URL imagem: $imageUrl"
            )

            // BUSCAR USUÁRIO
            val userDoc =
                firestore
                    .collection("users")
                    .document(uid)
                    .get()
                    .await()

            val username =
                userDoc.getString("username")
                    ?: "Usuário"

            val userPhoto =
                userDoc.getString("photoUrl")
                    ?: ""

            Log.d(
                "ZEROSK",
                "Usuário carregado"
            )

            // ID POST
            val postId =
                firestore
                    .collection("posts")
                    .document()
                    .id

            // CRIAR POST
            val post = Post(

                id = postId,

                userId = uid,

                userName = username,

                userPhoto = userPhoto,

                imageUrl = imageUrl,

                caption = caption,

                likes = emptyList(),

                timestamp =
                System.currentTimeMillis()
            )

            Log.d(
                "ZEROSK",
                "Post criado"
            )

            // SALVAR FIRESTORE
            firestore
                .collection("posts")
                .document(postId)
                .set(post)
                .await()

            Log.d(
                "ZEROSK",
                "Firestore salvou post"
            )

            Result.success(Unit)

        } catch (e: Exception) {

            Log.e(
                "ZEROSK",
                "ERRO AO POSTAR",
                e
            )

            e.printStackTrace()

            Result.failure(e)
        }
    }

    // CURTIR / DESCURTIR POST
    suspend fun toggleLike(
        postId: String
    ) {

        val currentUser =
            auth.currentUser ?: return

        val uid =
            currentUser.uid

        val postRef =
            firestore
                .collection("posts")
                .document(postId)

        firestore.runTransaction { transaction ->

            val snapshot =
                transaction.get(postRef)

            val postLikes =
                snapshot.get("likes")
                        as? List<String>
                    ?: emptyList()

            val updatedLikes =

                if (postLikes.contains(uid)) {

                    postLikes - uid

                } else {

                    postLikes + uid
                }

            transaction.update(
                postRef,
                "likes",
                updatedLikes
            )

        }.await()
    }

    // ADICIONAR COMENTÁRIO
    suspend fun addComment(
        postId: String,
        text: String
    ) {

        val currentUser =
            auth.currentUser ?: return

        val uid =
            currentUser.uid

        val userDoc =
            firestore
                .collection("users")
                .document(uid)
                .get()
                .await()

        val username =
            userDoc.getString("username")
                ?: "Usuário"

        val userPhoto =
            userDoc.getString("photoUrl")
                ?: ""

        val commentId =
            firestore
                .collection("posts")
                .document(postId)
                .collection("comments")
                .document()
                .id

        val comment = Comment(

            id = commentId,

            postId = postId,

            userId = uid,

            userName = username,

            userPhoto = userPhoto,

            text = text,

            timestamp =
            System.currentTimeMillis()
        )

        firestore
            .collection("posts")
            .document(postId)
            .collection("comments")
            .document(commentId)
            .set(comment)
            .await()
    }

    // PEGAR COMENTÁRIOS
    fun getCommentsRealtime(
        postId: String,
        onChange: (List<Comment>) -> Unit
    ) {

        firestore
            .collection("posts")
            .document(postId)
            .collection("comments")

            .orderBy(
                "timestamp",
                Query.Direction.ASCENDING
            )

            .addSnapshotListener { value, error ->

                if (error != null) {

                    Log.e(
                        "ZEROSK",
                        "Erro comentários",
                        error
                    )

                    return@addSnapshotListener
                }

                val comments =
                    value?.documents?.mapNotNull {

                        it.toObject(
                            Comment::class.java
                        )

                    } ?: emptyList()

                onChange(comments)
            }
    }

    // POSTS EM TEMPO REAL
    fun getPostsRealtime(
        onChange: (List<Post>) -> Unit
    ) {

        firestore
            .collection("posts")

            .orderBy(
                "timestamp",
                Query.Direction.DESCENDING
            )

            .addSnapshotListener { value, error ->

                if (error != null) {

                    Log.e(
                        "ZEROSK",
                        "Erro posts realtime",
                        error
                    )

                    return@addSnapshotListener
                }

                val posts =
                    value?.documents?.mapNotNull {

                        it.toObject(
                            Post::class.java
                        )

                    } ?: emptyList()

                Log.d(
                    "ZEROSK",
                    "Posts carregados: ${posts.size}"
                )

                onChange(posts)
            }
    }
}