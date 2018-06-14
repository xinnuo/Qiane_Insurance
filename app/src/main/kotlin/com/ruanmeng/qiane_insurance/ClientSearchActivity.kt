package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.load_Linear
import com.ruanmeng.model.CommonData
import com.ruanmeng.view.NormalDecoration
import kotlinx.android.synthetic.main.activity_client_search.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.dip
import org.jetbrains.anko.sp
import java.util.ArrayList

class ClientSearchActivity : BaseActivity() {

    private val list = ArrayList<CommonData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_search)
        setToolbarVisibility(false)
        init_title()

        getData()
    }

    @Suppress("DEPRECATION")
    override fun init_title() {
        client_list.load_Linear(baseContext)

        client_list.addItemDecoration(object : NormalDecoration() {
            override fun getHeaderName(pos: Int): String = list[pos].letter
        }.apply {
            setHeaderContentColor(resources.getColor(R.color.background))
            setHeaderHeight(dip(25))
            setTextSize(sp(13))
            setTextColor(resources.getColor(R.color.gray))
        })

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_client_list) { data, injector ->

                    val isLast = list.indexOf(data) == list.size - 1

                    injector.text(R.id.item_client_name, data.title)
                            .visibility(
                                    R.id.item_client_divider1,
                                    if ((!isLast && data.letter != list[list.indexOf(data) + 1].letter) || isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_client_divider2, if (!isLast) View.GONE else View.VISIBLE)
                }
                .attachTo(client_list)

        val letters = listOf("A", "B", "C", "D", "E", "F", "G",
                "H", "I", "J", "K", "L", "M", "N",
                "O", "P", "Q", "R", "S", "T", "U",
                "V", "W", "X", "Y", "Z", "#")
        client_index.setIndexBarHeightRatio(0.9f)
        client_index.indexBar.apply {
            setNorTextColor(resources.getColor(R.color.gray))
            setSelTextColor(resources.getColor(R.color.black))
            setIndexsList(letters)
            setIndexChangeListener { name ->
                val itemIndex = list.indexOfFirst { it.letter == name }
                if (itemIndex > -1)
                    (client_list.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(itemIndex, 0)
            }
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.client_cancel -> client_edit.setText("")
        }
    }

    override fun getData() {
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

        mAdapter.updateData(list)
    }
}
