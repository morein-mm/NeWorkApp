//package ru.netology.nmedia.viewmodel
//
//import android.net.Uri
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import androidx.paging.PagingData
//import androidx.paging.map
//import androidx.room.Embedded
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.flatMapLatest
//import kotlinx.coroutines.flow.flowOn
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.launch
//import ru.netology.nmedia.auth.AppAuth
//import ru.netology.nmedia.dto.Attachment
//import ru.netology.nmedia.dto.Coordinates
//import ru.netology.nmedia.dto.Event
//import ru.netology.nmedia.enumeration.AttachmentType
//import ru.netology.nmedia.enumeration.EventType
//import ru.netology.nmedia.model.AttachmentModel
//import ru.netology.nmedia.model.FeedModelState
//import ru.netology.nmedia.model.PhotoModel
//import ru.netology.nmedia.repository.EventRepositoryImpl
//import java.io.File
//import javax.inject.Inject
//
//private val empty = Event(
//    id = 0,
//    author = "",
//    authorId = 0,
//    authorJob = null,
//    authorAvatar = null,
//    content = "",
//    datetime = "",
//    published = "",
//    coords = null,
//    type = EventType.ONLINE,
//    likeOwnerIds = null,
//    likedByMe = false,
//    speakerIds = null,
//    participantsIds = null,
//    participatedByMe = false,
//    attachment = null,
//    link = null,
//    ownedByMe = false,
//)
//
//private val noPhoto = PhotoModel()
//
//private val noAttachment = AttachmentModel()
//
//@HiltViewModel
//class EventViewModel @Inject constructor(
//    private val repository: EventRepositoryImpl,
//    appAuth: AppAuth,
//) : ViewModel() {
//
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    val data: Flow<PagingData<Event>> = appAuth
//        .state.flatMapLatest { auth ->
//            repository.data
//                .map { events ->
//                    events.map {
//                        it.copy(
//                            ownedByMe = it.authorId == auth?.id
//                        )
//                    }
//                }
//        }
//        .flowOn(Dispatchers.Default)
//
//    private val _dataState = MutableLiveData<FeedModelState>()
//    val dataState: LiveData<FeedModelState>
//        get() = _dataState
//
//    private val _attachment = MutableLiveData(noAttachment)
//    val attachment: LiveData<AttachmentModel>
//        get() = _attachment
//
//    private val _attachmentType = MutableLiveData<AttachmentType?>()
//    val attachmentType: LiveData<AttachmentType?>
//        get() = _attachmentType
//
//    private val _coords = MutableLiveData<Coordinates?>()
//    val coords: LiveData<Coordinates?>
//        get() = _coords
//
//    private val _edited = MutableLiveData(empty)
//    val edited: LiveData<Event>
//        get() = _edited
//
//    private val _eventSaveResult = MutableLiveData<Boolean>()
//    val eventSaveResult: LiveData<Boolean>
//        get() = _eventSaveResult
//
////    private val _postCreated = SingleLiveEvent<Unit>()
////    val postCreated: LiveData<Unit>
////        get() = _postCreated
//
//    //
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
//    fun save() {
//        println("save in viewmodel")
//        val event = _edited.value ?: return
//        println("save in viewmodel2")
//        viewModelScope.launch {
//            try {
//                _dataState.value = FeedModelState(loading = true)
//                println(_attachment.value)
//                when (_attachment.value?.isEmpty) {
//                    true -> {
//                        println("before repos save")
//                        repository.save(event)
//                    }
//                    else -> _attachment.value?.file?.let { file ->
//                        repository.saveWithAttachment(
//                            event,
//                            file,
//                            _edited.value!!.attachment!!.type
//                        )
//                    }
//                }
//                _dataState.value = FeedModelState()
//                _edited.value = empty // Сбрасываем состояние после сохранения
//                _eventSaveResult.postValue(true) // Успешное сохранение
//            } catch (e: Exception) {
//                _dataState.value = FeedModelState(error = true)
//                _eventSaveResult.postValue(false) // Ошибка сохранения
//            }
//        }
//    }
//
//    fun edit(event: Event) {
//        _edited.value = event
//        event.attachment?.let {
//
//        }
//    }
//
//    fun changeContent(content: String) {
//        val text = content.trim()
//        if (edited.value?.content == text) {
//            return
//        }
//        _edited.value = edited.value?.copy(content = text)
//    }
//
//    fun changeDatetime(datetime: String) {
//        _edited.value = edited.value?.copy(datetime = datetime)
//    }
//
//    fun changeType(type: EventType) {
//        _edited.value = edited.value?.copy(type = type)
//    }
//
//    fun changeSpeackers(speakers: List<Int>) {
//        _edited.value = edited.value?.copy(speakerIds = speakers)
//    }
//
//    fun changeAttachment(uri: Uri?, file: File?) {
//        _attachment.value = AttachmentModel(uri, file)
//        _edited.value = _edited.value?.copy(
//            attachment = if (uri != null && file != null && _attachmentType.value != null) {
//                Attachment(
//                    uri.toString(),
//                    _attachmentType.value!!
//                ) // Укажите нужный `AttachmentType`
//            } else {
//                null
//            }
//        )
//    }
//
//    fun changeAttachmentType(type: AttachmentType?) {
//        if (_attachmentType.value != type) {
//            _attachmentType.value = type
//            _attachment.value = null // Сбрасываем текущее вложение, если тип меняется
//        }
//    }
//
//    fun dropAttachment() {
//        _attachment.value = null
//        _attachmentType.value = null // Тип также сбрасывается
//        _edited.value = _edited.value?.copy(attachment = null)
//    }
//
//    fun setCoords(lat: Double, long: Double) {
//        _coords.value = Coordinates(lat, long)
//        _edited.value = _edited.value?.copy(coords = _coords.value)
//    }
//
//}