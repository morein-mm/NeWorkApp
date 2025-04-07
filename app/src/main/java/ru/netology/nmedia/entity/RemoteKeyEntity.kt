package ru.netology.nmedia.entity

enum class KeyType {
    AFTER,
    BEFORE
}

abstract class RemoteKeyEntity(
    open val type: KeyType,
    open val key: Long
)