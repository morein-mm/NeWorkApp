//package ru.netology.nmedia.activity
//
//import android.os.Bundle
//import android.view.Menu
//import android.view.MenuItem
//import android.view.View
//import android.widget.Toast
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.navigation.findNavController
//import androidx.navigation.fragment.NavHostFragment
//import androidx.navigation.fragment.findNavController
//import androidx.navigation.ui.AppBarConfiguration
//import androidx.navigation.ui.setupWithNavController
//import com.google.android.material.bottomnavigation.BottomNavigationView
//import com.google.android.material.snackbar.Snackbar
//import com.yandex.mapkit.MapKitFactory
//import dagger.hilt.android.AndroidEntryPoint
//import ru.netology.nmedia.BuildConfig
//import ru.netology.nmedia.R
//import ru.netology.nmedia.activity.ProfileFragment.Companion.userId
//import ru.netology.nmedia.ui.UsersFeedFragment.Companion.viewType
//import ru.netology.nmedia.auth.AppAuth
//import ru.netology.nmedia.enumeration.ViewType
//import ru.netology.nmedia.viewmodel.AuthViewModel
//import ru.netology.nmedia.viewmodel.EventViewModel
//import ru.netology.nmedia.viewmodel.JobViewModel
//import ru.netology.nmedia.viewmodel.PostViewModel
//import ru.netology.nmedia.viewmodel.UserViewModel
//import javax.inject.Inject
//
//@AndroidEntryPoint
//class AppActivity_old : AppCompatActivity(R.layout.activity_app) {
//
//    @Inject
//    lateinit var appAuth: AppAuth
//
//    private val viewModelUsers: UserViewModel by viewModels()
//    private val viewModelJobs: JobViewModel by viewModels()
//    private val viewModelEvents: EventViewModel by viewModels()
//    private val viewModelPosts: PostViewModel by viewModels()
//
//    private val viewModelAuth: AuthViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//
//        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
//        super.onCreate(savedInstanceState)
//
//        val toolbar: Toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)
//
//        // Получаем NavController из NavHostFragment
//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        val navController = navHostFragment.navController
//
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(R.id.feedFragment, R.id.events_feed, R.id.users_feed) // Три основных экрана
//        )
//
//        toolbar.setupWithNavController(navController, appBarConfiguration)
//
//        // Привязываем BottomNavigationView к NavController
//        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
//        bottomNavigationView.setupWithNavController(navController)
//
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            invalidateOptionsMenu() // Обновление меню при смене экрана
//            when (destination.id) {
//                R.id.feedFragment, R.id.events_feed -> {
//                    toolbar.title = "NeWork"
//                    bottomNavigationView.visibility = View.VISIBLE
//                }
//
//                R.id.users_feed -> {
//                    val userIds =
//                        navController.currentBackStackEntry?.arguments?.getLongArray("userIds")
//                    val viewType =
//                        try {
//                            ViewType.entries.last { it.name == navController.currentBackStackEntry?.arguments?.viewType }
//                        } catch (e: NoSuchElementException) {
//                            ViewType.FEED
//                        }
//
//                    toolbar.title =
//                        if (userIds?.isNotEmpty() == true) "Likers"
//                        else if (viewType == ViewType.FEED_WITH_SELECT) "Choose user"
//                        else "NeWork"
//                    //TODO: не выводится для Choose users, хотя условие = true
//                    supportActionBar?.setDisplayHomeAsUpEnabled(userIds?.isNotEmpty() == true || viewType == ViewType.FEED_WITH_SELECT)
//                    bottomNavigationView.visibility =
//                        if (userIds?.isNotEmpty() == true || viewType == ViewType.FEED_WITH_SELECT) View.GONE else View.VISIBLE
//                }
//
//                R.id.signInFragment -> {
//                    toolbar.title = "Login"
//                    bottomNavigationView.visibility = View.GONE
//                }
//
//                R.id.signUpFragment -> {
//                    toolbar.title = "Registration"
//                    bottomNavigationView.visibility = View.GONE
//                }
//
//                R.id.profileFragment -> {
//                    val currentUserId = viewModelAuth.auth.value?.id
//                    val userId = navController.currentBackStackEntry?.arguments?.getLong("userId")
//                    toolbar.title = if (currentUserId == userId) {
//                        "You"
//                    } else {
//                        val user = viewModelUsers.data.value?.users?.find { it.id == userId }
//                        "${user?.name} / ${user?.login}"
//                    }
//                    bottomNavigationView.visibility = View.GONE
//                }
//
//                R.id.newPostFragment -> {
//                    toolbar.title = "New post"
//                    bottomNavigationView.visibility = View.GONE
//                }
//
//                R.id.newEventFragment -> {
//                    toolbar.title = "New event"
//                    bottomNavigationView.visibility = View.GONE
//                }
//
//                R.id.mapsFragment -> {
//                    toolbar.title = "Location"
//                    bottomNavigationView.visibility = View.GONE
//                }
//
//                R.id.postDetailsFragment -> {
//                    toolbar.title = "Post"
//                    bottomNavigationView.visibility = View.GONE
//                }
//
//                else -> {
//                    toolbar.title = "ELSE"
//                    bottomNavigationView.visibility = View.GONE
//                }
//            }
//        }
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment)
//        val currentDestination = navController.currentDestination?.id
//
//        // Очистка текущего меню
//        menu?.clear()
//
//        // Выбор меню в зависимости от текущего экрана
//
//        when (currentDestination) {
//            R.id.profileFragment -> {
//                val userId = navController.currentBackStackEntry?.arguments?.getLong("userId")
//                val isCurrentUserProfile = userId == viewModelAuth.auth.value?.id
//                if (isCurrentUserProfile) {
//                    menuInflater.inflate(R.menu.logout_menu, menu)
//                }
//            }
//
//            R.id.newPostFragment, R.id.newEventFragment, R.id.mapsFragment -> {
//                menuInflater.inflate(R.menu.save_menu, menu)
//            }
//
//            R.id.users_feed -> {
//                val userIds =
//                    navController.currentBackStackEntry?.arguments?.getLongArray("userIds")
//
//                val viewType =
//                    ViewType.entries.lastOrNull {
//                        it.name == findNavController(R.id.nav_host_fragment)
//                            .currentBackStackEntry?.arguments?.viewType
//                    }
//
//                if (viewType == ViewType.FEED_WITH_SELECT) {
//                    menuInflater.inflate(R.menu.save_menu, menu)
////                    TODO: Уточнить, корректна ли такая проверка (именно на null)
//                } else if (userIds == null) {
////                    TODO: Этот кусок кода используется дважды - вынести в функцию?
//                    menuInflater.inflate(R.menu.main_menu, menu)
//                    // Подписка на изменения состояния авторизации
//                    viewModelAuth.auth.observe(this) {
//                        val isAuthorized = viewModelAuth.isAuthorized
//
//                        // Устанавливаем видимость пунктов меню в зависимости от состояния
//                        menu?.findItem(R.id.action_login)?.isVisible = !isAuthorized
//                        menu?.findItem(R.id.action_register)?.isVisible = !isAuthorized
//                        menu?.findItem(R.id.action_profile)?.isVisible = isAuthorized
//                    }
//                }
//            }
//
//            else -> {
//                menuInflater.inflate(R.menu.main_menu, menu)
//                // Подписка на изменения состояния авторизации
//                viewModelAuth.auth.observe(this) {
//                    val isAuthorized = viewModelAuth.isAuthorized
//
//                    // Устанавливаем видимость пунктов меню в зависимости от состояния
//                    menu?.findItem(R.id.action_login)?.isVisible = !isAuthorized
//                    menu?.findItem(R.id.action_register)?.isVisible = !isAuthorized
//                    menu?.findItem(R.id.action_profile)?.isVisible = isAuthorized
//                }
//            }
//        }
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.action_login -> {
//                findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_signInFragment)
//                true
//            }
//
//            R.id.action_register -> {
//                findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_signUpFragment)
//                true
//            }
//
//            R.id.action_profile -> {
//                val bundle = Bundle().apply {
//                    viewModelAuth.auth.value?.let {
//                        putLong(
//                            "userId",
//                            it.id
//                        )
//                    }
//                }
//                findNavController(R.id.nav_host_fragment).navigate(
//                    R.id.action_global_profileFragment,
//                    bundle
//                )
//                true
//            }
//
//            R.id.action_logout -> {
//                appAuth.clearAuth() // Выполняем выход пользователя
//                findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_feedFragment) // Переход на стартовый экран
//                true
//            }
//
//            R.id.action_save -> {
//                val currentFragment =
//                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
//                        ?.childFragmentManager?.primaryNavigationFragment
//                if (currentFragment is MapsFragment) {
//                    currentFragment.saveCoordinates()
//                }
//
//                if (currentFragment is UsersFeedFragment) {
//                    val selectedSpeakers = viewModelUsers.selectedUsers.value.orEmpty()
//                    viewModelEvents.changeSpeackers(selectedSpeakers.map { it.toInt() })
//                    findNavController(R.id.nav_host_fragment).navigateUp() // Возвращаемся в NewEventFragment
//                }
//
//                if (currentFragment is NewEventFragment) {
//                    val content = currentFragment.getContent()
//                    if (content.isNullOrEmpty()) {
//                        Snackbar.make(findViewById(android.R.id.content), "Content cannot be empty", Snackbar.LENGTH_SHORT).show()
////                        Toast.makeText(this, "Content cannot be empty", Toast.LENGTH_SHORT).show()
//                    }
////                    else if (viewModelEvents.edited.value?.datetime.isNullOrEmpty()) {
////                        Snackbar.make(findViewById(android.R.id.content), "Date and time cannot be empty", Snackbar.LENGTH_SHORT).show()
////                    }
//                    else {
//                        currentFragment.saveEvent(content)
//                    }
//                }
//
//                true
//            }
//
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//}