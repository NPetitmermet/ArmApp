package com.nick.callum.arm;

/**
 * @author cyoung
 * 
 * Packet assembly and packaging class as per Arm Link Packet Structure specifications
 * found at https://learn.trossenrobotics.com/arbotix/arbotix-communication-controllers/31-arm-link-reference.html
 *
 * Packet structure (per byte)
 * 
 *  1] Packet header 0xFF
 *  2] X-axis coordinate high byte / base servo rotation high byte
 *  3] X-axis coordinate low byte / base servo rotation low byte
 *  4] Y-axis coordinate high byte / shoulder servo rotation high byte
 *  5] Y-axis coordinate low byte / shoulder servo rotation low byte
 *  6] Z-axis coordinate high byte / elbow servo rotation high byte
 *  7] Z-axis coordinate low byte / elbow servo rotation low byte
 *  8] Wrist angle high byte
 *  9] Wrist angle low byte
 * 10] Wrist rotate high byte
 * 11] Wrist rotate low byte
 * 12] Gripper high byte
 * 13] Gripper low byte
 * 14] Delta byte
 * 15] Button byte
 * 16] Extended instruction byte
 * 17] Checksum
**/

class Comm{
    private boolean DEBUG;
    private static int PACKET_SIZE = 17;
    private static byte DELTA = 125;

//    private static Serial _serialComm;

    Comm(){
        this.DEBUG = false;
//        _serialComm = new Serial();
        // initArm();
    }
    

    Comm(boolean isDebugMode){
        this.DEBUG = isDebugMode;
//        _serialComm = new Serial();
        // initArm();
    }

    public void initArm(){
        if(DEBUG)
            System.out.println("Initializing arm to cartesian mode...");

        byte[] packet = getBasePacket();
        packet[15] = 0x20;
//        _serialComm.sendPacket(packet);
    }

    public static void main(String[] args){
        Comm c = new Comm(true);
        byte[] packet = c.getBasePacket();
//        _serialComm.sendPacket(c.rotateShoulderToDegree(150, c.rotateBaseToDegree(200, c.rotateWristToDegree(300, packet))));
    }

    byte[] resetPositionToDefault(byte[] packet){
        if(DEBUG)
            System.out.println("Resetting to default...");
            
        packet[15] = (byte) 0x20;

        packet[16] = checkSum(packet);
        return packet;
    }

    byte[] emergencyStop(byte[] packet){
        if(DEBUG)
            System.out.println("EMERGENCY STOP");

        packet[15] = (byte) 0x11;

        packet[16] = checkSum(packet);
        return packet;
    }

    byte[] rotateBaseToDegree(int deg, byte[] packet){
        if(DEBUG)
            System.out.println("Rotating base to " + deg + " degrees...");
            
        byte high = (byte) (deg / 256);
        byte low = (byte) (deg % 256);

        packet[1] = high;
        packet[2] = low;

        packet[16] = checkSum(packet);
        if(DEBUG){
            printPacket(packet);
        }

        return packet;
    }

    byte[] rotateShoulderToDegree(int deg, byte[] packet){
        if(DEBUG)
            System.out.println("Rotating shoulder to " + deg + " degrees...");
            
        byte high = (byte) (deg / 256);
        byte low = (byte) (deg % 256);

        packet[3] = high;
        packet[4] = low;

        packet[16] = checkSum(packet);
        if(DEBUG){
            printPacket(packet);
        }

        return packet;
    }

    byte[] rotateElbowToDegree(int deg, byte[] packet){
        if(DEBUG)
            System.out.println("Rotating elbow to " + deg + " degrees...");
            
        byte high = (byte) (deg / 256);
        byte low = (byte) (deg % 256);

        packet[5] = high;
        packet[6] = low;

        packet[16] = checkSum(packet);
        if(DEBUG){
            printPacket(packet);
        }

        return packet;
    }

    byte[] moveWristAngleTo(int ang, byte[] packet){
        if(DEBUG)
            System.out.println("Moving wrist to " + ang + " degrees...");
            
        byte high = (byte) (ang / 256);
        byte low = (byte) (ang % 256);

        packet[7] = high;
        packet[8] = low;

        packet[16] = checkSum(packet);
        if(DEBUG){
            printPacket(packet);
        }

        return packet;
    }

    byte[] rotateWristToDegree(int deg, byte[] packet){
        if(DEBUG)
            System.out.println("Rotating wrist to " + deg + " degrees...");
            
        byte high = (byte) (deg / 256) ;
        byte low = (byte) (deg % 256);

        packet[9] = high;
        packet[10] = low;

        packet[16] = checkSum(packet);
        if(DEBUG){
            printPacket(packet);
        }

        return packet;
    }

    byte[] moveGripper(int pos, byte[] packet){
        if(DEBUG)
            System.out.println("Moving gripper to " + pos + "...");
            
        byte high = (byte) (pos / 256);
        byte low = (byte) (pos % 256);

        packet[7] = high;
        packet[8] = low;

        packet[16] = checkSum(packet);
        if(DEBUG){
            printPacket(packet);
        }

        return packet;
    }

    byte checkSum(byte[] packet){
        int sum = packet[1] + packet[2] + packet[3] + packet[4] + packet[5] + packet[6] + packet[7] + packet[8] + packet[9] + packet[10] + packet[11] + packet[12] + packet[13] + packet[14] + packet[15]; //add up bytes 2-16
        int invertedChecksum = sum % 256; //isolate the lowest byte
        byte checksum = (byte)(255 - invertedChecksum); //invert value to get file checksum

        return checksum;
    }

    public void sendPacket(byte[] packet){
//        _serialComm.sendPacket(packet);
    }

    byte[] getBasePacket(){
        byte[] packet = new byte[PACKET_SIZE];
        packet[0]  = (byte)0xFF;
        packet[1]  = 0;
        packet[2]  = 0;
        packet[3]  = 0;
        packet[4]  = 0;
        packet[5]  = 0;
        packet[6]  = 0;
        packet[7]  = 0;
        packet[8]  = 0;
        packet[9]  = 0;
        packet[10] = 0;
        packet[11] = 0;
        packet[12] = 0;
        packet[13] = DELTA;
        packet[14] = 0;
        packet[15] = 0;
        packet[16] = 0;
        return packet;
    }

    void printPacket(byte[] packet){
        String s = "Packet sent: " + "\n" + 
            "1: " + packet[0] + "\n" + 
            "2: " + packet[1] + "\n" + 
            "3: " + packet[2] + "\n" + 
            "4: " + packet[3] + "\n" + 
            "5: " + packet[4] + "\n" + 
            "6: " + packet[5] + "\n" + 
            "7: " + packet[6] + "\n" + 
            "8: " + packet[7] + "\n" + 
            "9: " + packet[8] + "\n" + 
            "10: " + packet[9] + "\n" + 
            "11: " + packet[10] + "\n" + 
            "12: " + packet[11] + "\n" + 
            "13: " + packet[12] + "\n" + 
            "14: " + packet[13] + "\n" + 
            "15: " + packet[14] + "\n" + 
            "16: " + packet[15] + "\n" + 
            "17: " + packet[16] + "\n";

        System.out.println(s);
    }
}