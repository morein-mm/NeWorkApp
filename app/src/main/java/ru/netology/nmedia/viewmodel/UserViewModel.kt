package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.model.UsersFeedModel
import ru.netology.nmedia.model.UsersModelState
import ru.netology.nmedia.repository.UserRepositoryImpl
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
//    private val appAuth: AppAuth,
    private val repository: UserRepositoryImpl
) : ViewModel() {

    val data: LiveData<UsersFeedModel> = repository.data.map { list ->
        UsersFeedModel(
            list.sortedBy { it.name }, // Сортировка по алфавиту
            list.isEmpty()
        )
    }.asLiveData(Dispatchers.Default)

    private val _dataState = MutableLiveData<UsersModelState>()
    val dataState: LiveData<UsersModelState>
        get() = _dataState

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

//    private val _selectedUsers: MutableLiveData<List<Long>> =
//        MutableLiveData(mutableListOf())
//    val selectedUsers: LiveData<List<Long>>
//        get() = _selectedUsers

    // Список выбранных пользователей
    private val _selectedUsers = MutableLiveData<Set<Long>>(emptySet())
    val selectedUsers: LiveData<Set<Long>> get() = _selectedUsers


    init {
        loadUsers()
    }

    fun loadUsers() = viewModelScope.launch {
        try {
            _dataState.value = UsersModelState(loading = true)
            repository.getAllUsers()
            _dataState.value = UsersModelState()
        } catch (e: Exception) {
            _dataState.value = UsersModelState(error = true)
        }
    }

    fun getUserById(id: Long){
        viewModelScope.launch {
            _user.value = repository.getById(id)
        }
    }

//    fun selectUser(userId: Long) {
//        if (_selectedUsers.value?.contains(userId) != true) {
//            _selectedUsers.value = _selectedUsers.value?.plus(userId)
//        }
//    }
//
//    fun unSelectUser(userId: Long) {
//        _selectedUsers.value = _selectedUsers.value?.minus(userId)
//    }
//
//    fun clearSelectedUsers() {
//        _selectedUsers.value = mutableListOf()
//    }

    fun setSelectedUsers(users: List<Long>) {
        _selectedUsers.value = users.toSet()
    }

    fun selectUser(userId: Long) {
        _selectedUsers.value = _selectedUsers.value?.plus(userId)
    }

    fun unSelectUser(userId: Long) {
        _selectedUsers.value = _selectedUsers.value?.minus(userId)
    }

    fun toggleUserSelection(userId: Long) {
        val currentSelection = _selectedUsers.value.orEmpty()
        val updatedSelection = if (currentSelection.contains(userId)) {
            currentSelection - userId // Убираем пользователя из списка
        } else {
            currentSelection + userId // Добавляем пользователя в список
        }
        _selectedUsers.value = updatedSelection
    }


}