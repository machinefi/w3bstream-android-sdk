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

abstract class BaseFragment : Fragment(), IFragment {

    protected val TAG = this::class.java.name

    @Inject
    lateinit var mVmFactory: ViewModelProvider.Factory

//    protected var mLoadService: LoadService<View>? = null

//    protected val mLoadingDialog by lazy {
//        LoadingDailog.Builder(this.activity)
//                .setMessage("加载中...")
//                .setCancelable(false)
//                .setCancelOutside(true).create()
//    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var contentView: View? = null
        val layoutId = layoutResourceID(savedInstanceState)
        if (layoutId > 0) {
//            mLoadService = LoadSir.getDefault().register(inflater.inflate(layoutId, null)) as LoadService<View>?
//            contentView = mLoadService?.loadLayout
            contentView = inflater.inflate(layoutId, null)
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

    override fun onDestroyView() {
//        mLoadingDialog.dismiss()
        super.onDestroyView()
    }

}