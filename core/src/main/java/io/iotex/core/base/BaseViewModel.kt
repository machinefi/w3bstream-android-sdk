package io.iotex.core.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus

abstract class BaseViewModel: ViewModel() {

    private val composite = CompositeDisposable()

    init {
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
    }

    protected fun addDisposable(d: Disposable) {
        composite.add(d)
    }

    protected open fun useEventBus() = false

    override fun onCleared() {
        super.onCleared()
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
        composite.dispose()
    }

}