package `in`.emergencyWbApp.dashboard


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import `in`.emergencyWbApp.dashboard.screens.ContactsFragment
import `in`.emergencyWbApp.dashboard.screens.DashboardFragment
import `in`.emergencyWbApp.dashboard.screens.FirstAidFragment


class DashboardPagerAdapter(
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {

        return when (position) {

            0 -> DashboardFragment()

            1 -> ContactsFragment()

            2 -> FirstAidFragment()

            else -> DashboardFragment()
        }
    }
}