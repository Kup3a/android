package ru.kazakov.finalproject.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class AdvertContent {

    public static final List<AdvertItem> ITEMS = new ArrayList<>();
    public static final Map<String, AdvertItem> ITEM_MAP = new HashMap<>();

    public static final List<User> USERS = new ArrayList<>();
    public static final Map<String, User> USER_MAP = new HashMap<>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addAdvertItem(createDummyItem(i));
        }
        // создадим тестового юзера
        User testUser = new User("1", "kazakov", "89851234567", "vk.com");
        USERS.add(testUser);
        USER_MAP.put(testUser.id, testUser);
    }

    private static void addAdvertItem(AdvertItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.name, item);
    }

    private static AdvertItem createDummyItem(int position) {
        return new AdvertItem(Integer.toString(position), String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class AdvertItem {
        // эти поля мы получим при загрузке списка объявлений
        public final String id;
        public final String name;
        public final String price;
        public final String place;
        // а эти уже при переходе к просмотру конкретного объявления
        public final String deposit = null;
        public final String time = null;
        public final String descr = null;
        // а вот юзера будем частично получать до, частично после
        //// TODO: 19.11.2016 в тестовом режиме только так
        public final User user = new User("0", "user", "8888", "vk.com");


        public AdvertItem(String id, String name, String price, String place) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.place = place;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class User {
        public final String id;
        public final String name;
        public final String tel;
        public final String vk;

        public User(String id, String name, String tel, String vk) {
            this.id = id;
            this.name = name;
            this.tel = tel;
            this.vk = vk;
        }
    }
}
