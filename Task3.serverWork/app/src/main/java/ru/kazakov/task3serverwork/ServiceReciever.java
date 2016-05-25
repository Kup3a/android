package ru.kazakov.task3serverwork;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import ru.kazakov.task3serverwork.dummy.MessageContent;

public class ServiceReciever extends Service {

    private final String FRAG_REC_TAG = "reciever_log";
    static Socket socket = null;
    public static InputStream inputStream = null;
    private final String dstAddress = "188.166.49.215";
    private final int port = 7777;

    public ServiceReciever() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PendingIntent pint = intent.getParcelableExtra(MainActivity.PARAM_TO_REC_PINTENT);

        recieveMessages(pint);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(FRAG_REC_TAG, "reciever destroyed");
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stopSelf();
    }

    private void recieveMessages(final PendingIntent pendingIntent) {
        final PendingIntent pi = pendingIntent;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(FRAG_REC_TAG, "in recieveMessages");


                String response = "";
                try {

//                    MainActivity.socketForServ = new Socket(MainActivity.dstAddress, MainActivity.port);
//
//                    socket = MainActivity.socketForServ;

                    if (MainActivity.socketForServ == null || MainActivity.isRefFailed) {
                        Log.d(FRAG_REC_TAG, "socket == null");
                        socket = new Socket(MainActivity.dstAddress, MainActivity.port);
                        MainActivity.socketForServ = socket;
                    } else {
                        socket = MainActivity.socketForServ;
                    }


                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                    byte[] buffer = new byte[500];
//                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    inputStream = socket.getInputStream();
                    JSONObject jsonObject;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                        response = byteArrayOutputStream.toString("UTF-8");
                        Log.d(FRAG_REC_TAG, "read json from socket: " + response);
                        try {
                            jsonObject = new JSONObject(response);

                            if (!jsonObject.toString().equals(response)) {
                                String[] strings = response.split("(\\}\\{){1}");
                                for (int i = 0; i < strings.length; i++) {
                                    strings[i] = (i%2 == 0) ? strings[i] + "}" : "{" + strings[i];
                                    Log.d(FRAG_REC_TAG, strings[i]);
                                    chooseAction(new JSONObject(strings[i]), pendingIntent);
                                }
                            } else {
                                Log.d(FRAG_REC_TAG, "equal");
                                chooseAction(jsonObject, pi);
                            }

                            byteArrayOutputStream.reset();
//                            chooseAction(jsonObject, pi);
                        } catch (JSONException jsonEx) {
                            Log.d(FRAG_REC_TAG, "json isn't ready yet: " + response);
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (UnknownHostException e) {
                    Log.d(FRAG_REC_TAG, "UnknownHostException");

                    // уведомляем активити о проблемах с сервером
                    Intent intent = new Intent().putExtra(MainActivity.PARAM_FROM_REC_ACT, "UnknownHostException");
                    try {
                        pi.send(ServiceReciever.this, MainActivity.ERROR_FROM_RECIEVER, intent);
                    } catch (PendingIntent.CanceledException e1) {
                        e1.printStackTrace();
                    }

                    e.printStackTrace();
                } catch (IOException e) {
                    Log.d(FRAG_REC_TAG, "IOException");

                    Intent intent = new Intent().putExtra(MainActivity.PARAM_FROM_REC_ACT, "IOException");
                    try {
                        pi.send(ServiceReciever.this, MainActivity.ERROR_FROM_RECIEVER, intent);
                    } catch (PendingIntent.CanceledException e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * В зависимости от полученного от сервера сообщения будем выполнять разные действия:
     * action = welcome: отправляем Активити интент с welcome, по которому та понимает, что нужно слать регистрацию/авторизацию
     * action = auth: после проверки на ошибки отсылаем Активити интент с session_id
     * action = register: после проверки на ошибки отсылаем Активити интент со статусом
     * <p/>
     * Если обнаружилась ошибка, отслыаем её Активити.
     *
     * @param jsonObject - джейсон, из которого извлекаем action
     */
    private void chooseAction(JSONObject jsonObject, PendingIntent pendingIntent) throws JSONException, PendingIntent.CanceledException {
        Log.d(FRAG_REC_TAG, "full mes from serv: " + jsonObject.toString());
        String action = jsonObject.getString("action");
        Intent intent;
        switch (action) {
            case "welcome":
                sendStrTOActivity(action, pendingIntent);
                break;
            case "auth":

                if (jsonObject.getJSONObject("data").getInt("status") == 0) {
                    MainActivity.SESSION_ID = jsonObject.getJSONObject("data").getString("sid");
                }
                intent = new Intent().putExtra(MainActivity.PARAM_FROM_REC_ACT, action)
                        .putExtra("error", jsonObject.getJSONObject("data").getString("error"));

                pendingIntent.send(ServiceReciever.this, 1, intent);
                break;
            case "register":
                intent = new Intent().putExtra(MainActivity.PARAM_FROM_REC_ACT, action)
                        .putExtra("error", jsonObject.getJSONObject("data").getString("error"));
                Log.d(FRAG_REC_TAG, "register status " + jsonObject.getJSONObject("data").getInt("status"));
                pendingIntent.send(ServiceReciever.this, 8, intent);
                break;
            case "unknown":
                sendWithErrorChecking(jsonObject, pendingIntent);
                break;
            case "channellist":
                // СПИСОК КАНАЛОВ ПРОСИМ ОДИН РАЗ, ТАК ЧТО ВСЁ НОРМ!
                if (jsonObject.getJSONObject("data").getInt("status") != 0) {
                    sendStrTOActivity(jsonObject.getJSONObject("data").getString("error"), pendingIntent);
                } else {
                    // здесь кладем чаты на диск
                    String filename = "file_with_channels";
                    FileOutputStream outputStream;
                    try {
                        outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(jsonObject.getString("data").getBytes());
                        outputStream.close();
                        Log.d(FRAG_REC_TAG, "каналы на диске");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    sendStrTOActivity(jsonObject.getString("action"), pendingIntent);
                }
                break;
            case "enter":
                /*
                Для входа в канал:
                1) подчищаем список сообщений(чтобы при не первом входе не было косяков)
                2) заполняем список сообщений уже пришедшими сообщениями
                3) посылаем активности сообщение, которое она расценит, как готовность к отображению фрагмента с каналом
                 */
                if (jsonObject.getJSONObject("data").getInt("status") != 0) {
                    sendStrTOActivity(jsonObject.getJSONObject("data").getString("error"), pendingIntent);
                } else {
                    MessageContent.ITEMS.clear();

                    JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("last_msg");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        MessageContent.addMessage(jsonObject.getString("from"),
                                jsonObject.getString("nick"), jsonObject.getString("body"));
                    }

                    sendStrTOActivity(jsonObject.getString("action"), pendingIntent);
                }
                break;
            case "ev_enter":
//                sendStrTOActivity(jsonObject.getString("action"), pendingIntent);
                intent = new Intent().putExtra(MainActivity.PARAM_FROM_REC_ACT, action)
                        .putExtra("nick", jsonObject.getJSONObject("data").getString("nick"));
                pendingIntent.send(ServiceReciever.this, 6, intent);
                break;
            case "ev_message":
                /*
                Тут мы должны
                1) добавить в коллекцию сообщений
                2)
                 */
                jsonObject = jsonObject.getJSONObject("data");
                MessageContent.addMessage(jsonObject.getString("from"),
                        jsonObject.getString("nick"), jsonObject.getString("body"));
                Log.d(FRAG_REC_TAG, "new mes added");

//                MessageFragment.adapter.swap(jsonObject.getString("from"),
//                        jsonObject.getString("nick"), jsonObject.getString("body"));

                sendStrTOActivity("ev_message", pendingIntent);
                break;
            case "createchannel":
                intent = new Intent().putExtra(MainActivity.PARAM_FROM_REC_ACT, action)
                        .putExtra("error", jsonObject.getJSONObject("data").getString("error"));
                Log.d(FRAG_REC_TAG, "createchannel status " + jsonObject.getJSONObject("data").getInt("status"));
                if (jsonObject.getJSONObject("data").getInt("status") == 0) {
                    intent.putExtra("chid", jsonObject.getJSONObject("data").getString("chid"));
                }
                pendingIntent.send(ServiceReciever.this, 8, intent);
                break;
            case "setuserinfo":
                intent = new Intent().putExtra(MainActivity.PARAM_FROM_REC_ACT, action)
                        .putExtra("error", jsonObject.getJSONObject("data").getString("error"));
                Log.d(FRAG_REC_TAG, "setuserinfo status " + jsonObject.getJSONObject("data").getInt("status"));
                pendingIntent.send(ServiceReciever.this, 8, intent);
                break;
            case "userinfo":
                UserInfoFragment.userNick = jsonObject.getJSONObject("data").getString("nick");
                UserInfoFragment.status = jsonObject.getJSONObject("data").getString("user_status");
                sendWithErrorChecking(jsonObject, pendingIntent);
                break;
        }
    }

    /*
    .
    // TODO: 18.05.2016  Можно придумать систему кодов, которые будут соответствовать каждому action'у

     */
    private void sendStrTOActivity(String action, PendingIntent pi) throws PendingIntent.CanceledException {
        Map<String, Integer> actionCodes = new HashMap<>(12);
        actionCodes.put("welcome", 0);
        actionCodes.put("auth", 1);
        actionCodes.put("register", 2);
        actionCodes.put("channellist", 3);
        actionCodes.put("unknown", 4);
        actionCodes.put("enter", 5);
        actionCodes.put("ev_enter", 6);
        actionCodes.put("ev_message", 7);
        actionCodes.put("userinfo", 9);
        // возможно, вкладывание полезной инфы тоже следует перенести в методы
        // потому что сейчас в активити полезного ничего не уходит. только поле action
        // TODO: 24.05.2016 не нравится, то сейчас мы дублируем посылку action при успешном действии: action есть и в интенте, и в resultCod. Надо ВСегда(даже при ошибке) посылать и action, и результат
        Intent intent = new Intent().putExtra(MainActivity.PARAM_FROM_REC_ACT, action);
        if (actionCodes.get(action) != null) {
            pi.send(ServiceReciever.this, actionCodes.get(action), intent);
        } else {
            pi.send(ServiceReciever.this, MainActivity.ERROR_FROM_RECIEVER, intent);
        }

    }

    private void sendWithErrorChecking(JSONObject jsonObject, PendingIntent pi) throws JSONException, PendingIntent.CanceledException {
        if (jsonObject.getJSONObject("data").getInt("status") != 0) {
            sendStrTOActivity(jsonObject.getJSONObject("data").getString("error"), pi);
        } else {
            sendStrTOActivity(jsonObject.getString("action"), pi);
        }
    }

}
