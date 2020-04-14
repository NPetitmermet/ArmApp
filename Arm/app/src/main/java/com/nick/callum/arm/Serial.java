package com.nick.callum.arm;

import java.io.IOException;
import java.io.OutputStream;

import com.fazecast.jSerialComm.SerialPort;

/**
 * 
 * Handles communication between ArbotiX-M robocontroller and calling application
 * 
 * Packets must be sent no more than 30hz (every 33ms)
 * 
 * Methods of serial communication
 *  * Wired
 */
class Serial{
    private static int RATE = 33;
    private static int BAUD = 38400;
    private static int NUM_BITS = 8;
    private static int STOP_BITS = 1;
    private boolean isBluetooth;
    private boolean DEBUG;

    private SerialPort[] ports;
    private SerialPort port;

    private OutputStream output;
    
    Serial(){
        this.isBluetooth = false;
        this.DEBUG = true;
        setupPort();
    }

    private void setupPort(){
        ports = SerialPort.getCommPorts();
        if(DEBUG){
            System.out.println("Available ports recognized: ");
            for(SerialPort port : ports){
                System.out.println(port.getSystemPortName());
            }
        }

        // port setup
        port = SerialPort.getCommPort("COM4");
        port.setBaudRate(BAUD);
        port.setParity(SerialPort.NO_PARITY);
        port.setNumDataBits(NUM_BITS);
        port.setNumStopBits(STOP_BITS);

        if(DEBUG)
            System.out.println("Opening port " + port.getSystemPortName() + " ...");

        port.openPort(3000);

        if(port.isOpen()){
            System.out.println("Port " + port.getSystemPortName() + " opened...");
        } else {
            System.out.println("Port " + port.getSystemPortName() + " not opened...");
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                while(port.isOpen()){
                    System.out.println("Closing port " + port.getSystemPortName() + "...");
                    port.closePort();
                }
                System.out.println("Port " + port.getSystemPortName() + " closed");
            }
        });
    }

    public void sendPacket(byte[] packet){
        if(port.isOpen()){
            try{
                output = port.getOutputStream();
                for(byte b : packet){
                    output.write(b);
                }
            } catch (IOException e){
                System.out.println("IOException caught: " + e.getStackTrace());
                
                System.out.println("===============================");
                System.out.println("Exiting program...");
                System.out.println("===============================");
                System.exit(0);
            }
        }
    }
}