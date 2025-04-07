package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Coordinates
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.enumeration.EventType
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.model.AttachmentModel
import ru.netology.nmedia.repository.BaseRepository
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import java.time.Instant
import javax.inject.Inject

private val emptyEvent = Event(
    id = 0,
    author = "",
    authorId = 0,
    authorJob = null,
    authorAvatar = null,
    content = "",
    datetime = "",
    published = "",
    coords = null,
    type = EventType.ONLINE,
    likeOwnerIds = null,
    likedByMe = false,
    speakerIds = null,
    participantsIds = null,
    participatedByMe = false,
    attachment = null,
    link = null,
    ownedByMe = false,
)

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: BaseRepository<Event>,
    private val appAuth: AppAuth
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val data: Flow<PagingData<Event>> = appAuth.state
        .flatMapLatest { auth ->
            repository.data.map { pagingData ->
                pagingData.map { event ->
                    event.copy(ownedByMe = event.authorId == auth?.id)
                }
            }
        }
        .flowOn(Dispatchers.Default)


    private val _event = MutableLiveData<Event?>()
    val event: LiveData<Event?> get() = _event

    // Текущий редактируемый пост
    private val _edited = MutableLiveData(emptyEvent)
    val edited: LiveData<Event> get() = _edited

    // Текущее вложение (локальный файл и его URI)
    private val _attachment = MutableLiveData<AttachmentModel?>(null)
    val attachment: LiveData<AttachmentModel?> get() = _attachment

    // Текущий тип вложения
    private val _attachmentType = MutableLiveData<AttachmentType?>(null)
    val attachmentType: LiveData<AttachmentType?> get() = _attachmentType

    private val _errorMessage = SingleLiveEvent<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _eventCreated = SingleLiveEvent<Unit>()
    val eventCreated: LiveData<Unit>
        get() = _eventCreated

    fun changeAttachmentType(type: AttachmentType) {
        _attachmentType.value = type
    }

    fun changeAttachment(uri: Uri, file: File) {
        _attachment.value = AttachmentModel(uri, file)
    }

    fun dropAttachment() {
        _attachment.value = null
        _attachmentType.value = null
    }

    fun setCoordinates(lat: Double, long: Double) {
        _edited.value = edited.value?.copy(coords = Coordinates(lat, long))
    }

    fun updateSpeakerIds(speakerIds: List<Long>?) {
        _edited.value = _edited.value?.copy(speakerIds = speakerIds)
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        _edited.value = edited.value?.copy(content = text)
    }

    fun changeDatetime(itemDatetime: String) {
        _edited.value = edited.value?.copy(datetime = itemDatetime)
    }

    fun changeType(eventType: EventType) {
        _edited.value = edited.value?.copy(type = eventType)
    }

    fun setError(errorMessage: String) {
        println("Viewmodel set error")
        _errorMessage.value = errorMessage
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun saveEvent() {
        val currentEvent = _edited.value ?: return
        _eventCreated.value = Unit
        viewModelScope.launch {
            try {
                val attachment = _attachment.value
                val uploadedAttachment = if (attachment != null) {
                    val uploaded = attachment.file?.let { repository.upload(it) }
                    uploaded?.let {
                        Attachment(
                            it.url,
                            _attachmentType.value ?: AttachmentType.IMAGE
                        )
                    }
                } else {
                    null
                }
                val eventToSave = currentEvent.copy(
                    attachment = uploadedAttachment,
                    published = Instant.now().toString(),
                )
                repository.save(eventToSave)
                _edited.value = emptyEvent
                _attachment.value = null
                _attachmentType.value = null
            } catch (e: ApiError) {
                println("Ошибка API: ${e.code} ${e.message}")
                _errorMessage.postValue("Ошибка сервера ${e.code}: ${e.message}")
            } catch (e: NetworkError) {
                println("Ошибка сети")
                _errorMessage.postValue("Ошибка сети. Проверьте подключение к интернету.")
            } catch (e: UnknownError) {
                println("Неизвестная ошибка")
                _errorMessage.postValue("Произошла неизвестная ошибка.")
            } catch (e: Exception) {
                println("Ошибка в save: ${e::class.simpleName} - ${e.message}")
                println("Другая ошибка: ${e.localizedMessage}")
                _errorMessage.postValue("Ошибка: ${e.localizedMessage}")
            }
        }
    }

    fun likeById(event: Event) {
        viewModelScope.launch {
            try {
                if (event.likedByMe) {
                    repository.dislikeById(event.id)
                } else {
                    repository.likeById(event.id)
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Ошибка: ${e.localizedMessage ?: "Неизвестная ошибка"}")
            }
        }
    }

    fun edit(event: Event) {
        _edited.value = event
        // Если у поста есть вложение, добавляем его в LiveData
        event.attachment?.let { attachment ->
            _attachment.value = AttachmentModel(uri = Uri.parse(attachment.url))
            _attachmentType.value = attachment.type
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            repository.delete(id)
        } catch (e: Exception) {
            _errorMessage.postValue("Ошибка: ${e.localizedMessage ?: "Неизвестная ошибка"}")
        }

    }

    fun loadEvent(id: Long) {
        viewModelScope.launch {
            val event = repository.getById(id)
            val auth = appAuth.state.value
            _event.value = event.copy(ownedByMe = event.authorId == auth?.id)
        }
    }

}