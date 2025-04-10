package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.ui.EventNewEditFragment
import ru.netology.nmedia.ui.MapsFragment
import ru.netology.nmedia.ui.PostNewEditFragment
import ru.netology.nmedia.ui.UsersFeedFragment
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.EventViewModel
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.viewmodel.UserViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.activity_app) {

    @Inject
    lateinit var appAuth: AppAuth


    private val viewModelAuth: AuthViewModel by viewModels()
    private val viewModelPosts: PostViewModel by viewModels()
    private val viewModelEvents: EventViewModel by viewModels()
    private val viewModelUsers: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Получаем NavController из NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.feedFragment, // Экран списка постов
                R.id.events_feed, // Экран списка событий
                R.id.users_feed, // Экран списка пользователей
            )
        )

        toolbar.setupWithNavController(navController, appBarConfiguration)

        // Привязываем BottomNavigationView к NavController
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.setOnItemReselectedListener { /* Ничего не делаем, чтобы не пересоздавался фрагмент */ }

        navController.addOnDestinationChangedListener { _, destination, arguments ->
            invalidateOptionsMenu() // Обновление меню при смене экрана
            when (destination.id) {
                R.id.feedFragment, R.id.events_feed -> {
                    toolbar.title = getString(R.string.app_name)
                    bottomNavigationView.visibility = View.VISIBLE
                }

                R.id.users_feed -> {
                    val isSelectionMode = arguments?.getBoolean("isSelectionMode", false) ?: false
                    if (isSelectionMode) {
                        toolbar.title = getString(R.string.choose_users)
                        bottomNavigationView.visibility = View.GONE
                        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Показать кнопку назад
                    } else {
                        toolbar.title = getString(R.string.app_name)
                        bottomNavigationView.visibility = View.VISIBLE
                    }
                }

                R.id.signInFragment -> {
                    toolbar.title = getString(R.string.login)
                    bottomNavigationView.visibility = View.GONE
                }

                R.id.newPostFragment -> {
                    toolbar.title = getString(R.string.new_post)
                    bottomNavigationView.visibility = View.GONE
                }

                R.id.eventNewEditFragment -> {
                    toolbar.title = getString(R.string.new_event)
                    bottomNavigationView.visibility = View.GONE
                }

                R.id.postDetailsFragment -> {
                    toolbar.title = getString(R.string.post)
                    bottomNavigationView.visibility = View.GONE
                }

                R.id.eventDetailsFragment -> {
                    toolbar.title = getString(R.string.event)
                    bottomNavigationView.visibility = View.GONE
                }

                R.id.mapsFragment -> {
                    toolbar.title = getString(R.string.select_location)
                    bottomNavigationView.visibility = View.GONE
                }

                R.id.profileFragment -> {
                    // Заголовок будет обновляться в самом ProfileFragment
                    bottomNavigationView.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val currentDestination = findNavController(R.id.nav_host_fragment).currentDestination?.id

        if (currentDestination == R.id.signInFragment) {
            return false // Не показываем меню для SignInFragment
        }

        menu?.clear() // Очищаем меню перед добавлением нового

        when (currentDestination) {
            R.id.newPostFragment, R.id.mapsFragment, R.id.eventNewEditFragment -> {
                menuInflater.inflate(R.menu.save_menu, menu) // Показываем меню сохранения
            }

            R.id.users_feed -> {
                // Получаем isSelectionMode из аргументов фрагмента
                val isSelectionMode =
                    findNavController(R.id.nav_host_fragment).currentBackStackEntry?.arguments?.getBoolean(
                        "isSelectionMode"
                    ) ?: false
                if (isSelectionMode) {
                    menuInflater.inflate(R.menu.save_menu, menu) // Показываем меню Save
                } else {
                    menuInflater.inflate(R.menu.main_menu, menu) // Показываем обычное меню
                    // Подписка на изменения состояния авторизации
                    viewModelAuth.auth.observe(this) {
                        val isAuthorized = viewModelAuth.isAuthorized

                        menu?.findItem(R.id.action_login)?.isVisible = !isAuthorized
                        menu?.findItem(R.id.action_register)?.isVisible = !isAuthorized
                        menu?.findItem(R.id.action_profile)?.isVisible = isAuthorized
                    }
                }
            }

            R.id.postDetailsFragment, R.id.eventDetailsFragment -> {
                menuInflater.inflate(R.menu.share_menu, menu) // Показываем меню share
            }

            R.id.profileFragment -> {
                viewModelUsers.user.observe(this) { user ->
                    if (user != null) {
                        val isCurrentUserProfile = user.id == viewModelAuth.auth.value?.id
                        if (isCurrentUserProfile) {
                            menuInflater.inflate(R.menu.logout_menu, menu)
                        }
                    }
                }
            }

            else -> {
                menuInflater.inflate(R.menu.main_menu, menu) // Обычное меню
                // Подписка на изменения состояния авторизации
                viewModelAuth.auth.observe(this) {
                    val isAuthorized = viewModelAuth.isAuthorized

                    menu?.findItem(R.id.action_login)?.isVisible = !isAuthorized
                    menu?.findItem(R.id.action_register)?.isVisible = !isAuthorized
                    menu?.findItem(R.id.action_profile)?.isVisible = isAuthorized
                }
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_login -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_signInFragment)
                true
            }

            R.id.action_register -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_signUpFragment)
                true
            }

            R.id.action_profile -> {
                val bundle = Bundle().apply {
                    viewModelAuth.auth.value?.let {
                        putLong(
                            "userId",
                            it.id
                        )
                    }
                }
                findNavController(R.id.nav_host_fragment).navigate(
                    R.id.action_global_profileFragment,
                    bundle
                )
                true
            }

            R.id.action_logout -> {
                appAuth.clearAuth()
                findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_feedFragment)
                true
            }

            R.id.action_save -> { // Обработка кнопки Save
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.childFragmentManager?.primaryNavigationFragment
                println("Current fragment: $currentFragment")

                when (currentFragment) {
                    is PostNewEditFragment -> {
                        val isContentValid = currentFragment.changeContent()
                        if (isContentValid) {
                            viewModelPosts.savePost()
                        }
                    }

                    is EventNewEditFragment -> {
                        val isContentValid = currentFragment.changeContent()
                        if (isContentValid) {
                            viewModelEvents.saveEvent()
                        }
                    }

                    is MapsFragment -> {
                        // Получаем координаты из MapsFragment
                        val coordinates = currentFragment.getPlacemarkCoordinates()
                        val source = currentFragment.arguments?.getString("source")

                        coordinates?.let { (latitude, longitude) ->
                            when (source) {
                                "post" -> viewModelPosts.setCoordinates(latitude, longitude)
                                "event" -> viewModelEvents.setCoordinates(latitude, longitude)
                                else -> {
                                    Toast.makeText(this, "Unknown source", Toast.LENGTH_SHORT).show()
                                }
                            }
                            findNavController(R.id.nav_host_fragment).popBackStack()
                        } ?: run {
                            // Если метка не установлена
                            Toast.makeText(
                                this,
                                getString(R.string.set_placemark),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    is UsersFeedFragment -> {
                        val selectedUsers = currentFragment.getSelectedUsers()
                        val source = currentFragment.arguments?.getString("source")
                        when (source) {
                            "post" -> viewModelPosts.updateMentionedIds(if (selectedUsers.isNotEmpty()) selectedUsers else null)
                            "event" -> viewModelEvents.updateSpeackerIds(if (selectedUsers.isNotEmpty()) selectedUsers else null)
                            else -> {
                                Toast.makeText(this, "Unknown source", Toast.LENGTH_SHORT).show()
                            }
                        }
                        findNavController(R.id.nav_host_fragment).popBackStack()
                    }

                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}