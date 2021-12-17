package io.iotex.pebble.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class ViewModelFactory @Inject constructor(val mCreator: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>?) :
        ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        var creator = mCreator?.get(modelClass)
        if (creator == null) {
            creator = mCreator?.asIterable()?.first {
                modelClass.isAssignableFrom(it.key)
            }?.value
        }

        if (creator == null)
            throw IllegalArgumentException("unknown model class $modelClass")

        return creator.get() as T
    }

}