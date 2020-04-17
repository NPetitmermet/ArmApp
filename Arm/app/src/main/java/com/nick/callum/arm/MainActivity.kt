package com.nick.callum.arm

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.bluetooth.BluetoothSocket
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import java.util.UUID
import android.bluetooth.BluetoothDevice
import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.AsyncTask.execute
import android.util.Log
import org.w3c.dom.Text
import kotlinx.coroutines.*
import java.io.IOException
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private val comm = Comm()
    private var packet = comm.getBasePacket()
    private var wristRotate = 512
    private var wristBend = 515
    private var elbowBend = 555
    private var shoulderBend = 507
    private var baseRotate = 512
    private var gripperTightness = 256
    private var progress: ProgressDialog? = null
    var myBluetooth: BluetoothAdapter? = null
    var btSocket: BluetoothSocket? = null
    var address: String? = null
    private var isBtConnected = false
    val myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val newint = intent
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS)

        ConnectBT().execute()

        setContentView(R.layout.activity_main)
        wristDecrease.setOnClickListener {
            decreaseWristRotate()
        }
        wristIncrease.setOnClickListener {
            increaseWristRotate()
        }
        tightenIncrease.setOnClickListener {
            tightenGrip()
        }
        tightenDecrease.setOnClickListener{
            loosenGrip()
        }
        bendWristIncrease.setOnClickListener{
            increaseWristBend()
        }
        bendWristDecrease.setOnClickListener{
            decreaseWristBend()
        }
        elbowBendIncrease.setOnClickListener {
            increaseElbowBend()
        }
        elbowBendDecrease.setOnClickListener {
            decreaseElbowBend()
        }
        shoulderBendIncrease.setOnClickListener {
            increaseShoulderBend()
        }
        shoulderBendDecrease.setOnClickListener {
            decreaseShoulderBend()
        }
        rotateBaseIncrease.setOnClickListener {
            increaseBaseRotate()
        }
        rotateBaseDecrease.setOnClickListener {
            decreaseBaseRotate()
        }
        candy_grab.setOnClickListener {
            candyGrab()
        }
        emergency.setOnClickListener {
            emergencyStop()
        }
    }



    private fun sendSignal(packet) {
        val btSocket = btSocket
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(packet)
            } catch (e: IOException) {
//                msg("Error sending")
            }
        }
    }

    fun emergencyStop() {
        packet = comm.emergencyStop(packet)
    }

    fun candyGrab() {
        //TODO figure out what packet activates this
    }

    fun increaseWristRotate(){
        wristRotate += 50
        if(wristRotate > 1023) wristRotate = 1023
        packet = comm.moveWristAngleTo(wristRotate, packet)
        sendSignal(packet)
    }

    fun decreaseWristRotate(){
        wristRotate -= 50
        if(wristRotate < 0) wristRotate = 0
        packet = comm.moveWristAngleTo(wristRotate, packet)
        sendSignal(packet)
    }

    fun increaseWristBend(){
        wristBend += 50
        if(wristBend > 830) wristBend = 830
        packet = comm.rotateWristToDegree(wristBend, packet)
        sendSignal(packet)
    }

    fun decreaseWristBend(){
        wristBend -= 50
        if(wristBend < 200) wristBend = 200
        packet = comm.rotateWristToDegree(wristBend, packet)
        sendSignal(packet)
    }

    fun increaseBaseRotate(){
        baseRotate +=50
        if(baseRotate > 1023) baseRotate = 1023
        packet = comm.rotateBaseToDegree(baseRotate, packet)
        sendSignal(packet)
    }

    fun decreaseBaseRotate(){
        baseRotate -=50
        if(baseRotate < 0) baseRotate = 0
        packet = comm.rotateBaseToDegree(baseRotate, packet)
        sendSignal(packet)
    }

    fun increaseElbowBend(){
        elbowBend +=50
        if(elbowBend > 900) elbowBend = 900
        packet = comm.rotateElbowToDegree(elbowBend, packet)
        sendSignal(packet)
    }

    fun decreaseElbowBend(){
        elbowBend -=50
        if(elbowBend < 210) elbowBend = 210
        packet = comm.rotateElbowToDegree(elbowBend, packet)
        sendSignal(packet)
    }

    fun increaseShoulderBend(){
        shoulderBend +=50
        if(shoulderBend > 810) shoulderBend = 810
        packet = comm.rotateShoulderToDegree(shoulderBend, packet)
        sendSignal(packet)
    }

    fun decreaseShoulderBend(){
        shoulderBend -=50
        if(shoulderBend < 205) shoulderBend = 205
        packet = comm.rotateShoulderToDegree(shoulderBend, packet)
        sendSignal(packet)
    }

    fun tightenGrip(){
        gripperTightness += 50
        if(gripperTightness >512) gripperTightness = 512
        packet = comm.moveGripper(gripperTightness, packet)
        sendSignal(packet)
    }

    fun loosenGrip(){
        gripperTightness -= 50
        if(gripperTightness <0) gripperTightness = 0
        packet = comm.moveGripper(gripperTightness, packet)
        sendSignal(packet)
    }

    private fun msg(s: String) {
        Toast.makeText(applicationContext, s, Toast.LENGTH_LONG).show()
    }
    private inner class ConnectBT : AsyncTask<Void, Void, Void>() {
        private var ConnectSuccess = true

        override fun onPreExecute() {
            progress = ProgressDialog.show(this@MainActivity, "Connecting...", "Please Wait!!!")
        }

        override fun doInBackground(vararg devices: Void): Void? {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter()
                    val bluetooth = myBluetooth
                    if(bluetooth != null) {
                        val dispositivo = bluetooth.getRemoteDevice(address)
                        btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID)
                        val socket = btSocket
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                        if(socket != null)
                            socket.connect()
                        else
                            ConnectSuccess = false
                    } else
                        ConnectSuccess = false
                }
            } catch (e: IOException) {
                ConnectSuccess = false
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.")
                finish()
            } else {
                msg("Connected")
                isBtConnected = true

//                GlobalScope.launch{
//                    while(true) {
//                        sendSignal()
//                    }
//                }
            }
            progress?.dismiss()
        }
    }

}
