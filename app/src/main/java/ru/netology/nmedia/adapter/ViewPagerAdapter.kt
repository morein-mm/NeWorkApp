package ru.netology.nmedia.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.netology.nmedia.activity.UserJobsFragment
import ru.netology.nmedia.ui.UserPostsFragment

class ViewPagerAdapter(
    fragment: Fragment,
    private val userId: Long,
) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> UserPostsFragment.newInstance(userId)
        1 -> UserJobsFragment.newInstance(userId)
//        1 -> UserPostsFragment.newInstance(userId)
        else -> error("Unknown position $position")
    }

    override fun getItemCount(): Int {
        return 2
    }
}