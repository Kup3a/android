package ru.kazakov.task3serverwork;

import android.app.Service;
import android.content.Intent;
import android.net.sip.SipRegistrationListener;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ru.kazakov.task3serverwork.utils.HashWorker;

public class SenderService extends Service {
    private final String ACT_LOG = "sender_log";
    static OutputStream outputStream = null;

    public SenderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            outputStream = MainActivity.socketForServ.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getStringExtra(MainActivity.PARAM_SENDER_ACTION);
        SenderThread senderThread = new SenderThread(action, intent);
        senderThread.run();

        return super.onStartCommand(intent, flags, startId);
    }

    public void auth() {
        String login = MainActivity.LOGIN;
        String hashedPassword = MainActivity.PASS;
        try {
            String query = " {\n" +
                    "  \"action\":\"auth\",\n" +
                    "  \"data\":{\n" +
                    "   \"login\":\"" + login + "\",\n" +
                    "   \"pass\":\"" + hashedPassword + "\"\n" +
                    "  }\n" +
                    " }";
            outputStream.write(query.getBytes());
            Log.d("sender_log", "auth with query = " + hashedPassword);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void register() {
        String login = MainActivity.LOGIN;
        String hashedPassword = MainActivity.PASS;
        try {
            String query = " {\n" +
                    "  \"action\":\"register\", \n" +
                    "  \"data\":{\n" +
                    "   \"login\":\"" + login + "\",\n" +
                    "   \"pass\":\"" + hashedPassword + "\",\n" +
                    "   \"nick\":\"" + MainActivity.NICK + "\"\n" +
                    "  }\n" +
                    " }";
            outputStream.write(query.getBytes());
            Log.d("sender_log", "register with query " + hashedPassword);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void channellist() {
        try {
            String query = "{\n" +
                    "  \"action\":\"channellist\",\n" +
                    "  \"data\":{\n" +
                    "  \"cid\":\"" + MainActivity.LOGIN + "\",\n" +
                    "  \"sid\":\"" + MainActivity.SESSION_ID + "\"\n" +
                    "  }\n" +
                    " }";
            outputStream.write(query.getBytes());
            Log.d("sender_log", "getting channellist with sid " + MainActivity.SESSION_ID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void message(String channelId, String body) {
        try {
            String query = "{\n" +
                    " \"action\":\"message\",\n" +
                    " \"data\": {\n" +
                    "  \"cid\":\"" + MainActivity.LOGIN + "\",\n" +
                    "  \"sid\":\"" + MainActivity.SESSION_ID + "\",\n" +
                    "  \"channel\":\"" + channelId + "\",\n" +
                    "  \"body\":\"" + body + "\" \n" +
                    "  }\n" +
                    " }";
            outputStream.write(query.getBytes());
            Log.d("sender_log", "sending message: " + body + ", to channel: " + channelId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enter(String channelId) {
        try {
            String query = "{\n" +
                    " \"action\":\"enter\",\n" +
                    " \"data\": {\n" +
                    "  \"cid\":\"" + MainActivity.LOGIN + "\",\n" +
                    "  \"sid\":\"" + MainActivity.SESSION_ID + "\",\n" +
                    "  \"channel\":\"" + channelId + "\"\n" +
                    "  }\n" +
                    " }";
            outputStream.write(query.getBytes());
            Log.d("sender_log", "entering to channel: " + query);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void leave(String channelId) {
        try {
            String query = "{\n" +
                    " \"action\":\"leave\",\n" +
                    " \"data\": {\n" +
                    "  \"cid\":\"" + MainActivity.LOGIN + "\",\n" +
                    "  \"sid\":\"" + MainActivity.SESSION_ID + "\",\n" +
                    "  \"channel\":\"" + channelId + "\"\n" +
                    "  }\n" +
                    " }";
            outputStream.write(query.getBytes());
            Log.d("sender_log", "leaving channel: " + query);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createchannel(String name, String descr) {
        try {
            String query = "{\n" +
                    "\t\"action\":\"createchannel\",\n" +
                    "\t\"data\":{\n" +
                    "\t\"cid\":\"" + MainActivity.LOGIN + "\",\n" +
                    "\t\"sid\":\"" + MainActivity.SESSION_ID + "\",\n" +
                    "\t\"name\":\"" + name + "\",\n" +
                    "\t\"descr\":\"" + descr + "\"\n" +
                    "\t}\n" +
                    " }";
            outputStream.write(query.getBytes());
            Log.d("sender_log", "create channel: " + query);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void userinfo(String userId) {
        try {
            String query = "{\n" +
                    " \"action\":\"userinfo\",\n" +
                    " \"data\": {\n" +
                    "  \"user\":\""+ userId + "\",\n" +
                    "  \"cid\":\"" + MainActivity.LOGIN + "\",\n" +
                    "  \"sid\":\"" + MainActivity.SESSION_ID + "\"\n" +
                    "  }\n" +
                    " }";
            outputStream.write(query.getBytes());
            Log.d("sender_log", "getting userinfo: " + query);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setuserinfo(String status) {
        try {
            String query = "{\n" +
                    " \"action\":\"setuserinfo\",\n" +
                    " \"data\": {\n" +
                    "  \"user_status\":\"" + status + "\",\n" +
                    "  \"cid\":\"" + MainActivity.LOGIN + "\",\n" +
                    "  \"sid\":\"" + MainActivity.SESSION_ID + "\"\n" +
                    "  }\n" +
                    " }";
            outputStream.write(query.getBytes());
            Log.d("sender_log", "getting userinfo: " + query);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class SenderThread implements Runnable {

        private String action;
        private Intent intent;

        public SenderThread(String action, Intent intent) {
            this.action = action;
            this.intent = intent;
        }

        @Override
        public void run() {
            switch (action) {
                case "auth":
                    auth();
                    break;
                case "register":
                    register();
                    break;
                case "channellist":
                    channellist();
                    break;
                case "message":
                    message(intent.getStringExtra("channelId"), intent.getStringExtra("body"));
                    break;
                case "enter":
                    enter(intent.getStringExtra("channelId"));
                    break;
                case "leave":
                    leave(intent.getStringExtra("channelId"));
                    break;
                case "createchannel":
                    createchannel(intent.getStringExtra("name"), intent.getStringExtra("descr"));
                    break;
                case "userinfo":
                    userinfo(intent.getStringExtra("userId"));
                    break;
                case "setuserinfo":
                    setuserinfo(intent.getStringExtra("user_status"));
                    break;
            }
            stopSelf();
        }
    }

}
