package ru.netology.nmedia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentMapBinding
import ru.netology.nmedia.dto.Coordinates
import ru.netology.nmedia.viewmodel.PostViewModel

//import ru.netology.nmedia.viewmodel.EventViewModel

class MapsFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var map: Map
    private lateinit var placemark: PlacemarkMapObject

    private val source: String? by lazy {
        arguments?.getString("source")
    }

//    val viewModel: EventViewModel by activityViewModels()
    val viewModel: PostViewModel by activityViewModels()

    private val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {
            removePlacemarkFromMap()
            showPlacemarkOnMap(point.latitude, point.longitude)
        }

        override fun onMapLongTap(map: Map, point: Point) = Unit
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        MapKitFactory.initialize(requireContext())

        val binding = FragmentMapBinding.inflate(inflater, container, false)
        mapView = binding.mapView
        map = mapView.mapWindow.map


        // Устанавливаем стартовый центр карты и метку
        val initialCoords = arguments?.getParcelable<Coordinates>("coords")
        val latitude = initialCoords?.lat ?: defaultLatitude
        val longitude = initialCoords?.long ?: defaultLongitude

        showPlacemarkOnMap(latitude, longitude)
        map.move(
            CameraPosition(
                Point(latitude, longitude),
                14.0f,
                0.0f,
                0.0f
            )
        )
        map.addInputListener(inputListener)
        return binding.root
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        mapView.onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    private fun showPlacemarkOnMap(latitude: Double, longitude: Double) {
        val imageProvider =
            ImageProvider.fromResource(requireContext(), R.drawable.ic_location_pin_png_24dp)

        placemark = map.mapObjects.addPlacemark().apply {
            geometry = Point(latitude, longitude)
            setIcon(imageProvider)
        }
    }

    private fun removePlacemarkFromMap() {
        map.mapObjects.remove(placemark)

    }

    fun saveCoordinates() {
        // Проверяем, есть ли текущая метка
        if (::placemark.isInitialized) {
            val point = placemark.geometry
            viewModel.setCoordinates(point.latitude, point.longitude)
            findNavController().navigateUp() // Возвращаемся на предыдущий фрагмент
        }
    }

    // Метод для получения координат
    fun getPlacemarkCoordinates(): Pair<Double, Double>? {
        return if (::placemark.isInitialized) {
            val point = placemark.geometry
            Pair(point.latitude, point.longitude)
        } else {
            null // Если метка не установлена
        }
    }

    companion object {
        val defaultLatitude = 55.751244
        val defaultLongitude = 37.618423
    }
}