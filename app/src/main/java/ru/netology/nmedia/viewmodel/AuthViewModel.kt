package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.UserRepository
import java.io.File
import javax.inject.Inject

private val noPhoto = PhotoModel()

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth: AppAuth,
    private val repository: UserRepository
) : ViewModel() {

    val auth: LiveData<Token?> = appAuth.state
        .asLiveData()

    val isAuthorized: Boolean
        get() = auth.value?.token != null

    fun login(login: String, password: String) {
        viewModelScope.launch {
            try {
                val token = repository.login(login, password)
                appAuth.setAuth(token.id, token.token)
            } catch (e: Exception) {
            }
        }
    }

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    fun dropPhoto() {
        _photo.value = noPhoto
    }

    fun registerUser(login: String, password: String, name: String) {
        viewModelScope.launch {
//            TODO: переделать в один метод регистрации
            try {
                val token = when (_photo.value) {
                    noPhoto -> repository.registerUser(login, password, name)
                    else -> _photo.value?.file?.let { file ->
                        repository.registerUser(login, password, name, file)
//                        repository.saveWithAttachment(it, file)
                    }
                }


//                val token = repository.registerUser(login, password, name)
                appAuth.setAuth(token!!.id, token!!.token)
            } catch (e: Exception) {
            }
        }
    }



}