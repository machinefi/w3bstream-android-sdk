package io.iotex.pebble.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RxUtil {

    companion object {

        fun clicks(view: View?): Observable<String> {
            return ViewClickObservable(view)
        }

        fun textChange(view: TextView?): Observable<String> {
            return TextChangeObservable(view)
        }

        fun <T> applySchedulers(): ObservableTransformer<T, T> {
            return ObservableTransformer<Any, Any> {
                it.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            } as ObservableTransformer<T, T>
        }
    }

}

class ViewClickObservable(val view: View?) : Observable<String>() {

    override fun subscribeActual(observer: Observer<in String>?) {
        view?.setOnClickListener {
            observer?.onNext("onClick")
        }
    }

}

class TextChangeObservable(val view: TextView?) : Observable<String>() {


    override fun subscribeActual(observer: Observer<in String>?) {
        view?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                observer?.onNext(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }


}