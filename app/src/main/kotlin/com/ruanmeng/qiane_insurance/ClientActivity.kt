package com.ruanmeng.qiane_insurance

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.load_Linear
import com.ruanmeng.base.refresh
import com.ruanmeng.base.startActivity
import com.ruanmeng.model.CommonData
import com.ruanmeng.view.NormalDecoration
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import net.idik.lib.slimadapter.SlimAdapterEx
import org.jetbrains.anko.dip
import org.jetbrains.anko.sp
import java.util.ArrayList

class ClientActivity : BaseActivity() {

    private val list = ArrayList<CommonData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)
        init_title("客户", "搜索客户")

        getData()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("InflateParams")
    override fun init_title() {
        super.init_title()
        swipe_refresh.refresh { getData() }
        recycle_list.load_Linear(baseContext, swipe_refresh)

        recycle_list.addItemDecoration(object : NormalDecoration() {
            override fun getHeaderName(pos: Int): String? = if (pos == 0) null else list[pos - 1].letter
        }.apply {
            setHeaderContentColor(resources.getColor(R.color.background))
            setHeaderHeight(dip(25))
            setTextSize(sp(13))
            setTextColor(resources.getColor(R.color.gray))
        })

        mAdapterEx = SlimAdapter.create(SlimAdapterEx::class.java)
                .apply {
                    val view = LayoutInflater.from(baseContext).inflate(R.layout.header_client, null)
                    addHeader(view)
                }
                .register<CommonData>(R.layout.item_client_list) { data, injector ->

                    val isLast = list.indexOf(data) == list.size - 1

                    injector.text(R.id.item_client_name, data.title)
                            .visibility(
                                    R.id.item_client_divider1,
                                    if ((!isLast && data.letter != list[list.indexOf(data) + 1].letter) || isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_client_divider2, if (!isLast) View.GONE else View.VISIBLE)
                }
                .attachTo(recycle_list)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.tv_nav_right -> startActivity<ClientSearchActivity>()
            R.id.client_birth_ll -> startActivity<ClientBirthActivity>()
            R.id.client_form_ll -> startActivity<ClientFormActivity>()
            R.id.client_edit -> startActivity<ClientEditActivity>()
            R.id.client_input -> startActivity<ClientAddActivity>()
            R.id.client_contact -> startActivity<ClientAddActivity>()
        }
    }

    override fun getData() {
        swipe_refresh.isRefreshing = false

        list.clear()
        list.add(CommonData().apply {
            title = "李晓明1"
            letter = "L"
        })
        list.add(CommonData().apply {
            title = "李晓明2"
            letter = "L"
        })
        list.add(CommonData().apply {
            title = "李晓明3"
            letter = "L"
        })
        list.add(CommonData().apply {
            title = "李晓明4"
            letter = "L"
        })
        list.add(CommonData().apply {
            title = "李晓明5"
            letter = "L"
        })
        list.add(CommonData().apply {
            title = "李晓明6"
            letter = "L"
        })
        list.add(CommonData().apply {
            title = "李健熙"
            letter = "L"
        })
        list.add(CommonData().apply {
            title = "王晓静1"
            letter = "W"
        })
        list.add(CommonData().apply {
            title = "王晓静2"
            letter = "W"
        })
        list.add(CommonData().apply {
            title = "王晓静3"
            letter = "W"
        })
        list.add(CommonData().apply {
            title = "王晓静4"
            letter = "W"
        })
        list.add(CommonData().apply {
            title = "王晓静5"
            letter = "W"
        })
        list.add(CommonData().apply {
            title = "王晓静6"
            letter = "W"
        })
        list.add(CommonData().apply {
            title = "王晓静7"
            letter = "W"
        })
        list.add(CommonData().apply {
            title = "张金霞"
            letter = "Z"
        })
        list.add(CommonData().apply {
            title = "张亚轩"
            letter = "Z"
        })
        list.add(CommonData().apply {
            title = "张靖宇"
            letter = "Z"
        })
        list.add(CommonData().apply {
            title = "张心雨"
            letter = "Z"
        })

        mAdapterEx.updateData(list)
    }
}
