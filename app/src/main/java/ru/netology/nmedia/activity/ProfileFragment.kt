package ru.netology.nmedia.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentProfileBinding
import ru.netology.nmedia.util.LongArg
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.viewmodel.UserViewModel
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth
    private val viewModel: AuthViewModel by activityViewModels()
    private val viewModelUser: UserViewModel by activityViewModels()

    //    private val viewModelJob: JobViewModel by activityViewModels()
    private val viewModelPost: PostViewModel by activityViewModels()

    companion object {
        var Bundle.userId: Long by LongArg
    }


//    override fun onDestroy() {
//        super.onDestroy()
//        viewModelJob.setUserId(0)
//        viewModelPost.setUserId(0)
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentProfileBinding.inflate(
            inflater,
            container,
            false
        )


        Log.d("ProfileFragment", "Profile fragment loaded")
//        val userId = arguments?.userId ?: 0
        val userId = arguments?.getLong("userId") ?: appAuth.state.value?.id

//        val viewPager = binding.viewPagerUser
//        val viewPagerUserAdapter = ViewPagerAdapter(this, userId)
//        viewPager.adapter = viewPagerUserAdapter
//
//        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                binding.profileTabs.getTabAt(position)?.select()
//            }
//        })
//
//        binding.profileTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//                viewPager.currentItem = tab!!.position
//            }
//
//            override fun onTabUnselected(p0: TabLayout.Tab?) {
//            }
//
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                viewPager.currentItem = tab!!.position
//            }
//
//
//        })

//        viewModelUser.data.observe(viewLifecycleOwner) { userModel ->
//            userModel.users.find { it.id == userId }.let { user ->
//                println("USER ID = $user")
//                binding.apply {
//                    Glide.with(binding.avatar)
//                        .load("${user?.avatar}")
//                        .centerInside()
////                        .error(R.drawable.ic_error)
//                        .into(binding.avatar)
////                    toolbar.title = user?.name + " / " + user?.login
//                }
//                user?.let {
//////                    TODO: возможно, тут стоит делать загрузку работ
////                    viewModelJob.setUserId(user.id)
////                    println("IN PROFILE FRAGMENT")
////                    println(user.id)
////                    viewModelPost.setUserId(user.id)
//                }
//            }
//        }

        userId?.let { id ->
            viewModelUser.getUserById(userId)
            viewModelUser.user.observe(viewLifecycleOwner) { user ->
                user?.let {
                    Glide.with(binding.avatar)
                        .load(user.avatar)
                        .centerInside()
                        .into(binding.avatar)
//                    binding.toolbar.title = "${user.name} / ${user.login}"

                    // Определяем заголовок
                    val title = if (it.id == appAuth.state.value?.id) {
                        getString(R.string.you) // Для текущего пользователя
                    } else {
                        "${it.name} / ${it.login}" // Для другого пользователя
                    }
                    // Обновляем заголовок в AppBar
                    (activity as? AppCompatActivity)?.supportActionBar?.title = title
                }
            }
        } ?: run {
            // Если userId null, можно показать ошибку или выполнить другую логику
            // Например, логировать ошибку или показывать сообщение
        }

        return binding.root
    }


}