package ru.netology.nmedia.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Coordinates(
    val lat: Double,
    val long: Double
) : Parcelable