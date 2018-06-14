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

class MessageActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout { include<View>(R.layout.layout_list) }
        init_title("消息")

        list.add(CommonData().apply {
            title = "2018-05-29"
            content = "邀请好友成功，您有20元红包到账，请及时查收。"
        })
        list.add(CommonData().apply {
            title = "2018-04-26"
            content = "恭喜您，资质认证已通过，赶快去推广吧，高奖励再向您招手，立即行动吧！"
        })
        list.add(CommonData().apply {
            title = "2018-04-26"
            content = "恭喜您，您已注册成功！"
        })

        mAdapter.updateData(list)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关消息信息！"
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_msg_list) { data, injector ->

                    val isLast = list.indexOf(data) == list.size - 1

                    injector.text(R.id.item_msg_time, data.title)
                            .text(R.id.item_msg_content, data.content)

                            .visibility(R.id.item_msg_divider1, if (isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_msg_divider2, if (!isLast) View.GONE else View.VISIBLE)
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        swipe_refresh.isRefreshing = false
    }
}
