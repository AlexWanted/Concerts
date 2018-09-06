package ru.rewindforce.concerts;

import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import ru.rewindforce.concerts.Homepage.ConcertsOverviewFragment;

public class HomepageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        setStatusBarTranslucent(false);
        if (getSupportFragmentManager().findFragmentByTag("concerts_overview") == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            ConcertsOverviewFragment overviewFragment = ConcertsOverviewFragment.newInstance();
            transaction.add(R.id.fragment_container, overviewFragment, "concerts_overview");
            transaction.commit();
        }
    }

    /**
     * Метод для изменения прозрачности и цвета статус бара.
     * Не работает на версии SDK меньше 19.
     *
     * @param enable - включена ли прозрачность. [true, false]
     */
    protected void setStatusBarTranslucent(boolean enable) {
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (enable) getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
