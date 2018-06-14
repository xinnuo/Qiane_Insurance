package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.load_Linear
import com.ruanmeng.base.refresh
import com.ruanmeng.model.CommonData
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.include
import org.jetbrains.anko.verticalLayout
import java.util.ArrayList

class ClientBirthActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout { include<View>(R.layout.layout_list) }
        init_title("生日提醒")

        list.add(CommonData("1"))
        list.add(CommonData("2"))
        mAdapter.updateData(list)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关生日提醒！"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh)

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_birth_list) { data, injector ->

                    val position = list.indexOf(data)
                    val isLast = list.indexOf(data) == list.size - 1

                    injector.text(R.id.item_birth_name, "姓名")
                            .text(R.id.item_birth_age, "0岁生日")
                            .text(R.id.item_birth_day, "0天")

                            .visibility(R.id.item_birth_divider1, if (isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_birth_divider2, if (!isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_birth_divider3, if (position != 0) View.GONE else View.VISIBLE)
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        swipe_refresh.isRefreshing = false
    }
}
