package ru.kazakov.task3serverwork.dummy;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatsContent {
    private static final String CHATS_CONTENT_TAG = "chats_content";

    /**
     * An array of sample (dummy) items.
     */
    public static final List<ChatItem> CHATS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, ChatItem> CHATS_MAP = new HashMap<String, ChatItem>();

    public static List<ChatItem> myNewChats = new ArrayList<>();

    /**
     * Подготавливает данные для отображения. Фактически считывает их с диска, предварительно
     * почистив коллекции, чтобы при повторном открытии фрагмента с каналами не было несколько раз
     * добавленных каналов.
     *
     * @param context
     */
    public static void fillCollections(Context context) {
        try {
            CHATS.clear();
            CHATS_MAP.clear();

            FileInputStream fileIn = context.openFileInput("file_with_channels");
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[1024];
            String s = "";
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s += readstring;
            }
            InputRead.close();

            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("channels");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                addChat(new ChatItem(jsonObject.getString("chid"), jsonObject.getString("name"), jsonObject.getString("descr")));
            }

            ArrayList<ChatItem> temp = new ArrayList<>();
            for (int i = 0; i < myNewChats.size(); i++) {
                if ( ! myNewChats.get(i).id.equals("") ) {
                    temp.add(myNewChats.get(i));
                }
            }
            myNewChats = temp;

            CHATS.addAll(myNewChats);

            Log.d(CHATS_CONTENT_TAG, "джейсон сделан!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < CHATS.size(); i++) {
            Log.d(CHATS_CONTENT_TAG, CHATS.get(i).toString());
        }

    }

    public static void addChat(ChatItem item) {
        CHATS.add(item);
        CHATS_MAP.put(item.id, item);
    }


    /**
     * A dummy item representing a piece of name.
     */
    public static class ChatItem {
        public String id;
        public final String name;
        public final String descr;

        public ChatItem(String id, String name, String descr) {
            this.id = id;
            this.name = name;
            this.descr = descr;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
