package com.hktech.bluetoothprinter

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DeviceListActivity : AppCompatActivity() {

    val TAG = "TAG"
    var mBluetoothAdapter: BluetoothAdapter? = null
    private var mPairedDevicesArrayAdapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
        setContentView(R.layout.activity_detail_list)
        setResult(Activity.RESULT_CANCELED)
        mPairedDevicesArrayAdapter = ArrayAdapter<String>(this, R.layout.device_name)

        var mPairedListView = findViewById<ListView>(R.id.paired_devices)
        mPairedListView.adapter = mPairedDevicesArrayAdapter
        mPairedListView.onItemClickListener = mDeviceClickListener

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        var mPairedDevices = mBluetoothAdapter!!.bondedDevices

        if (mPairedDevices.isNotEmpty()) {
            findViewById<TextView>(R.id.title_paired_devices).visibility = View.VISIBLE
            for (mDevice in mPairedDevices) {
                mPairedDevicesArrayAdapter!!.add(mDevice.name + "\n" + mDevice.address)
            }
        } else {
            var mNoDevices = "None Paired"
            mPairedDevicesArrayAdapter!!.add(mNoDevices)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter!!.cancelDiscovery()
        }
    }

    private var mDeviceClickListener =
        OnItemClickListener { _: AdapterView<*>, mView: View, _: Int, _: Long ->
            try {
                mBluetoothAdapter!!.cancelDiscovery();
                var mDeviceInfo = (mView as TextView).text.toString()
                var mDeviceAddress = mDeviceInfo.substring(mDeviceInfo.length - 17);
                Log.v(TAG, "Device_Address $mDeviceAddress");

                var mBundle = Bundle()
                mBundle.putString("DeviceAddress", mDeviceAddress);
                var mBackIntent = Intent()
                mBackIntent.putExtras(mBundle);
                setResult(RESULT_OK, mBackIntent);
                finish()
            } catch (ex: Exception) {

            }
        }
}