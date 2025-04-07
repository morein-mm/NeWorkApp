//package ru.netology.nmedia.adapter
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import androidx.viewpager2.adapter.FragmentStateAdapter
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import ru.netology.nmedia.activity.JobsFeedFragment
//import ru.netology.nmedia.ui.PostsFeedFragment
//
//class ViewPagerAdapter(
//    fragment: Fragment,
//    private val userId: Long,
//) : FragmentStateAdapter(fragment) {
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    override fun createFragment(position: Int): Fragment {
//        return when (position) {
//            0 -> PostsFeedFragment().apply {
//                arguments = Bundle().apply {
//                    putLong("userId", userId)
//                }
//            }
//            1 -> JobsFeedFragment().apply {
//                arguments = Bundle().apply {
//                    putLong("userId", userId)
//                }
//            }
//            else -> PostsFeedFragment().apply {
//                arguments = Bundle().apply {
//                    putLong("userId", userId)
//                }
//            }
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return 2
//    }
//}