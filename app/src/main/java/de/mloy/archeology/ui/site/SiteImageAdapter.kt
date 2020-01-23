package de.mloy.archeology.ui.site

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class SiteImageAdapter(fm: FragmentManager, private var images: List<String>) :
    FragmentStatePagerAdapter(
        fm,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

    override fun getItem(position: Int): Fragment {
        return SiteImageFragment.newInstance(
            images.getOrNull(position), position
        )
    }

    override fun getCount(): Int =
        if (images.size < 4) {
            images.size + 1
        } else {
            images.size
        }
}