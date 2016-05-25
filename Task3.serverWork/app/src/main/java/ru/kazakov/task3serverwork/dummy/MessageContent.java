package ru.kazakov.task3serverwork.dummy;

import android.content.Intent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample name for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class MessageContent {

    public static final List<MessageItem> ITEMS = new ArrayList<MessageItem>();


    public static void addMessage(String from, String nick, String body) {
        ITEMS.add(new MessageItem(from, nick, body));
    }

    /**
     * A dummy item representing a piece of name.
     */
    public static class MessageItem {
        public final String from;
        public final String nick;
        public final String body;


        public MessageItem(String from, String nick, String body) {
            this.from = from;
            this.nick = nick;
            this.body = body;
        }

        @Override
        public String toString() {
            return nick + ": " + body;
        }
    }
}
