//package ru.netology.nmedia.viewmodel
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.asLiveData
//import androidx.lifecycle.viewModelScope
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.flatMapLatest
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.launch
//import ru.netology.nmedia.auth.AppAuth
//import ru.netology.nmedia.model.FeedModelState
//import ru.netology.nmedia.model.JobFeedModel
//import ru.netology.nmedia.repository.JobRepositoryImpl
//import ru.netology.nmedia.util.SingleLiveEvent
//import javax.inject.Inject
//
//@HiltViewModel
//class JobViewModel @Inject constructor(
//    private val repository: JobRepositoryImpl,
//    appAuth: AppAuth,
//) : ViewModel() {
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    val data: LiveData<JobFeedModel> = appAuth.state
//        .flatMapLatest { auth ->
//            repository.data.map { it ->
//                JobFeedModel(
//                    it.map { job ->
//                        job.copy(
//                            ownedByMe = auth?.id?.toInt() == userId.value
//                        )
//                    },
//                    it.isEmpty()
//                )
//            }
//    }.asLiveData(Dispatchers.Default)
//
//    private val _dataState = MutableLiveData<FeedModelState>()
//    val dataState: LiveData<FeedModelState>
//        get() = _dataState
//
//    private val _userId = MutableLiveData<Int>(0)
//    val userId: LiveData<Int>
//        get() = _userId
//
//    private val _jobCreated = SingleLiveEvent<Unit>()
//    val jobCreated: SingleLiveEvent<Unit>
//        get() = _jobCreated
//
//
////    Нужен ли init?
//    init {
//        loadJobs(userId.value?.toLong() ?: -1)
//    }
//
//    fun loadJobs(userId: Long) = viewModelScope.launch {
//        try {
//            _dataState.value = FeedModelState(loading = true)
//            clearJobs()
//            repository.getJobs(userId)
//            _dataState.value = FeedModelState()
//        } catch (e: Exception) {
//            _dataState.value = FeedModelState(error = true)
//        }
//    }
//
//    fun clearJobs() = viewModelScope.launch {
//        try {
//            _dataState.value = FeedModelState(loading = true)
//            repository.clearJobs()
//            _dataState.value = FeedModelState()
//        } catch (e: Exception) {
//            _dataState.value = FeedModelState(error = true)
//        }
//
//    }
//
//    fun setUserId(id: Long) {
//        _userId.value = id.toInt()
//    }
//
//    fun createJob(name: String, position: String, link: String?) {
//        viewModelScope.launch {
//            try {
//                jobCreated.value = repository.createJob(name, position, link)
//            } catch (e: Exception) {
//            }
//        }
//    }
//
//    fun removeById(id: Long) {
//        viewModelScope.launch {
//            try {
//                _dataState.value = FeedModelState(loading = true)
//                repository.removeById(id)
//                _dataState.value = FeedModelState()
//            } catch (e: Exception) {
//                _dataState.value = FeedModelState(error = true)
//            }
//        }
//    }
//
////    fun refreshPosts() = viewModelScope.launch {
////        try {
////            _dataState.value = FeedModelState(refreshing = true)
//////            repository.getAll()
////            _dataState.value = FeedModelState()
////        } catch (e: Exception) {
////            _dataState.value = FeedModelState(error = true)
////        }
////    }
////
////    fun save() {
////        edited.value?.let {
////            _postCreated.value = Unit
////            viewModelScope.launch {
////                try {
////                    when (_photo.value) {
////                        noPhoto -> repository.save(it)
////                        else -> _photo.value?.file?.let { file ->
////                            repository.saveWithAttachment(it, file)
////                        }
////                    }
////
////                    _dataState.value = FeedModelState()
////                } catch (e: Exception) {
////                    _dataState.value = FeedModelState(error = true)
////                }
////            }
////        }
////        edited.value = empty
////    }
////
////    fun edit(post: Post) {
////        edited.value = post
////    }
////
////    fun changeContent(content: String) {
////        val text = content.trim()
////        if (edited.value?.content == text) {
////            return
////        }
////        edited.value = edited.value?.copy(content = text)
////    }
////
////    fun likeById(post: Post) {
////        viewModelScope.launch {
////            try {
//////TODO: Нужно ли перед FeedModelState?
////                repository.likeById(post.id, post.likedByMe)
////                _dataState.value = FeedModelState()
////            } catch (e: Exception) {
////                _dataState.value = FeedModelState(error = true)
////            }
////        }
////    }
////
////    fun removeById(id: Long) = viewModelScope.launch {
////        try {
////            repository.removeById(id)
////            _dataState.value = FeedModelState()
////        } catch (e: Exception) {
////            _dataState.value = FeedModelState(error = true)
////        }
////
////    }
////
////    fun showAll() = viewModelScope.launch {
////        try {
////            repository.showAll()
////        } catch (e: Exception) {
////            _dataState.value = FeedModelState(error = true)
////        }
////    }
////
////    fun changePhoto(uri: Uri?, file: File?) {
////        _photo.value = PhotoModel(uri, file)
////    }
////
////    fun dropPhoto() {
////        _photo.value = noPhoto
////    }
//
//}