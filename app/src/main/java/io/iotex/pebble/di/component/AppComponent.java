package io.iotex.pebble.di.component;

import io.iotex.pebble.di.module.ActivityModule;
import io.iotex.pebble.di.module.AppModule;
import io.iotex.pebble.di.module.FragmentModule;
import io.iotex.pebble.di.module.ViewModelModule;
import io.iotex.pebble.di.scope.AppScope;

import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;
import io.iotex.pebble.app.PebbleApp;

@AppScope
@Component(modules = {AndroidSupportInjectionModule.class,
                AppModule.class,
                ActivityModule.class,
                FragmentModule.class,
                ViewModelModule.class,})
public interface AppComponent {

    void inject(PebbleApp app);

}
