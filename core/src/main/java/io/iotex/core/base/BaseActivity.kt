package io.iotex.core.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.iotex.core.base.service.IActivity
import javax.inject.Inject


abstract class BaseActivity : AppCompatActivity(), IActivity, HasAndroidInjector {

    @Inject
    lateinit var mVmFactory: ViewModelProvider.Factory

    @Inject
    lateinit var mAndroidInjector: DispatchingAndroidInjector<Any>

//    protected val mLoadingDialog by lazy {
//        LoadingDailog.Builder(this)
//                .setMessage("加载中...")
//                .setCancelable(false)
//                .setCancelOutside(true).create()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        beforeInflate(savedInstanceState)
        super.onCreate(savedInstanceState)

        val layoutId = layoutResourceID(savedInstanceState)
        if (layoutId > 0) {
            setContentView(layoutId)
        }

        registerObserver()

        initView(savedInstanceState)

        initData(savedInstanceState)
    }

    override fun beforeInflate(savedInstanceState: Bundle?) {
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return mAndroidInjector
    }

    override fun onDetachedFromWindow() {
//        mLoadingDialog.dismiss()
        super.onDetachedFromWindow()
    }
}