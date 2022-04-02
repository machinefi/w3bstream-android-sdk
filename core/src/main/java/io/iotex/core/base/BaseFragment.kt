package io.iotex.core.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import io.iotex.core.base.service.IFragment
import javax.inject.Inject

abstract class BaseFragment(private val layoutId: Int) : Fragment(), IFragment {

    protected val TAG = this::class.java.name

    @Inject
    lateinit var mVmFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var contentView: View? = null
        if (layoutId > 0) {
            contentView = inflater.inflate(layoutId, container, false)
        }
        return contentView ?: super.onCreateView(inflater, container, savedInstanceState)
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerObserver()

        initView(view, savedInstanceState)
    }

    final override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData(savedInstanceState)
    }

}