package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
//import com.google.firebase.ktx.Firebase
//import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.dto.Token
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _state = MutableStateFlow<Token?>(null)
    val state: StateFlow<Token?> = _state.asStateFlow()


    init {
        val id = prefs.getLong(ID_KEY, 0L)
        val token = prefs.getString(TOKEN_KEY, null)

        if (id != 0L && token != null) {
            _state.value = Token(id, token)
        } else {
            prefs.edit { clear() }
        }
//        sendPushToken()
    }

    @Synchronized
    fun setAuth(id: Long, token: String) {
        _state.value = Token(id, token)
        prefs.edit {
            putLong(ID_KEY, id)
            putString(TOKEN_KEY, token)
        }
//        sendPushToken()
    }

    @Synchronized
    fun clearAuth() {
        _state.value = null
        prefs.edit { clear() }
//        sendPushToken()
    }

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {
        fun getApiService(): ApiService
    }

//    fun sendPushToken(token: String? = null) {
//        CoroutineScope(Dispatchers.Default).launch {
//            val push = PushToken(token ?: Firebase.messaging.token.await())
//            try {
//                val entryPoint = EntryPointAccessors.fromApplication(context, AppAuthEntryPoint::class.java)
//                entryPoint.getApiService().savePushToken(push)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//        }
//    }

    companion object {
        private const val ID_KEY = "ID_KEY"
        private const val TOKEN_KEY = "TOKEN_KEY"

        private var INSTANCE: AppAuth? = null

        fun getInstance() = requireNotNull(INSTANCE) {
            "You must call init before"
        }


        fun init(context: Context) {
            INSTANCE = AppAuth(context.applicationContext)
        }
    }

}