//package ru.netology.nmedia.viewmodel
//
//import android.net.Uri
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.asFlow
//import androidx.lifecycle.viewModelScope
//import androidx.paging.PagingData
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.flatMapLatest
//import kotlinx.coroutines.launch
//import ru.netology.nmedia.auth.AppAuth
//import ru.netology.nmedia.dto.Post
//import ru.netology.nmedia.model.FeedModel
//import ru.netology.nmedia.model.FeedModelState
//import ru.netology.nmedia.model.PhotoModel
//import ru.netology.nmedia.util.SingleLiveEvent
//import java.io.File
//import javax.inject.Inject
//
//@HiltViewModel
//class PostViewModel_old @Inject constructor(
//    private val repository: PostRepository,
//    appAuth: AppAuth,
//) : ViewModel() {
//
//    private val _userId = MutableLiveData<Long>(0)
//    val userId: LiveData<Long>
//        get() = _userId
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    val data: Flow<PagingData<Post>> = userId.asFlow()
//        .flatMapLatest { id ->
//        appAuth.state.flatMapLatest { auth ->
//            repository.data.map { pagingData ->
//                pagingData.filter { post ->
//                    // Возвращаем все посты, если userId == 0, иначе фильтруем
//                    id == 0L || post.authorId == id
//                }.map { post ->
//                    post.copy(
//                        ownedByMe = post.authorId == auth?.id
//                    )
//                }
//            }
//        }
//    }.flowOn(Dispatchers.Default)
//
//
////    @OptIn(ExperimentalCoroutinesApi::class)
////    val data: Flow<PagingData<Post>> = appAuth
////        .state.flatMapLatest { auth ->
////            repository.data
////                .map { posts ->
////                    posts.filter { post ->
////                        (post.likeOwnerIds?.size ?: 0) > 2
////                    }.map { post ->
////                        post.copy(
////                            ownedByMe = post.authorId == auth?.id
////                        )
////                    }
////                }
////        }
////        .flowOn(Dispatchers.Default)
//
//
//
//
//    val postData: LiveData<FeedModel> =
//        repository.postData.map(::FeedModel).asLiveData(Dispatchers.Default)
//
//    private val _dataState = MutableLiveData<FeedModelState>()
//    val dataState: LiveData<FeedModelState>
//        get() = _dataState
//
//    private val _photo = MutableLiveData(noPhoto)
//    val photo: LiveData<PhotoModel>
//        get() = _photo
//
////    val newerCount: LiveData<Int> = data.switchMap {
////        val newerId = it.firstOrNull()?.id ?: 0L
////        repository.getNewerCount(newerId)
////            .asLiveData(Dispatchers.Default)
////    }
//
//    private val edited = MutableLiveData(empty)
//
//    private val _postCreated = SingleLiveEvent<Unit>()
//    val postCreated: LiveData<Unit>
//        get() = _postCreated
//
//
//    init {
//        loadPosts()
//    }
//
//    fun loadPosts() = viewModelScope.launch {
//        try {
//            _dataState.value = FeedModelState(loading = true)
////            repository.getAll()
//            _dataState.value = FeedModelState()
//        } catch (e: Exception) {
//            _dataState.value = FeedModelState(error = true)
//        }
//    }
//
//    fun refreshPosts() = viewModelScope.launch {
//        try {
//            _dataState.value = FeedModelState(refreshing = true)
////            repository.getAll()
//            _dataState.value = FeedModelState()
//        } catch (e: Exception) {
//            _dataState.value = FeedModelState(error = true)
//        }
//    }
//
//    fun save() {
//        edited.value?.let {
//            _postCreated.value = Unit
//            viewModelScope.launch {
//                try {
//                    when (_photo.value) {
//                        noPhoto -> repository.save(it)
//                        else -> _photo.value?.file?.let { file ->
//                            repository.saveWithAttachment(it, file)
//                        }
//                    }
//
//                    _dataState.value = FeedModelState()
//                } catch (e: Exception) {
//                    _dataState.value = FeedModelState(error = true)
//                }
//            }
//        }
//        edited.value = empty
//    }
//
//    fun edit(post: Post) {
//        edited.value = post
//    }
//
//    fun changeContent(content: String) {
//        val text = content.trim()
//        if (edited.value?.content == text) {
//            return
//        }
//        edited.value = edited.value?.copy(content = text)
//    }
//
//    fun likeById(post: Post) {
//        viewModelScope.launch {
//            try {
////TODO: Нужно ли перед FeedModelState?
//                repository.likeById(post.id, post.likedByMe)
//                _dataState.value = FeedModelState()
//            } catch (e: Exception) {
//                _dataState.value = FeedModelState(error = true)
//            }
//        }
//    }
//
//    fun removeById(id: Long) = viewModelScope.launch {
//        try {
//            repository.removeById(id)
//            _dataState.value = FeedModelState()
//        } catch (e: Exception) {
//            _dataState.value = FeedModelState(error = true)
//        }
//
//    }
//
//    fun showAll() = viewModelScope.launch {
//        try {
//            repository.showAll()
//        } catch (e: Exception) {
//            _dataState.value = FeedModelState(error = true)
//        }
//    }
//
//    fun changePhoto(uri: Uri?, file: File?) {
//        _photo.value = PhotoModel(uri, file)
//    }
//
//    fun dropPhoto() {
//        _photo.value = noPhoto
//    }
//
//    fun setUserId(id: Long) {
//        _userId.value = id
//    }
//
//}