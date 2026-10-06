package `in`.emergencyWbApp.dashboard




import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : FragmentActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)

        viewPager = findViewById(R.id.viewPager)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        val adapter = DashboardPagerAdapter(this)

        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 2

        viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    when (position) {
                        0 -> bottomNavigation.selectedItemId =
                            R.id.nav_dashboard

                        1 -> bottomNavigation.selectedItemId =
                            R.id.nav_contacts

                        2 -> bottomNavigation.selectedItemId =
                            R.id.nav_first_aid
                    }
                }
            }
        )

        bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_dashboard -> {
                    viewPager.currentItem = 0
                    true
                }

                R.id.nav_contacts -> {
                    viewPager.currentItem = 1
                    true
                }

                R.id.nav_first_aid -> {
                    viewPager.currentItem = 2
                    true
                }

                else -> false
            }
        }
    }
}