package ru.kazakov.finalproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.kazakov.finalproject.dummy.AdvertContent;
import ru.kazakov.finalproject.dummy.MyDealContent;

public class MainActivity extends AppCompatActivity implements AdvertListFragment.OnAdvertListInteractionListener,
        AdvertItemFragment.OnItemFragmentInteractionListener, UserProfileFragment.OnProfileInteractionListener,
        SearchFragment.OnFragmentSearchInteractionListener, MyDealsFragment.OnMyDealInteractionListener,
        SettingsFragment.OnSettingsInteractionListener, CurrentPositionInterface, RegistrationFragment.OnRegistrationInteractionListener,
        CardFragment.OnCardFragmentInteractionListener {

    public static final String prefName = "mysettings";
    private static final String M_AC_LOG = "main_act_log";
    public static boolean IS_AFTER_ONBOARDING = false;
    public static boolean IS_ADVERT_CHOOSEN = false;
    public static boolean IS_USER_CHOOSEN = false;
    private String[] titles;
    private ListView drawerList; // это лист из меню
    private DrawerLayout drawerLayout;        // это корневой элемент в layout
    private ActionBarDrawerToggle drawerToggle;      //  это слушатель событий, происходящих с DrawerLayout
    private ActionBar actionBar;
    private int currentPosition = -1;  // текущий выбранный фрагмент
    private Map<String, Integer> fragmentNameToMenuPosition = new HashMap<>();
    private Map<String, String> fragmentNameToTitleText = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(M_AC_LOG, "create");
        initFragmentMenuMap();
        initFragmentTitleMap();
        setContentView(R.layout.activity_main);
        //получаем ссылку на DrawerLayout - это тот, который является корневым в нашем layout
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_main);

        // TODO: 19.11.2016 пока идет разработка
        SharedPreferences set = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        if (set.contains(AdvertListFragment.SET_VISITED)) {
            SharedPreferences.Editor editor = set.edit();
            editor.remove(AdvertListFragment.SET_VISITED);
            editor.apply();
        }

        // начинаем грузить объявления
        LoadAdvertsTask task = new LoadAdvertsTask();
        task.execute(null, null, null);
        /* смотрим, нужно ли показывать онбоардинг */
        SharedPreferences mSettings = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        if (mSettings.contains(AdvertListFragment.SET_VISITED)) {
            // show advert
            if (getSupportActionBar() != null) {
                actionBar = getSupportActionBar();
                prepareAcionBar(actionBar);
                actionBar.show();
            }
            prepareLeftMenu();
            showNewFragment("AdvertListFragment", false);
        } else {
            // show onboarding
            Intent i = new Intent(MainActivity.this, CustomIntro.class);
            startActivity(i);
            
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (IS_AFTER_ONBOARDING) {
            prepareLeftMenu();
            actionBar = getSupportActionBar();
            prepareAcionBar(actionBar);
            showNewFragment("AdvertListFragment", false);
        }

    }

    private void prepareLeftMenu() {
        //заполнение списка мению
        titles = getResources().getStringArray(R.array.titles_for_menu);
        drawerList = (ListView) findViewById(R.id.drawer_list);
        drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, titles));
        // назначаем слушателя, класс которого сами и создали
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private void prepareAcionBar(ActionBar actionBar) {
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.ic_navigate_before_white);
    }

    @Override
    public void showReviewsAboutUser(Uri uri) {

    }

    @Override
    public void onSearch(Uri uri) {

    }

    @Override
    public void onDealsListInteraction(MyDealContent.MyDeal item) {
        Toast.makeText(this, "mydeal " + item.id, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSetButInteraction(Uri uri) {

    }

    @Override
    public void setCurrentPosition(String fragmentName) {
        Log.d(M_AC_LOG, "cur_pos = " + currentPosition + ", new pos = " + fragmentNameToMenuPosition.get(fragmentName) + ", fragname = " + fragmentName);
        currentPosition = fragmentNameToMenuPosition.get(fragmentName);
        setActionBarTitle(fragmentName);
    }

    @Override
    public void onRegistationtInteraction(String name, String sirname, String email, String phone, String password) {
        showNewFragment("CardFragment", true);
    }

    @Override
    public void onCardFragmentInteraction(Uri uri) {

    }

    // слушатель нажатия на элемент левого меню
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // что будет происходить при нажатии:
            // 1 - переключение фрагмента во фрейме, 2 - изменение заголовка, 3 - закрывать меню
            String fragmentName;
            switch (position) {
                case 1:
                    fragmentName = "SearchFragment";
                    break;
                case 2:
                    fragmentName = "MyDealsFragment";
                    break;
                case 3:
                    fragmentName = "SettingsFragment";
                    break;
                default:
                    fragmentName = "AdvertListFragment";
                    break;
            }
            // управление транзациями фрагментов
            showNewFragment(fragmentName, true);
            // изменение заголовка

            drawerLayout.closeDrawer(GravityCompat.START);
            // запоминаю текущую позицию только в конце, потому что сравнением текущей и поступившей позиций решаю, а добавлять ли в стек фрагмент?
        }
    }

    // это про actionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Заполнение меню; элементы (если они есть) добавляются на панель действий.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // создаем ActionBarDrawerToggle, который будет слушать события в layout
        // сразу же перегружаем в нём методы, которые отрабатывают при открытии и закрытии панели
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer) {
            Toast toast;

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                toast = Toast.makeText(getApplicationContext(), "open", Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
                toast = Toast.makeText(getApplicationContext(), "close", Toast.LENGTH_SHORT);
                toast.show();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);  // так мы связыем слушатель и сам DrawerLayout
        return super.onCreateOptionsMenu(menu);
    }

    // обработка нажатий на
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //MenuItem - это объект, на который был сделан щелчок, на панели действий
        switch (item.getItemId()) {
            case R.id.top_menu_search:
                // показ подробного поиска
                showNewFragment("SearchFragment", true);
                return true;
            // так получаем доступ к кнопке домой
            case android.R.id.home:
                // как-то достать списочный фрагмент из стека ( а он там всегда есть), показать его, а остальные удалить или нет?
                showNewFragment("AdvertListFragment", true);
                return true;   // т.е. обработали щелчок
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    /**
     * Нажали на объявление
     * @param item
     */
    @Override
    public void onAdvertListInteraction(AdvertContent.AdvertItem item) {
        if (!IS_ADVERT_CHOOSEN){
            IS_ADVERT_CHOOSEN = true;
            AdvertItemFragment.ADVERT_ID = item.id;
            showNewFragment("AdvertItemFragment", true);
        }
    }


    @Override
    public void onAvatar(String userId) {
        if (!IS_USER_CHOOSEN){
            IS_USER_CHOOSEN = true;
            UserProfileFragment.USER_ID = userId;
            showNewFragment("UserProfileFragment", true);
        }
    }

    @Override
    public void onMakeDeal(String adverId) {
        // понимать, что у пользователя с авторизацией лучше ч/з сервер, а то мы будем думать одно, сервак скажет другое..
        // TODO: 20.11.2016 для теста
        boolean where_to_go = false;
        // если авторизованы - дальше
        if (where_to_go) {

        }
        //если нет - регистрация/авторизация
        else {
            showNewFragment("RegistrationFragment", true);
        }
        Toast.makeText(this, "авторизованы или нет??", Toast.LENGTH_SHORT).show();
    }

    /**
     * Для загрузки с сервера объявлений
     */
    class LoadAdvertsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(M_AC_LOG, "stared loading adverts");
            return null;
        }
    }

    /**
     * Все транзакции фрагментов должны происходить только через этот метод.
     * @param fragmentName
     * @param backStackAdding
     */
    private void showNewFragment(String fragmentName, boolean backStackAdding) {
        Fragment fragment;
        switch (fragmentName) {
            case "AdvertListFragment":
                fragment = new AdvertListFragment();
                break;
            case "AdvertItemFragment":
                fragment = new AdvertItemFragment();
                break;
            case "UserProfileFragment":
                fragment = new UserProfileFragment();
                break;
            case "SearchFragment":
                fragment = new SearchFragment();
                break;
            case "MyDealsFragment":
                fragment = new MyDealsFragment();
                break;
            case "SettingsFragment":
                fragment = new SettingsFragment();
                break;
            case "RegistrationFragment":
                fragment = new RegistrationFragment();
                break;
            case "CardFragment":
                fragment = new CardFragment();
                break;
            default:
                fragment = new AdvertListFragment();
                break;
        }
        if (currentPosition != fragmentNameToMenuPosition.get(fragmentName)) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame_for_fragments, fragment);
            if (backStackAdding) {
                ft.addToBackStack(null);
            }
            ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        }

    }

    private void setActionBarTitle(String fragmentName) {
        String title;
        if (fragmentNameToTitleText.containsKey(fragmentName)) {
            title = fragmentNameToTitleText.get(fragmentName);
        } else {
            title = getResources().getString(R.string.app_name);
        }
        actionBar.setTitle(title);
    }

    private void initFragmentMenuMap() {
        // для тех фрагментов, которые есть в правом меню, порядок должен совпадать с порядком в этом меню
        List<String> fragmentNames = Arrays.asList("AdvertListFragment", "SearchFragment", "MyDealsFragment", "SettingsFragment", "AdvertItemFragment", "UserProfileFragment", "RegistrationFragment", "CardFragment");
        int i = 0;
        for (String name : fragmentNames) {
            fragmentNameToMenuPosition.put(name, i);
            i++;
        }
    }

    private void initFragmentTitleMap() {
        fragmentNameToTitleText.put("AdvertListFragment", getResources().getString(R.string.AdvertListFragment));
        fragmentNameToTitleText.put("SearchFragment", getResources().getString(R.string.SearchFragment));
        fragmentNameToTitleText.put("MyDealsFragment", getResources().getString(R.string.MyDealsFragment));
        fragmentNameToTitleText.put("SettingsFragment", getResources().getString(R.string.SettingsFragment));
        fragmentNameToTitleText.put("UserProfileFragment", getResources().getString(R.string.UserProfileFragment));
        fragmentNameToTitleText.put("AdvertItemFragment", getResources().getString(R.string.AdvertItemFragment));
        fragmentNameToTitleText.put("AdvertItemFragment", getResources().getString(R.string.RegistrationFragment));
        fragmentNameToTitleText.put("AdvertItemFragment", getResources().getString(R.string.CardFragment));
    }

}
