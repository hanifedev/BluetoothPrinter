package com.hktech.bluetoothprinter

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.io.OutputStream
import java.nio.ByteBuffer
import java.util.*


class MainActivity : AppCompatActivity(), Runnable {

    protected val TAG = "TAG"
    private val REQUEST_CONNECT_DEVICE = 1
    private val REQUEST_ENABLE_BT = 2
    var mScan: Button? = null
    var mPrint: Button? = null
    var mDisc: Button? = null
    var mBluetoothAdapter: BluetoothAdapter? = null
    private val applicationUUID: UUID = UUID
        .fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var mBluetoothConnectProgressDialog: ProgressDialog? = null
    private var mBluetoothSocket: BluetoothSocket? = null
    var mBluetoothDevice: BluetoothDevice? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        mScan = findViewById<Button>(R.id.Scan)
        mScan!!.setOnClickListener {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (mBluetoothAdapter == null) {
                Toast.makeText(this@MainActivity, "Message1", Toast.LENGTH_SHORT).show()
            } else {
                if (!mBluetoothAdapter!!.isEnabled) {
                    val enableBtIntent = Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE
                    )
                    startActivityForResult(
                        enableBtIntent,
                        REQUEST_ENABLE_BT
                    )
                } else {
                    listPairedDevices()
                    val connectIntent = Intent(
                        this@MainActivity,
                        DeviceListActivity::class.java
                    )
                    startActivityForResult(
                        connectIntent,
                        REQUEST_CONNECT_DEVICE
                    )
                }
            }
        }


        mPrint = findViewById(R.id.mPrint)
        mPrint!!.setOnClickListener {
            val t: Thread = object : Thread() {
                override fun run() {
                    try {
                        val os: OutputStream = mBluetoothSocket!!.outputStream
                        var BILL = ""
                        BILL = """                   XXXX MART    
                               XX.AA.BB.CC.     
                              NO 25 ABC ABCDE    
                              XXXXX YYYYYY      
                               MMM 590019091      
"""
                        BILL = """
                        $BILL-----------------------------------------------
                        
                        """.trimIndent()
                        BILL += String.format(
                            "%1$-10s %2$10s %3$13s %4$10s",
                            "Item",
                            "Qty",
                            "Rate",
                            "Totel"
                        )
                        BILL = """
                        $BILL
                        
                        """.trimIndent()
                        BILL = (BILL
                                + "-----------------------------------------------")
                        BILL = """$BILL
                         ${String.format("%1$-10s %2$10s %3$11s %4$10s", "item-001", "5", "10", "50.00")}"""
                                                BILL = """$BILL
                         ${String.format("%1$-10s %2$10s %3$11s %4$10s", "item-002", "10", "5", "50.00")}"""
                                                BILL = """$BILL
                         ${String.format("%1$-10s %2$10s %3$11s %4$10s", "item-003", "20", "10", "200.00")}"""
                                                BILL = """$BILL
                         ${String.format("%1$-10s %2$10s %3$11s %4$10s", "item-004", "50", "10", "500.00")}"""
                        BILL = """
                        $BILL
                        -----------------------------------------------
                        """.trimIndent()
                        BILL = "$BILL\n\n "
                        BILL = "$BILL                   Total Qty:      85\n"
                        BILL = "$BILL                   Total Value:     700.00\n"
                        BILL = """
                        $BILL-----------------------------------------------
                        
                        """.trimIndent()
                        BILL = "$BILL\n\n "
                        os.write(BILL.toByteArray())
                        //This is printer specific code you can comment ==== > Start

                        // Setting height
                        val gs = 29
                        os.write(intToByteArray(gs))
                        val h = 104
                        os.write(intToByteArray(h))
                        val n = 162
                        os.write(intToByteArray(n))

                        // Setting Width
                        val gs_width = 29
                        os.write(intToByteArray(gs_width))
                        val w = 119
                        os.write(intToByteArray(w))
                        val n_width = 2
                        os.write(intToByteArray(n_width))
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Exe ", e)
                    }
                }
            }
            t.start()
        }

        mDisc = findViewById(R.id.dis)
        mDisc!!.setOnClickListener {
            if (mBluetoothAdapter != null) mBluetoothAdapter!!.disable()
        }

    }

    override fun onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy()
        try {
            mBluetoothSocket?.close()
        } catch (e: java.lang.Exception) {
            Log.e("Tag", "Exe ", e)
        }
    }

    override fun onBackPressed() {
        try {
            mBluetoothSocket?.close()
        } catch (e: java.lang.Exception) {
            Log.e("Tag", "Exe ", e)
        }
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun onActivityResult(requestCode: Int, mResultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, mResultCode, data)
        when (requestCode) {
            REQUEST_CONNECT_DEVICE -> if (mResultCode == RESULT_OK) {
                val mExtra = data!!.extras
                val mDeviceAddress = mExtra!!.getString("DeviceAddress")
                Log.v(TAG, "Coming incoming address $mDeviceAddress")
                mBluetoothDevice = mBluetoothAdapter!!.getRemoteDevice(mDeviceAddress)
                mBluetoothConnectProgressDialog = ProgressDialog.show(
                    this@MainActivity,
                    "Connecting...", mBluetoothDevice!!.name + " : "
                            + mBluetoothDevice!!.address, true, false
                )
                val mBlutoothConnectThread: Thread = Thread(this@MainActivity)
                mBlutoothConnectThread.start()
                // pairToDevice(mBluetoothDevice); This method is replaced by
                // progress dialog with thread
            }
            REQUEST_ENABLE_BT -> if (mResultCode == RESULT_OK) {
                listPairedDevices()
                val connectIntent = Intent(
                    this@MainActivity,
                    DeviceListActivity::class.java
                )
                startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE)
            } else {
                Toast.makeText(this@MainActivity, "Message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun listPairedDevices() {
        val mPairedDevices = mBluetoothAdapter!!.bondedDevices
        if (mPairedDevices.size > 0) {
            for (mDevice in mPairedDevices) {
                Log.v(
                    TAG, "PairedDevices: " + mDevice.name + "  "
                            + mDevice.address
                )
            }
        }
    }

    override fun run() {
        try {
            mBluetoothSocket = mBluetoothDevice!!.createRfcommSocketToServiceRecord(applicationUUID)
            mBluetoothAdapter!!.cancelDiscovery()
            mBluetoothSocket!!.connect()
            mHandler.sendEmptyMessage(0)
        } catch (eConnectException: IOException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException)
            closeSocket(mBluetoothSocket!!)
            return
        }
    }

    private fun closeSocket(nOpenSocket: BluetoothSocket) {
        try {
            nOpenSocket.close()
            Log.d(TAG, "SocketClosed")
        } catch (ex: IOException) {
            Log.d(TAG, "CouldNotCloseSocket")
        }
    }

    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            mBluetoothConnectProgressDialog!!.dismiss()
            Toast.makeText(this@MainActivity, "DeviceConnected", Toast.LENGTH_SHORT).show()
        }
    }

    fun intToByteArray(value: Int): ByteArray {
        var b = ByteBuffer.allocate(4).putInt(value).array()

        for (k in 0..b.size) {
            println(
                "Selva  [" + k + "] = " + "0x" + UnicodeFormatter.byteToHex(b[k])
            )
        }
        return b
    }

    fun sel(value : Int): ByteArray {
        var buffer = ByteBuffer.allocate (2)
        buffer.putInt(value)
        buffer.flip()
        return buffer.array()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}