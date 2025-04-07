//package ru.netology.nmedia.service
//
//import android.Manifest
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.content.Context
//import android.content.pm.PackageManager
//import android.os.Build
//import androidx.core.app.NotificationCompat
//import androidx.core.app.NotificationManagerCompat
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//import com.google.gson.Gson
//import dagger.hilt.android.AndroidEntryPoint
//import ru.netology.nmedia.R
//import ru.netology.nmedia.auth.AppAuth
//import javax.inject.Inject
//import kotlin.random.Random
//
//@AndroidEntryPoint
//class FCMService : FirebaseMessagingService() {
//    private val action = "action"
//    private val content = "content"
//    private val channelId = "remote"
//    private val gson = Gson()
//
//    @Inject
//    lateinit var appAuth: AppAuth
//
//    override fun onCreate() {
//        super.onCreate()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = getString(R.string.channel_remote_name)
//            val descriptionText = getString(R.string.channel_remote_description)
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel(channelId, name, importance).apply {
//                description = descriptionText
//            }
//            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            manager.createNotificationChannel(channel)
//        }
//    }
//
//    override fun onMessageReceived(message: RemoteMessage) {
//        println(message.data["content"])
//        message.data[action]?.let {
//            when (Action.valueOf(it)) {
//                Action.LIKE -> handleLike(gson.fromJson(message.data[content], Like::class.java))
//            }
//        }
//
//        message.data["content"]?.let {
//            val message = gson.fromJson(message.data[content], Message::class.java)
//            when (message.recepientId) {
//                appAuth.state.value?.id, null -> showNotification(message.content)
//                else -> appAuth.sendPushToken(appAuth.state.value?.token)
//            }
//        }
//    }
//
//    override fun onNewToken(token: String) {
//        println(token)
//        appAuth.sendPushToken(token)
//    }
//
//    private fun handleLike(content: Like) {
//        val notification = NotificationCompat.Builder(this, channelId)
////            .setSmallIcon(R.drawable.ic_notification)
//            .setContentTitle(
//                getString(
//                    R.string.notification_user_liked,
//                    content.userName,
//                    content.postAuthor,
//                )
//            )
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .build()
//
//        notify(notification)
//    }
//
//    private fun showNotification(content: String) {
//        val notification = NotificationCompat.Builder(this, channelId)
////            .setSmallIcon(R.drawable.ic_notification)
//            .setContentTitle(
//                content
//            )
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .build()
//
//        notify(notification)
//    }
//
//    private fun notify(notification: Notification) {
//        if (
//            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
//            checkSelfPermission(
//                Manifest.permission.POST_NOTIFICATIONS
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            NotificationManagerCompat.from(this)
//                .notify(Random.nextInt(100_000), notification)
//        }
//    }
//}
//
//enum class Action {
//    LIKE,
//}
//
//data class Like(
//    val userId: Long,
//    val userName: String,
//    val postId: Long,
//    val postAuthor: String,
//)
//
//data class Message(
//    val recepientId: Long?,
//    val content: String,
//)
//
