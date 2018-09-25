package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.lzg.extend.BaseResponse
import com.lzg.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.include
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.verticalLayout
import java.util.ArrayList

class MessageActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verticalLayout { include<View>(R.layout.layout_list) }
        init_title("消息")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
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

                    injector.text(R.id.item_msg_time, data.sendDate)
                            .text(R.id.item_msg_content, when (data.msgType) {
                                "0", "2", "3" -> data.msgTitle
                                else -> data.content
                            })
                            .text(R.id.item_msg_title, when (data.msgType) {
                                "0" -> "精选活动"
                                "1" -> "通知消息"
                                "2" -> "资产消息"
                                "3" -> "互动消息"
                                else -> "消息"
                            })
                            .image(R.id.item_msg_img, when (data.msgType) {
                                "0" -> R.mipmap.icon_msg04
                                "2" -> R.mipmap.icon_msg03
                                "3" -> R.mipmap.icon_msg02
                                else -> R.mipmap.icon_msg01
                            })

                            .visibility(R.id.item_msg_divider1, if (isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_msg_divider2, if (!isLast) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_msg) {
                                startActivity<WebActivity>(
                                        "title" to "消息详情",
                                        "hint" to when (data.msgType) {
                                            "0" -> "精选活动"
                                            "1" -> "通知消息"
                                            "2" -> "资产消息"
                                            "3" -> "互动消息"
                                            else -> "消息"
                                        },
                                        "time" to data.sendDate,
                                        "content" to data.content)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.msg_list_data)
                .tag(this@MessageActivity)
                .headers("token", getString("token"))
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {

                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().`object`)
                            if (count(response.body().`object`) > 0) pageNum++
                        }

                        mAdapter.updateData(list)
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false

                        empty_view.apply { if (list.isEmpty()) visible() else gone() }
                    }

                })
    }
}
