package com.machinefi.metapebble.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.machinefi.metapebble.di.key.ViewModelKey
import com.machinefi.metapebble.module.viewmodel.ActivateVM
import com.machinefi.metapebble.module.viewmodel.AppVM
import com.machinefi.metapebble.module.viewmodel.PebbleVM
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

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