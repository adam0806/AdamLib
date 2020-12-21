package com.adam.lib.basic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.adam.lib.util.LogUtils


/**
 * Created By Adam on 2020/11/25
 */
abstract class BasicFragment : Fragment() {
    protected var TAG = javaClass.simpleName
    private var mBaseRootView: View? = null
    private var mIsFirst = true
    private var mIsVisible = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        LogUtils.d(TAG, "Lifecycle, onCreateView: $TAG", LogUtils.LIFECYCLE)
        mBaseRootView = inflater.inflate(getLayoutResId(), container, false)
        return mBaseRootView
    }
    abstract fun getLayoutResId() : Int

    override fun onAttach(context: Context) {
        super.onAttach(context)
        LogUtils.d(TAG, "Lifecycle, onAttach: $TAG", LogUtils.LIFECYCLE)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtils.d(TAG, "Lifecycle, onCreate: $TAG", LogUtils.LIFECYCLE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtils.d(TAG, "Lifecycle, onViewCreated: $TAG", LogUtils.LIFECYCLE)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(mIsVisible && mIsFirst) {
            mIsFirst = false
            lazyLoad()
        }
        LogUtils.d(TAG, "Lifecycle, onActivityCreated: $TAG", LogUtils.LIFECYCLE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LogUtils.d(TAG, "Lifecycle, onActivityResult: $TAG", LogUtils.LIFECYCLE)
    }
    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        LogUtils.d(TAG, "Lifecycle, onAttachFragment: $TAG", LogUtils.LIFECYCLE)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        mIsVisible = isVisible
        if(mIsVisible){
            if(mIsFirst) {
                mIsFirst = false
                lazyLoad()
            }
            show()
        }else{
            hide()
        }
        LogUtils.d(TAG, "Lifecycle, setUserVisibleHint($isVisibleToUser): $TAG", LogUtils.LIFECYCLE)
    }
    abstract fun lazyLoad()

    fun show(){

    }
    fun hide(){

    }
    override fun onStart() {
        super.onStart()
        LogUtils.d(TAG, "Lifecycle, onStart: $TAG", LogUtils.LIFECYCLE)
    }
    override fun onResume() {
        super.onResume()
        LogUtils.d(TAG, "Lifecycle, onResume: $TAG", LogUtils.LIFECYCLE)
    }
    override fun onPause() {
        super.onPause()
        LogUtils.d(TAG, "Lifecycle, onPause: $TAG", LogUtils.LIFECYCLE)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        LogUtils.d(TAG, "Lifecycle, onSaveInstanceState: $TAG", LogUtils.LIFECYCLE)
    }
    override fun onStop() {
        super.onStop()
        LogUtils.d(TAG, "Lifecycle, onStop: $TAG", LogUtils.LIFECYCLE)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        LogUtils.d(TAG, "Lifecycle, onAttach: $TAG", LogUtils.LIFECYCLE)
    }
    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d(TAG, "Lifecycle, onDestroy: $TAG", LogUtils.LIFECYCLE)
    }
    override fun onDetach() {
        super.onDetach()
        LogUtils.d(TAG, "Lifecycle, onDetach: $TAG", LogUtils.LIFECYCLE)
    }
}