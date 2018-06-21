package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.load_Linear
import com.ruanmeng.base.setOneClickListener
import com.ruanmeng.model.CommonData
import com.ruanmeng.view.NormalDecoration
import kotlinx.android.synthetic.main.activity_client_edit.*
import net.idik.lib.slimadapter.SlimAdapter
import net.idik.lib.slimadapter.SlimAdapterEx
import org.jetbrains.anko.*
import java.util.ArrayList

class ClientEditActivity : BaseActivity() {

    private val list = ArrayList<CommonData>()
    private lateinit var totalTV: TextView
    private var isEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_edit)
        init_title("编辑", "管理通讯录")

        getData()
    }

    @Suppress("DEPRECATION")
    override fun init_title() {
        super.init_title()
        client_list.load_Linear(baseContext)

        client_list.addItemDecoration(object : NormalDecoration() {
            override fun getHeaderName(pos: Int): String? = if (pos == 0) null else list[pos - 1].letter
        }.apply {
            setHeaderContentColor(resources.getColor(R.color.background))
            setHeaderHeight(dip(25))
            setTextSize(sp(13))
            setTextColor(resources.getColor(R.color.gray))
        })

        mAdapterEx = SlimAdapter.create(SlimAdapterEx::class.java)
                .addHeader(createView())
                .register<CommonData>(R.layout.item_client_list) { data, injector ->

                    val isLast = list.indexOf(data) == list.size - 1

                    injector.text(R.id.item_client_name, data.title)
                            .visibility(R.id.item_client_check, if (isEdit) View.VISIBLE else View.GONE)
                            .visibility(
                                    R.id.item_client_divider1,
                                    if ((!isLast && data.letter != list[list.indexOf(data) + 1].letter) || isLast) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_client_divider2, if (!isLast) View.GONE else View.VISIBLE)
                }
                .attachTo(client_list)

        tvRight.setOneClickListener(View.OnClickListener {
            isEdit = !isEdit
            tvRight.text = if (isEdit) "取消" else "管理通讯录"
            client_add.visibility = if (isEdit) View.GONE else View.VISIBLE
            client_edit.visibility = if (isEdit) View.VISIBLE else View.GONE
            mAdapterEx.notifyDataSetChanged()
        })
    }

    private fun createView(): View {
        return UI {
            verticalLayout {
                lparams(height = wrapContent)

                linearLayout {
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(dip(15), 0, 0, 0)
                    backgroundColorResource = R.color.white

                    themedTextView("所有客户", R.style.Font15_black)

                    totalTV = themedTextView("(0人)", R.style.Font13_black) {
                        textColorResource = R.color.colorAccent
                    }
                }.lparams(width = matchParent, height = dip(45))

                view { backgroundColorResource = R.color.divider }.lparams(height = dip(0.5f))
            }
        }.view
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.client_input -> startActivity<ClientAddActivity>()
            R.id.client_contact -> startActivity<ClientAddActivity>()
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

        mAdapterEx.updateData(list)
    }
}
