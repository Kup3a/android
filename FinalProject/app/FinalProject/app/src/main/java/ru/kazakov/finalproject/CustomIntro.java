package ru.kazakov.finalproject;

import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;

import ru.kazakov.finalproject.R;


public class CustomIntro extends AppIntro2 {
    @Override
    public void init(Bundle savedInstanceState) {

// Здесь указываем количество слайдов, например нам нужно 3
        addSlide(SampleSlide.newInstance(R.layout.onboarding_1)); //
        addSlide(SampleSlide.newInstance(R.layout.onboarding_2));
        addSlide(SampleSlide.newInstance(R.layout.onboarding_3));

    }

//    private void loadMainActivity(){
//        Toast.makeText(this, "loadMA", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//    }


    @Override
    public void onNextPressed() {
        // Do something here, нажатие на стрелку
    }

    @Override
    public void onDonePressed() {
        // изменившаяся стрелка
        MainActivity.IS_AFTER_ONBOARDING = true;
        finish();
    }

    @Override
    public void onSlideChanged() {
        // Do something here, изменение экрана
    }
}
