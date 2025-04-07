package ru.netology.nmedia.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnEventInteractionListener
import ru.netology.nmedia.adapter.OnPostInteractionListener
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class PostDetailsFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth

    private lateinit var mapView: MapView
    private lateinit var map: Map
    private lateinit var placemark: PlacemarkMapObject

    private var postId: Long? = null

    private val viewModel: PostViewModel by activityViewModels()
    private val viewModelAuth: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = CardPostBinding.inflate(
            inflater,
            container,
            false
        )

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
        callback.isEnabled = true

        // Получаем postId из переданного Bundle
        postId = arguments?.getLong("postId")

        postId?.let {
            viewModel.loadPost(it)
        }

        mapView = binding.mapView
        map = mapView.mapWindow.map


        val viewHolder = PostViewHolder(binding, object : OnPostInteractionListener {
            override fun onLike(post: Post) {
                if (viewModelAuth.isAuthorized) {
                    viewModel.likeById(post)
                } else {
                    context?.let {
                        AlertDialog.Builder(it)
                            .setMessage(getString(R.string.like_post_dialog))
                            .setTitle(getString(R.string.like_post_dialog_header))
                            .setPositiveButton(getString(R.string.sign_in)) { dialog, which ->
                                findNavController().navigate(R.id.action_global_signInFragment)
                            }
                            .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                            }
                            .create()
                            .show()
                    }
                }
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(R.id.action_global_newPostFragment)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
                findNavController().navigateUp()
            }

            override fun onShare(post: Post) {
                // Логика шаринга
            }

            override fun onPostDetails(post: Post) {
                // Ничего не делаем, мы уже на экране деталей
            }

            override fun onLink(postLink: String) {
                val fixedLink =
                    if (postLink.startsWith("http://") || postLink.startsWith("https://")) {
                        postLink
                    } else {
                        "https://$postLink"
                    }
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fixedLink))
                startActivity(intent)
            }
        }, isDetailedView = true, requireContext())


        // Подписываемся на изменения в LiveData
        viewModel.post.observe(viewLifecycleOwner) { post ->
            post?.let {
                viewHolder.bind(it)
                post.coords?.let {
                    showPlacemarkOnMap(post.coords.lat, post.coords.long)
                    // Позиционирование карты по маркеру
                    map.move(
                        CameraPosition(
                            Point(post.coords.lat, post.coords.long),
                            14.0f,
                            0.0f,
                            0.0f
                        )
                    )
                }
            }
        }


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

}