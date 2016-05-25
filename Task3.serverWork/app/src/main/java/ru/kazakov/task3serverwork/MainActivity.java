package ru.kazakov.task3serverwork;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import ru.kazakov.task3serverwork.dummy.ChatsContent;
import ru.kazakov.task3serverwork.dummy.MessageContent;
import ru.kazakov.task3serverwork.utils.HashWorker;

public class MainActivity extends AppCompatActivity implements
        RegistrationFragment.OnFragmentInteractionListener,
        ChatsFragment.OnChatsListInteractionListener, MessageFragment.OnMessageInteractionListener,
        AddChannelFragment.OnAddingInteractionListener, SettingsFragment.OnSettingsInteractionListener,
        UserInfoFragment.OnUserInteractionListener {
    public static String LOGIN;
    public static String PASS;
    public static String SESSION_ID;
    public static String NICK;

    private final String ACT_LOG = "act_log";
    public static final String dstAddress = "188.166.49.215";
    public static final int port = 7777;

    public static final int RECIEVER_SERV_CODE = 100;
    public static final int ERROR_FROM_RECIEVER = 666;


    public static final String PARAM_TO_REC_PINTENT = "pint";
    public static final String PARAM_FROM_REC_ACT = "res";
    Intent intentToRecieveServ;

    public static Socket socketForServ;

    /**
     * Про логику выходов из канала: при нажатии на канал в списке ChatsFragment осуществляется вход в него. При
     * выходе с экрана канала ChatsFragment - выход из него.
     * Для этого перегрузил в onCreateView вызывается метод leave, который при выставленном флаге
     * отписывается от чата.
     */
    private Boolean needToLeaveFromCurChat = false;

    public static final String PARAM_SENDER_ACTION = "sender_action";


    /**
     * Кто позже закончил (асинк таск с показом сплеш скрина или авторизация+загрузка каналов), тот
     * и запускает следующие фрагменты.
     */
    private static boolean isTaskFinished = false;
    private static boolean areChannelsGot = false;
    private static String authResult = "none";

    /*
    Чтобы предотвратить быстрое двойное нажатие на чат (т.е. чтобы не открылось два фрагмента с
    чатами), при первом нажатии будем выставлять флаг в тру.
    А вот появление на экранефрагмента с каналами однозначно говорит о том, что можно входить в чат.
     */
    public static boolean isChannelChosen = false;

    public static boolean isRefFailed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

// TODO: 19.05.2016 для тестирования ситуации "логин отсутствует"
//        SharedPreferences mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = mSettings.edit();
//        editor.clear();
//        editor.apply();


        // было нужно на тестовом этапе, пока подключение происходило в основном потоке
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        /*
        Если это первый запуск активити, то:
        1) прячем статус бар (на сплеш скрине его быть не должно)
        2) запускаем асинк таск, ответственный за показ сплеша
        3) стартуем сервис прием сообщений, в котором проинициализируется сокет
         */
        if (savedInstanceState == null) {
            Log.d(ACT_LOG, "savedInstanceState == null");
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }

            InitialTask task = new InitialTask();
            task.execute(null, null, null);
        } else {
            socketForServ = (Socket) getLastCustomNonConfigurationInstance();
            Log.d(ACT_LOG, "else socketForServ = " + socketForServ);
        }
        startRecieverService();

    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        Log.d(ACT_LOG, "onRetain");
        return socketForServ;
    }

    /**
     * В меню у нас кнопка создания чата и переход к экрану настроек.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_channel:
                showNewFragment("AddChannelFragment", true);
                return true;
            case R.id.action_settings:
                showNewFragment("SettingsFragment", true);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(ACT_LOG, "onSaveIS");
        outState.putBoolean("new", false);
    }


    /**
     * Нужен, чтобы сплешскрин держался заданное колво времени.
     * Если мы получили список каналов и этот таск уже закончен, то просто показываем фрагмент с
     * каналами.
     * Если же после получения списка каналов таск еще не завершен, то кладм в resultConnection
     * значение true. А таск в методе onPostEx уже сам запустит экран с чатами.
     */
    class InitialTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            showNewFragment("SplashFragment", false);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isTaskFinished = true;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (areChannelsGot) {
                Toast.makeText(MainActivity.this, "onPostExecute запускает фрагмент", Toast.LENGTH_SHORT).show();
                showNewFragment("ChatsFragment", false);
            } else {
                switch (authResult){
                    case "need registr":
                        showNewFragment("RegistrationFragment", false);
                        break;
                    default:

                        break;
                }
                Toast.makeText(MainActivity.this, "onPostExecute закончил и ничего не делает", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Показывает новый фрагмент на экране.
     *
     * @param fragmentName    имя фрагмента совпадает с именем класса нужного фрагмента
     * @param backStackAdding нужно ли добавлять в бек-стек
     */
    private void showNewFragment(String fragmentName, boolean backStackAdding) {
        Fragment fragment;
        switch (fragmentName) {
            case "SplashFragment":
                fragment = new SplashFragment();
                break;
            case "RegistrationFragment":
                fragment = new RegistrationFragment();
                break;
            case "ChatsFragment":
                fragment = new ChatsFragment();
                if (getSupportActionBar() != null) {
                    getSupportActionBar().show();
                }
                break;
            case "MessageFragment":
                fragment = new MessageFragment();
                needToLeaveFromCurChat = true;
                break;
            case "SettingsFragment":
                fragment = new SettingsFragment();
                break;
            case "AddChannelFragment":
                fragment = new AddChannelFragment();
                break;
            case "UserInfoFragment":
                fragment = new UserInfoFragment();
                break;
            default:
                fragment = new SplashFragment();
                break;
        }
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_for_fragments, fragment);
        if (backStackAdding) {
            ft.addToBackStack(null);
        }
        ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();

    }


    /**
     * Запуск сервиса, ответственного за приём сообщений от сервера.
     */
    private void startRecieverService() {
        // Создаем PendingIntent для ServiceReciever
        PendingIntent pi = createPendingResult(RECIEVER_SERV_CODE, new Intent(), 0);
        // Создаем Intent для вызова сервиса, кладем туда PendingIntent
        intentToRecieveServ = new Intent(this, ServiceReciever.class).putExtra(PARAM_TO_REC_PINTENT, pi);
        // стартуем сервис
        startService(intentToRecieveServ);
    }

    /**
     * Если сохранен логин в SharedPre, то вытягиваем его оттуда(и пароль) и просим сендера залогиниться.
     * Если же нет, показываем экран с регистрацией.
     */
    private void tryToAuth() {
        SharedPreferences mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        if (mSettings.contains("login")) {
            Log.d(ACT_LOG, "have login in sharedPref");
            LOGIN = mSettings.getString("login", "");
            PASS = mSettings.getString("password", "");
            Intent intent = new Intent(this, SenderService.class).putExtra(PARAM_SENDER_ACTION, "auth");
            startService(intent);
        } else {
            Log.d(ACT_LOG, "have NO login in sharedPref");
            if (isTaskFinished) {
                showNewFragment("RegistrationFragment", false);
            } else {
                authResult = "need registr";
            }
        }
    }


// TODO: 19.05.2016 возможно, будет хорошо сделать кнопки и регистрации, и авторизации. пока непонятно, как работает серв

    /**
     * Это обработка нажатия во фрагменте RegistrationFragment.
     * Мы сохраняем логин и хешированный пароль на диск, после чего вызываем метод регистрации
     *
     * @param login
     * @param password
     */
    @Override
    public void onButRegistrPress(String login, String password,String nick) {
//        SharedPreferences mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = mSettings.edit();
//        editor.putString("login", login);
//        editor.putString("password", HashWorker.hash(password));
//        editor.apply();
//
//        LOGIN = login;
//        PASS = HashWorker.hash(password);

        LOGIN = login;
        PASS = HashWorker.hash(password);
        NICK = nick;

        Intent intent = new Intent(this, SenderService.class).putExtra(PARAM_SENDER_ACTION, "register");
        startService(intent);
    }


    /**
     * Когда мы авторизовались на сервере, необходимо сделать следующее:
     * 1) запросить список чатов
     */
    private void getChats() {
        Intent intent = new Intent(this, SenderService.class).putExtra(PARAM_SENDER_ACTION, "channellist");
        startService(intent);
    }


    /**
     * От ServiceReciever в активити поступают сообщения, по которым она определяет, что нужно делать
     * дальше.
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String action = data.getStringExtra(PARAM_FROM_REC_ACT);
        Log.d(ACT_LOG, "action from server: " + action);

        switch (action) {
            // когда только подключились
            case "welcome":
                // тут мы не должны реагировать, если был повторный запуск сервиса при неудачной регистрации
                if (!isRefFailed) {
                    tryToAuth();
                }
                break;
            case "register":
                // TODO: 25.05.2016 только сохраняем парольлогин на диск, если без ошибок, т.к. потом сразу от сервака приходит auth. Иначе надо показать ошибку и перезапустить сервис
                if (data.getStringExtra("error").equals("OK")) {
                    Log.d(ACT_LOG, "register OK");
                    // пишем пароль на диск
                    SharedPreferences mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString("login", LOGIN);
                    editor.putString("password", PASS);
                    editor.putString("nick", NICK);
                    editor.apply();
                } else {
                    isRefFailed = true;
                    Log.d(ACT_LOG, "register error " + data.getStringExtra("error"));
                    if (findViewById(R.id.tv_reg_result) != null) {
                        ((TextView) findViewById(R.id.tv_reg_result)).setText(data.getStringExtra("error"));
                    }
                    startRecieverService();
                }
                break;
            // когда прошла авторизация
            case "auth":
                if (data.getStringExtra("error").equals("OK")) {
                    getChats();
                } else if (isTaskFinished){
                    isRefFailed = true;
                    showNewFragment("RegistrationFragment", false);
                    startRecieverService();
                } else {
                    authResult = "need registr";
                }
                break;
            case "channellist":
                isRefFailed = false;
                areChannelsGot = true;
                if (isTaskFinished) {
                    showNewFragment("ChatsFragment", false);
                }
                break;
            case "enter":
                showNewFragment("MessageFragment", true);
                break;
            case "ev_enter":
                Toast.makeText(MainActivity.this, "user " + data.getStringExtra("nick") + " entered in channel", Toast.LENGTH_SHORT).show();
                break;
            case "ev_message":
                MessageFragment.adapter.notifyDataSetChanged();
                break;
            case "createchannel":
                String error = data.getStringExtra("error");
                if (findViewById(R.id.tv_add_result) != null) {
                    ((TextView) findViewById(R.id.tv_add_result)).setText(error);
                }

                if (error.equals("OK")) {
                    ChatsContent.myNewChats.get(ChatsContent.myNewChats.size() - 1).id = data.getStringExtra("chid");
                    Log.d(ACT_LOG, "myNewChat.id after createchannel " + ChatsContent.myNewChats.get(ChatsContent.myNewChats.size() - 1).id);
                    // добавляем к чату id
                    ChatsFragment.adapter.notifyDataSetChanged();

                } else {
                    // удаляем чат, который не получилось создать
                    ChatsContent.myNewChats.remove(ChatsContent.myNewChats.size() - 1);
                }
                break;
            case "setuserinfo":
                String errorr = data.getStringExtra("error");
                if (findViewById(R.id.tv_status_result) != null) {
                    ((TextView)findViewById(R.id.tv_status_result)).setText(errorr);
                }
                break;
            case "UnknownHostException":
                Toast.makeText(MainActivity.this, "Невозможно подключиться к серверу", Toast.LENGTH_LONG).show();
                break;
            case "IOException":
                Toast.makeText(MainActivity.this, "Невозможно подключиться к серверу", Toast.LENGTH_LONG).show();
                break;
            case "userinfo":
                showNewFragment("UserInfoFragment", true);
                break;
            default:
                break;

        }
        Log.d(ACT_LOG, "requestCode = " + requestCode + ", resultCode = "
                + resultCode);


    }

    // без перегрузки этого метода приложение крашится при выходе из-за сервиса
    @Override
    protected void onDestroy() {
        stopService(new Intent(this, ServiceReciever.class));
        try {
            if (socketForServ != null)
                socketForServ.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(ACT_LOG, "unable close socket");
        }
        super.onDestroy();
    }

    /**
     * При выборе определенного канала:
     * 0) isChannelChosen == true равносильно запрету выбирать новый канал
     * 1) сообщаем MessageFragment, к какому чату мы подключаемся
     * 2) отправляем запрос на сервер на вход в канал
     * 3) переходим в выбранный канал вызовом нового фрагмента (это будем делать не в этом методе, а как всегда после прихода подтверждающего сообщения от сервера)
     *
     * @param item
     */
    @Override
    public void onChannelInteraction(ChatsContent.ChatItem item) {
        if (!isChannelChosen) {
            isChannelChosen = true;
            MessageFragment.channel_id = item.id;

            Intent intent = new Intent(this, SenderService.class).putExtra(PARAM_SENDER_ACTION, "enter")
                    .putExtra("channelId", MessageFragment.channel_id);
            startService(intent);
        }
    }

    @Override
    public void leaveFromChannel() {
        if (needToLeaveFromCurChat) {
            Intent intent = new Intent(this, SenderService.class).putExtra(PARAM_SENDER_ACTION, "leave")
                    .putExtra("channelId", MessageFragment.channel_id);
            startService(intent);
            needToLeaveFromCurChat = false;
        }
    }

    /**
     * Показвыает фрагмент с информацией о пользователе.
     *
     * @param item
     */
    @Override
    public void onAvatarInteraction(MessageContent.MessageItem item) {
//        UserInfoFragment.userNick = item.nick;
        Intent intent = new Intent(this, SenderService.class).putExtra(PARAM_SENDER_ACTION, "userinfo")
                .putExtra("userId", item.from);
        startService(intent);
//        showNewFragment("UserInfoFragment", true);
    }

    /**
     * Что будет происходиь при нажатии кнопки Send:
     * 1) формируем и отправляем сообщение с помощью вызова Sender'а
     * 2) сообщение приходит с сервера и уже тогда его отображаем (конечно, уже не в этом методе)
     *
     * @param channelId
     * @param body
     */
    @Override
    public void onButtonSendInteraction(String channelId, String body) {
        Intent intent = new Intent(this, SenderService.class).putExtra(PARAM_SENDER_ACTION, "message")
                .putExtra("channelId", channelId)
                .putExtra("body", body);
        startService(intent);
    }

    /**
     * Кидаем на сервер запрос о созданни нового чата
     *
     * @param name
     * @param descr
     */
    @Override
    public void addChannel(String name, String descr) {
        ChatsContent.myNewChats.add(new ChatsContent.ChatItem("", name, descr));
        Log.d(ACT_LOG, "myNewChat " + ChatsContent.myNewChats.get(ChatsContent.myNewChats.size() - 1));
        Intent intent = new Intent(this, SenderService.class).putExtra(PARAM_SENDER_ACTION, "createchannel")
                .putExtra("name", name)
                .putExtra("descr", descr);
        startService(intent);
    }


    @Override
    public void changeSmth(String status) {
        Intent intent = new Intent(this, SenderService.class).putExtra(PARAM_SENDER_ACTION, "setuserinfo")
                .putExtra("user_status", status);
        startService(intent);
    }


    @Override
    public void onUserInteraction(Uri uri) {

    }

}
