package com.ruanmeng.fragment

import android.graphics.Color
import android.os.Bundle
import android.support.v4.widget.NestedScrollView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.qiane_insurance.R
import kotlinx.android.synthetic.main.fragment_main_third.*
import org.jetbrains.anko.support.v4.dip

class MainThirdFragment : BaseFragment() {

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        this.view?.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_third, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()
    }

    override fun init_title() {
        third_title_ll.setBackgroundColor(Color.argb(0, 194, 13, 35))

        third_scroll.setOnScrollChangeListener { _: NestedScrollView, _: Int, scrollY: Int, _: Int, _: Int ->
            if (scrollY > dip(100)) {
                val color = Color.argb(255, 194, 13, 35)
                third_title_ll.setBackgroundColor(color)
            }
            else {
                val alpha = scrollY / dip(100) * 255
                val color = Color.argb(alpha, 194, 13, 35)
                third_title_ll.setBackgroundColor(color)
            }
        }
    }
}
