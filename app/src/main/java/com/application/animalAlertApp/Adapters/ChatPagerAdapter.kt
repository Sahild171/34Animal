package com.application.animalAlertApp.Adapters


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.application.animalAlertApp.Fragments.TitleChatFragment
import com.application.animalAlertApp.Fragments.TitleFragment

class ChatPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        if (position == 0) {
            fragment = TitleFragment()
        } else if (position == 1) {
            fragment = TitleChatFragment()
        }
        return fragment!!
    }

}