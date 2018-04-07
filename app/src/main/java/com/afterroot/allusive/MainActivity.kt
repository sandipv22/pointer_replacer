/*
 * Copyright (C) 2016-2017 Sandip Vaghela
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.afterroot.allusive

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.*
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.afterroot.allusive.adapter.BottomNavigationAdapter
import com.afterroot.allusive.fragment.InstallPointerFragment
import com.afterroot.allusive.fragment.MainFragment
import com.afterroot.allusive.fragment.SettingsFragment
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        //Replace Launch theme with Light Theme
        setTheme(R.style.AppTheme_Light)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle!!)
        toggle!!.syncState()

        //Firebase Analytics, logs every time when user starts activity.
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("Device_Name", Build.DEVICE)
        bundle.putString("AndroidVersion", Build.VERSION.RELEASE)
        analytics.logEvent("DeviceInfo", bundle)

        //Initialize Interstitial Ad
        MobileAds.initialize(this, getString(R.string.interstitial_ad_1_id))

        nav_view.setNavigationItemSelectedListener(this)

        init()
    }

    private var mainFragment: MainFragment? = null
    private fun init() {
        val arrowDrawable = toggle!!.drawerArrowDrawable
        arrowDrawable.color =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    resources.getColor(android.R.color.white, theme)
                } else {
                    resources.getColor(android.R.color.white)
                }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nav_view.apply {
                itemIconTintList = resources.getColorStateList(R.color.nav_state_list, theme)
                itemTextColor = resources.getColorStateList(R.color.nav_state_list, theme)
            }
            checkPermissions()
        } else {
            nav_view.apply {
                itemIconTintList = resources.getColorStateList(R.color.nav_state_list)
                itemTextColor = resources.getColorStateList(R.color.nav_state_list)
            }
            if (mainFragment == null) {
                mainFragment = MainFragment.newInstance()
            }
            /*Handler().postDelayed({
                addFragment(mainFragment!!, R.id.fragment_container)
            }, 100)*/
        }

        view_pager.setPagingEnabled(false)
        val viewpagerAdapter = BottomNavigationAdapter(supportFragmentManager)
        val installPointerFragment = InstallPointerFragment.newInstance()
        val settingsFragment = SettingsFragment()

        viewpagerAdapter.run {
            addFragment(mainFragment!!)
            addFragment(installPointerFragment)
            addFragment(settingsFragment)
        }

        view_pager.adapter = viewpagerAdapter

        //BottomNavigation Click Listener
        navigation.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.navigation_home -> {
                    //replaceFragment(MainFragment.newInstance(), R.id.fragment_container)
                    view_pager.currentItem = 0
                }
                R.id.navigation_manage_pointer -> {
                    //replaceFragment(InstallPointerFragment.newInstance(), R.id.fragment_container)
                    view_pager.currentItem = 1
                }
                R.id.navigation_settings -> {
                    //replaceFragment(SettingsFragment(), R.id.fragment_container)
                    view_pager.currentItem = 2
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    private val manifestPermissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private fun checkPermissions() {
        Log.d(TAG, "checkPermissions: Checking Permissions..")
        val permissionChecker = PermissionChecker(this)
        if (permissionChecker.lacksPermissions(manifestPermissions)) {
            Log.d(TAG, "checkPermissions: Requesting Permissions..")
            ActivityCompat.requestPermissions(this, manifestPermissions, REQUEST_CODE)
        } else {
            Log.d(TAG, "checkPermissions: Permissions Granted..")
            if (mainFragment == null) {
                mainFragment = MainFragment.newInstance()
            }
            addFragment(mainFragment!!, R.id.fragment_container)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE -> {
                val isPermissionGranted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (!isPermissionGranted) {
                    Log.d(TAG, "onRequestPermissionsResult: Permissions not Granted..")
                    Helper.showSnackBar(this.coordinator_layout, "Please Grant Permissions", Snackbar.LENGTH_INDEFINITE, "GRANT", View.OnClickListener { checkPermissions() })
                } else {
                    checkPermissions()
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.reboot -> showRebootDialog()
        }
        return false
    }

    private fun showRebootDialog() {
        AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                .setTitle(R.string.reboot)
                .setMessage(R.string.text_reboot_confirm)
                .setPositiveButton(R.string.reboot, { _, _ ->
                    try {
                        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "reboot"))
                        process.waitFor()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })
                .setNegativeButton(R.string.text_no, { _, _ ->

                })
                .setNeutralButton(R.string.text_soft_reboot, { _, _ ->
                    try {
                        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "busybox killall system_server"))
                        process.waitFor()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }).show()
    }

    companion object {
        var toggle: ActionBarDrawerToggle? = null
        private val REQUEST_CODE: Int = 256

        fun showInstallPointersDialog(context: Context) {
            MaterialDialog.Builder(context)
                    .title("Install Pointers")
                    .content("No Pointers installed. Please install some pointers")
                    .positiveText("Install Pointers")
                    .onPositive { _: MaterialDialog, _: DialogAction ->
                        TODO("Add Fragment")
                    }.show()
        }

        fun showInstallPointerFragment(activity: FragmentActivity) {
            activity.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, InstallPointerFragment()).commit()
        }
    }

    /**
     * @see <a href="https://medium.com/thoughts-overflow/how-to-add-a-fragment-in-kotlin-way-73203c5a450b">Source: How to Add a Fragment the Kotlin way</a></p>
     */
    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }

    private fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int, func: (FragmentTransaction.() -> FragmentTransaction)? = null) {
        Log.d(TAG, "addFragment: adding ${fragment.javaClass.simpleName}")
        if (func != null) {
            supportFragmentManager.inTransaction { add(frameId, fragment).func() }
        } else {
            supportFragmentManager.inTransaction { add(frameId, fragment) }
        }
    }

    private fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int, func: (FragmentTransaction.() -> FragmentTransaction)? = null) {
        Log.d(TAG, "replaceFragment: replacing fragment with ${fragment.javaClass.simpleName}")
        if (func != null) {
            supportFragmentManager.inTransaction { replace(frameId, fragment).func() }
        } else {
            supportFragmentManager.inTransaction { replace(frameId, fragment) }
        }
    }
}