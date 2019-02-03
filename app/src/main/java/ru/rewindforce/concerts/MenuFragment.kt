package ru.rewindforce.concerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_menu.*
import ru.rewindforce.concerts.AddConcert.AddConcertFragment
import ru.rewindforce.concerts.HomepageActivity.Companion.MENU_FRAGMENT
import ru.rewindforce.concerts.HomepageActivity.Companion.PROFILE_FRAGMENT
import ru.rewindforce.concerts.authorization.AuthorizationActivity.PREF_LOGIN
import ru.rewindforce.concerts.authorization.AuthorizationActivity.PREF_UID
import ru.rewindforce.concerts.profile.ProfileFragment
import ru.rewindforce.concerts.utils.*

class MenuFragment: BaseFragment(R.layout.fragment_menu, true) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val login = getStringPref(PREF_LOGIN)
        val uid = getStringPref(PREF_UID)
        my_profile.setOnClickListener { openChildFragment(PROFILE_FRAGMENT,
                                        ProfileFragment.newInstance(uid ?: "", login ?: ""),
                                        childContainer.id) }
        add_concert.setOnClickListener { openChildFragment("add_concert",
                                         AddConcertFragment.newInstance(), childContainer.id) }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MenuFragment()
    }
}
