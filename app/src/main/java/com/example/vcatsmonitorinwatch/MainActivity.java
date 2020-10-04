package com.example.vcatsmonitorinwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    /**
     * android.widget.VideoView：视频播放器控件
     * myHandler：用于线程间通信的内部类 Handler
     */
    public MyHandler myHandler;
    private LinearLayout tester_1_All_Information;
    private LinearLayout tester_2_All_Information;
    private LinearLayout tester_3_All_Information;
    private LinearLayout tester_4_All_Information;
    private LinearLayout tester_5_All_Information;
    private LinearLayout tester_6_All_Information;
    private LinearLayout tester_7_All_Information;
    private LinearLayout tester_8_All_Information;
    private TextView tester_1_Name_VIN;
    private TextView tester_1_Operator_Message;
    private TextView tester_1_Progress;
    private TextView tester_2_Name_VIN;
    private TextView tester_2_Operator_Message;
    private TextView tester_2_Progress;
    private TextView tester_3_Name_VIN;
    private TextView tester_3_Operator_Message;
    private TextView tester_3_Progress;
    private TextView tester_4_Name_VIN;
    private TextView tester_4_Operator_Message;
    private TextView tester_4_Progress;
    private TextView tester_5_Name_VIN;
    private TextView tester_5_Operator_Message;
    private TextView tester_5_Progress;
    private TextView tester_6_Name_VIN;
    private TextView tester_6_Operator_Message;
    private TextView tester_6_Progress;
    private TextView tester_7_Name_VIN;
    private TextView tester_7_Operator_Message;
    private TextView tester_7_Progress;
    private TextView tester_8_Name_VIN;
    private TextView tester_8_Operator_Message;
    private TextView tester_8_Progress;

    //Define a Vector for tester Names
    Vector<String> testerNameVector = new Vector<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewsInMainActivity();

        /** 创建 handler 并与 looper 绑定
         * 主线程 MainActivity extends AppCompatActivity，AppCompatActivity 的祖上有 ContextWrapper
         * android.content.ContextWrapper#getMainLooper() ：获取 Looper
         * */
        myHandler = new MyHandler(MainActivity.this.getMainLooper());

        /**
         * 创建新线程用于循环监听 UDP 消息
         * 虽然如果将 监听UDP的线程 直接放在本类中，操作会简单一些，但是为了更加清晰，推荐新建线程类,
         * 所以要将创建好的 Handler 传递到后台的 UDP 线程中去
         */
        UdpThread udpThread = new UdpThread(myHandler);
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(udpThread);
    }

    /**
     * 自定义 android.os.Handler,用于接收后台线程传递过来给主线程的数据
     * Handler 是 Android 中专门用来处理线程间传递数据的工具
     */
    public class MyHandler extends Handler {

        /**
         * android.os.Looper 主要功能是为特定单一线程运行一个消息环,一个线程对应一个 Looper，同样一个 looper 对应一个线程。
         * 一个线程创建时本身是没有自己的 looper （只有主线程有），因此需要手动创建 Looper ，然后将 Looper 与线程相关联，
         * 操作方式：在需要关联的 looper 的线程中调用 Looper.prepare(预备)，之后再调用 Looper.loop(循环) 启动 looper，如下所示：
         * <pre>
         *  class LooperThread extends Thread {
         *      public Handler mHandler;
         *
         *      public void run() {
         *          Looper.prepare();
         *
         *          mHandler = new Handler() {
         *              public void handleMessage(Message msg) {
         *                  // process incoming messages here
         *              }
         *          };
         *
         *          Looper.loop();
         *      }
         *  }</pre>
         * 将 looper 与线程关联的时候，looper 会同时生产一个 messageQueue(消息队列)，looper 会不停的从 messageQueue 中取出消息(Message),
         * 然后线程就可以根据 Message 中的内容进行相应的操作。
         * 在创建 Handler 的时候,需要与特定的 looper 绑定,这样通过 handler 就可以把 message 传递给特定的 looper,继而传递给特定的线程。
         * 线程---Looper---Handler：一个 looper 可以对应多个 handler，而一个 handler 只能对应一个 looper
         *
         * @param L
         */
        public MyHandler(Looper L) {
            super(L);
        }

        /**
         * 必须重写这个方法，用于处理 android.os.Message,
         * 当 后台线程调用 android.os.Handler#sendMessage(android.os.Message) 方法发送消息后
         * 下面的 handleMessage(Message msg) 就会自动触发，然后处理消息
         */
        @Override
        public void handleMessage(Message message) {
            /**
             * android.os.Bundle 就是消息中的数据
             * android.os.BaseBundle#getString(java.lang.String)：取值的 key 不存在时，返回 null
             */
            Bundle bundle = message.getData();
            String messageText = bundle.getString("messageText");
            Log.i("Wmx Logs::", "handleMessage 接收到消息>>>" + messageText);

            if (messageText != null && !"".equals(messageText)) {
                tester_1_Operator_Message.setText(messageText);
                UdpMessageDecoder decodeUdpMessage = new UdpMessageDecoder();
                boolean bDecodeResult = decodeUdpMessage.decodeMessage(messageText);
                if (bDecodeResult){
                    switch (decodeUdpMessage.udpMessageHostName){
                        case TesterName.tester1Name:
                            tester_1_Name_VIN.setText(decodeUdpMessage.udpMessageHostName + " - " + decodeUdpMessage.udpMessageVinNumber);
                            tester_1_Operator_Message.setText(decodeUdpMessage.udpMessageWatchMessage);
                            setOperatorMessageTextColor(decodeUdpMessage.udpMessageTestStatus, tester_1_Operator_Message);
                            tester_1_Progress.setText(decodeUdpMessage.udpMessageTestProgress);
                            break;
                        case TesterName.tester2Name:
                            tester_2_Name_VIN.setText(decodeUdpMessage.udpMessageHostName + " - " + decodeUdpMessage.udpMessageVinNumber);
                            tester_2_Operator_Message.setText(decodeUdpMessage.udpMessageWatchMessage);
                            setOperatorMessageTextColor(decodeUdpMessage.udpMessageTestStatus, tester_2_Operator_Message);
                            tester_2_Progress.setText(decodeUdpMessage.udpMessageTestProgress);
                            break;
                        case TesterName.tester3Name:
                            tester_3_Name_VIN.setText(decodeUdpMessage.udpMessageHostName + " - " + decodeUdpMessage.udpMessageVinNumber);
                            tester_3_Operator_Message.setText(decodeUdpMessage.udpMessageWatchMessage);
                            setOperatorMessageTextColor(decodeUdpMessage.udpMessageTestStatus, tester_3_Operator_Message);
                            tester_3_Progress.setText(decodeUdpMessage.udpMessageTestProgress);
                            break;
                        case TesterName.tester4Name:
                            tester_4_Name_VIN.setText(decodeUdpMessage.udpMessageHostName + " - " + decodeUdpMessage.udpMessageVinNumber);
                            tester_4_Operator_Message.setText(decodeUdpMessage.udpMessageWatchMessage);
                            setOperatorMessageTextColor(decodeUdpMessage.udpMessageTestStatus, tester_4_Operator_Message);
                            tester_4_Progress.setText(decodeUdpMessage.udpMessageTestProgress);
                            break;
                        case TesterName.tester5Name:
                            tester_5_Name_VIN.setText(decodeUdpMessage.udpMessageHostName + " - " + decodeUdpMessage.udpMessageVinNumber);
                            tester_5_Operator_Message.setText(decodeUdpMessage.udpMessageWatchMessage);
                            setOperatorMessageTextColor(decodeUdpMessage.udpMessageTestStatus, tester_5_Operator_Message);
                            tester_5_Progress.setText(decodeUdpMessage.udpMessageTestProgress);
                            break;
                        case TesterName.tester6Name:
                            tester_6_Name_VIN.setText(decodeUdpMessage.udpMessageHostName + " - " + decodeUdpMessage.udpMessageVinNumber);
                            tester_6_Operator_Message.setText(decodeUdpMessage.udpMessageWatchMessage);
                            setOperatorMessageTextColor(decodeUdpMessage.udpMessageTestStatus, tester_6_Operator_Message);
                            tester_6_Progress.setText(decodeUdpMessage.udpMessageTestProgress);
                            break;
                        case TesterName.tester7Name:
                            tester_7_Name_VIN.setText(decodeUdpMessage.udpMessageHostName + " - " + decodeUdpMessage.udpMessageVinNumber);
                            tester_7_Operator_Message.setText(decodeUdpMessage.udpMessageWatchMessage);
                            setOperatorMessageTextColor(decodeUdpMessage.udpMessageTestStatus, tester_7_Operator_Message);
                            tester_7_Progress.setText(decodeUdpMessage.udpMessageTestProgress);
                            break;
                        case TesterName.tester8Name:
                            tester_8_Name_VIN.setText(decodeUdpMessage.udpMessageHostName + " - " + decodeUdpMessage.udpMessageVinNumber);
                            tester_8_Operator_Message.setText(decodeUdpMessage.udpMessageWatchMessage);
                            setOperatorMessageTextColor(decodeUdpMessage.udpMessageTestStatus, tester_8_Operator_Message);
                            tester_8_Progress.setText(decodeUdpMessage.udpMessageTestProgress);
                            break;
                    }
                }
            }
        }
    }

    private void findViewsInMainActivity() {
        tester_1_All_Information = findViewById(R.id.Kanban_Content_1st_LinearView);
        tester_1_Name_VIN = findViewById(R.id.Kanban_Content_1st_LinearView_1stLine);
        tester_1_Operator_Message = findViewById(R.id.Kanban_Content_1st_LinearView_2ndLine);
        tester_1_Progress = findViewById(R.id.Kanban_Content_1st_LinearView_3rdLine);
        tester_2_All_Information = findViewById(R.id.Kanban_Content_2nd_LinearView);
        tester_2_Name_VIN = findViewById(R.id.Kanban_Content_2nd_LinearView_1stline);
        tester_2_Operator_Message = findViewById(R.id.Kanban_Content_2nd_LinearView_2ndline);
        tester_2_Progress = findViewById(R.id.Kanban_Content_2nd_LinearView_3rdline);
        tester_3_All_Information = findViewById(R.id.Kanban_Content_3rd_LinearView);
        tester_3_Name_VIN = findViewById(R.id.Kanban_Content_3rd_LinearView_1stLine);
        tester_3_Operator_Message = findViewById(R.id.Kanban_Content_3rd_LinearView_2ndLine);
        tester_3_Progress = findViewById(R.id.Kanban_Content_3rd_LinearView_3rdLine);
        tester_4_All_Information = findViewById(R.id.Kanban_Content_4th_LinearView);
        tester_4_Name_VIN = findViewById(R.id.Kanban_Content_4th_LinearView_1stLine);
        tester_4_Operator_Message = findViewById(R.id.Kanban_Content_4th_LinearView_2ndLine);
        tester_4_Progress = findViewById(R.id.Kanban_Content_4th_LinearView_3rdLine);
        tester_5_All_Information = findViewById(R.id.Kanban_Content_5th_LinearView);
        tester_5_Name_VIN = findViewById(R.id.Kanban_Content_5th_LinearView_1stLine);
        tester_5_Operator_Message = findViewById(R.id.Kanban_Content_5th_LinearView_2ndLine);
        tester_5_Progress = findViewById(R.id.Kanban_Content_5th_LinearView_3rdLine);
        tester_6_All_Information = findViewById(R.id.Kanban_Content_6th_LinearView);
        tester_6_Name_VIN = findViewById(R.id.Kanban_Content_6th_LinearView_1stLine);
        tester_6_Operator_Message = findViewById(R.id.Kanban_Content_6th_LinearView_2ndLine);
        tester_6_Progress = findViewById(R.id.Kanban_Content_6th_LinearView_3rdLine);
        tester_7_All_Information = findViewById(R.id.Kanban_Content_7th_LinearView);
        tester_7_Name_VIN = findViewById(R.id.Kanban_Content_7th_LinearView_1stLine);
        tester_7_Operator_Message = findViewById(R.id.Kanban_Content_7th_LinearView_2ndLine);
        tester_7_Progress = findViewById(R.id.Kanban_Content_7th_LinearView_3rdLine);
        tester_8_All_Information = findViewById(R.id.Kanban_Content_8th_LinearView);
        tester_8_Name_VIN = findViewById(R.id.Kanban_Content_8th_LinearView_1stLine);
        tester_8_Operator_Message = findViewById(R.id.Kanban_Content_8th_LinearView_2ndLine);
        tester_8_Progress = findViewById(R.id.Kanban_Content_8th_LinearView_3rdLine);
    }

    private void setOperatorMessageTextColor (String testStatus, TextView operatorMessage){
        switch (testStatus){
            case "Testing":
                operatorMessage.setTextColor(Color.YELLOW);
                break;
            case "Fail":
                operatorMessage.setTextColor(Color.RED);
                break;
            case "Pass":
                operatorMessage.setTextColor(Color.GREEN);
                break;
            default:
                operatorMessage.setTextColor(Color.WHITE);
        }
    }
}