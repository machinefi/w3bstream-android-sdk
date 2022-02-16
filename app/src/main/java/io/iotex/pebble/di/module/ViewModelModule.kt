package io.iotex.pebble.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.iotex.pebble.di.key.ViewModelKey
import io.iotex.pebble.module.viewmodel.WalletVM

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(WalletVM::class)
    abstract fun walletVM(vm: WalletVM): ViewModel

}