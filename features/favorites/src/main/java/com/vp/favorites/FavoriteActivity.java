package com.vp.favorites;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;
import javax.inject.Inject;

public class FavoriteActivity extends AppCompatActivity implements HasAndroidInjector {

    @Inject
    DispatchingAndroidInjector<Object> dispatchingActivityInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new FavoriteListFragment(), FavoriteListFragment.TAG)
                .commit();
        }
    }
    @Override
    public AndroidInjector<Object> androidInjector() {
        return dispatchingActivityInjector;
    }
}
