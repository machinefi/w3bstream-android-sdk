package io.iotex.pebble.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.iotex.pebble.di.key.ViewModelKey
import io.iotex.pebble.module.viewmodel.ActivateVM
import io.iotex.pebble.module.viewmodel.AppVM
import io.iotex.pebble.module.viewmodel.PebbleVM

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(AppVM::class)
    abstract fun appVM(vm: AppVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PebbleVM::class)
    abstract fun pebbleVM(vm: PebbleVM): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(ActivateVM::class)
    abstract fun activateVM(vm: ActivateVM): ViewModel

}