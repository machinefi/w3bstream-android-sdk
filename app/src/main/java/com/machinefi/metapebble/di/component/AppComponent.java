package com.machinefi.metapebble.di.component;

import com.machinefi.metapebble.app.PebbleApp;
import com.machinefi.metapebble.di.module.ActivityModule;
import com.machinefi.metapebble.di.module.AppModule;
import com.machinefi.metapebble.di.module.FragmentModule;
import com.machinefi.metapebble.di.module.ViewModelModule;
import com.machinefi.metapebble.di.scope.AppScope;

import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

@AppScope
@Component(modules = {AndroidSupportInjectionModule.class,
                AppModule.class,
                ActivityModule.class,
                FragmentModule.class,
                ViewModelModule.class,})
public interface AppComponent {

    void inject(PebbleApp app);

}
