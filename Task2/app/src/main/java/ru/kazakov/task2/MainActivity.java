package ru.kazakov.task2;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import ru.kazakov.task2.dummy.DummyContent;

public class MainActivity extends AppCompatActivity implements AdFragment.OnListFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // сразу же при старте активити запускаем наш списковый фрагмент
        Fragment fragment = new AdFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();

    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new ViewPagerFragment();
        Bundle arguement = new Bundle();
        arguement.putInt(ViewPagerFragment.CURRENT_PAGE_NUMBER, Integer.valueOf(item.id));
        fragment.setArguments(arguement);
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }
}
