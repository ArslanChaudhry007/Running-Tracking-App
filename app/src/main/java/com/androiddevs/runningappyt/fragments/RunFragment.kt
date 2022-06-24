package com.androiddevs.runningappyt.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.runningappyt.R
import com.androiddevs.runningappyt.adapters.RunAdapter
import com.androiddevs.runningappyt.others.Constants.REQUEST_CODE_BACKGROUND_LOCATION_PERMISSION
import com.androiddevs.runningappyt.others.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.androiddevs.runningappyt.others.SortType
import com.androiddevs.runningappyt.others.TrackingUtility
import com.androiddevs.runningappyt.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_run.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run), View.OnClickListener,
    EasyPermissions.PermissionCallbacks, AdapterView.OnItemSelectedListener {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var runAdapter: RunAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermission()
        setupRecyclerView()
        fab.setOnClickListener(this)
        spFilter.onItemSelectedListener = this
        when (viewModel.sortType) {
            SortType.DATE -> spFilter.setSelection(0)
            SortType.RUNNING_TIME -> spFilter.setSelection(1)
            SortType.DISTANCE -> spFilter.setSelection(2)
            SortType.AVG_SPEED -> spFilter.setSelection(3)
            SortType.CALORIES_BURNED -> spFilter.setSelection(4)
        }

        viewModel.runs.observe(viewLifecycleOwner, Observer {
            runAdapter.submitList(it)
        })

    }

    private fun setupRecyclerView() = rvRuns.apply {
        runAdapter = RunAdapter()
        adapter = runAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun requestPermission() {
        if (TrackingUtility.hasLocationPermissions(requireContext())) {
            return
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permission to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permission to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.FOREGROUND_SERVICE
            )

        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestBackgroundPermission() {
        if (TrackingUtility.hasBackgroundPermissions(requireContext())) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept background location permission to use this app",
                REQUEST_CODE_BACKGROUND_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab -> {
                findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
            }

        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        if (TrackingUtility.hasLocationPermissions(requireContext())) {
            requestBackgroundPermission()
        }


    }

    override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        when (pos) {
            0 -> viewModel.sortRuns(SortType.DATE)
            1 -> viewModel.sortRuns(SortType.RUNNING_TIME)
            2 -> viewModel.sortRuns(SortType.DISTANCE)
            3 -> viewModel.sortRuns(SortType.AVG_SPEED)
            4 -> viewModel.sortRuns(SortType.CALORIES_BURNED)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}