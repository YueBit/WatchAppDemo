package com.example.vcatsmonitorinwatch;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

import static android.content.Context.VIBRATOR_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

/**
 * A class to decode UDP messages
 * Demo Message 1 (Testing): T$IFLEX01$LS123456789012345$SWDL$TESING$80
 * Demo Message 2 (Failed): F$IFLEX02$LS123456789012345&ECOS$FAILED to Read DTC
 * Demo Message 3 (Pass): P$IFLEX03$LS123456789012345$FHC
 */
public class UdpMessageDecoder {

    public String udpMessageTestStatus;
    public String udpMessageHostName;
    public String udpMessageVinNumber;
    public String udpMessageProcessName;
    public String udpMessageWatchMessage;
    public String udpMessageTestProgress;

    public boolean decodeMessage (String udpMessage){
        String[] messageArray = udpMessage.split("\\$");
        switch (messageArray[0]){
            case "T":
                if (messageArray.length == 6){
                    udpMessageTestStatus = "Testing";
                    udpMessageHostName = messageArray[1];
                    udpMessageVinNumber = messageArray[2];
                    udpMessageProcessName = messageArray[3];
                    udpMessageWatchMessage = messageArray[4];
                    udpMessageTestProgress = messageArray[5];
                    return true;
                }else{
                    return false;
                }

            case "F":
                if (messageArray.length == 5){
                    udpMessageTestStatus = "Fail";
                    udpMessageHostName = messageArray[1];
                    udpMessageVinNumber = messageArray[2];
                    udpMessageProcessName = messageArray[3];
                    udpMessageWatchMessage = udpMessageProcessName + " " +"Failed" + ": " + messageArray[4];
                    return true;
                }else{
                    return false;
                }

            case "P":
                if (messageArray.length == 4){
                    udpMessageTestStatus = "Pass";
                    udpMessageHostName = messageArray[1];
                    udpMessageVinNumber = messageArray[2];
                    udpMessageProcessName = messageArray[3];
                    udpMessageWatchMessage = udpMessageProcessName + " " + "PASSED";
                    return true;
                }else{
                    return false;
                }

            default:
                return false;
        }
    }

}
