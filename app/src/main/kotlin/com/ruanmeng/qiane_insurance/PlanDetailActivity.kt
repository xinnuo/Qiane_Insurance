package com.ruanmeng.qiane_insurance

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.visible
import com.ruanmeng.model.CommonData
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.startRotateAnimator
import com.ruanmeng.view.FullyLinearLayoutManager
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import kotlinx.android.synthetic.main.activity_plan_detail.*
import net.cachapa.expandablelayout.ExpandableLayout
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.sdk25.listeners.onClick
import org.jetbrains.anko.startActivity
import java.util.*
import kotlin.collections.ArrayList

class PlanDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_detail)
        init_title("产品详情")
    }

    override fun init_title() {
        super.init_title()
        ivRight.visible()

        val items = listOf("50万意外身故保障", "100%意外医疗报销", "突发性疾病")
        detail_type.adapter = object : TagAdapter<String>(items) {

            override fun getView(parent: FlowLayout, position: Int, item: String): View {
                val flowTitle = View.inflate(baseContext, R.layout.item_plan_type_flow, null) as TextView
                flowTitle.text = item
                return flowTitle
            }

        }

        val list = ArrayList<CommonData>()
        list.add(CommonData().apply {
            title = "年度总限额"
            type = "300万"
            content = "全年度报销总额度。"
        })
        list.add(CommonData().apply {
            title = "住院医疗费用"
            type = "200万"
            content = "费用包括：药品费、治疗费、手术费、检查检验费、护理费、救护车使用费、床位费、膳食费。购买时有社保：社保赔付后的报销金额=（医疗费用-已报销部分-1万元免赔）*100%。"
        })
        list.add(CommonData().apply {
            title = "指定门诊医疗费用"
            type = "200万"
            content = "门诊治疗包括：门诊肾透析；门诊恶性肿瘤治疗，包括化学疗法、放射疗法、肿瘤免疫疗法、肿瘤内分泌疗法、肿瘤靶向疗法；器官移植后抗排异治疗。"
        })
        list.add(CommonData().apply {
            title = "恶性肿瘤医疗保证金"
            type = "100万"
            content = "1、被保险人在等待期满后经确诊初次罹患本合同约定的恶性肿瘤，保险公司首先承担一般医疗保险金保险责任，当累计给付金额达到一般医疗保险金给付限额时，保险公司对被保险人剩余的医疗费用，承担恶性肿瘤医疗保险金责任。\n" +
                    "2、恶性肿瘤医疗保险金包含：恶性肿瘤住院医疗费用保险金、恶性肿瘤特殊门诊医疗费用保险金、恶性肿瘤门诊手术医疗费用保险金、恶性肿瘤住院前后门急诊医疗费用保险金。\n" +
                    "3、赔付比例：同一般医疗保险金。"
        })
        list.add(CommonData().apply {
            title = "等待期"
            type = "30天"
            content = "首次投保本保险或非连续投保本保险时，自本合同生效之日起30天为等待期；因意外伤害引起的保险事故，保险责任无等待期。续保无等待期。"
        })
        list.add(CommonData().apply {
            title = "生效时间"
            type = ""
            content = "投保成功后次日零时。"
        })
        detail_list.apply {
            isNestedScrollingEnabled = false
            layoutManager = FullyLinearLayoutManager(baseContext)
            adapter = SlimAdapter.create()
                    .register<CommonData>(R.layout.item_plan_detail_list) { data, injector ->
                        injector.text(R.id.item_detail_title, data.title)
                                .text(R.id.item_detail_des, data.type)
                                .text(R.id.item_detail_content, data.content)
                                .visibility(R.id.item_detail_divider, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)

                                .with<ExpandableLayout>(R.id.item_detail_expand) {
                                    if (data.isChecked) it.expand()
                                    else it.collapse()
                                }

                                .with<ImageView>(R.id.item_detail_icon) {
                                    if (data.isChecked) it.startRotateAnimator(0f, 180f)
                                    else it.startRotateAnimator(180f, 0f)
                                }

                                .clicked(R.id.item_detail) {
                                    data.isChecked = !data.isChecked
                                    (this.adapter as SlimAdapter).notifyItemChanged(list.indexOf(data))
                                }
                    }
                    .attachTo(this)
        }
        (detail_list.adapter as SlimAdapter).updateData(list)
    }

    @SuppressLint("InflateParams")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.plan_make -> startActivity<WebActivity>()
            R.id.detail_birth_img -> {
                val year = Calendar.getInstance().get(Calendar.YEAR)
                DialogHelper.showDateDialog(
                        baseContext,
                        year - 70,
                        year, 3,
                        "选择生日",
                        true, false) { _, _, _, _, _, date ->
                    detail_birth.text = date
                }
            }
            R.id.iv_nav_right -> {
                val view = LayoutInflater.from(baseContext).inflate(R.layout.dialog_share_bottom, null) as View
                val wechat = view.findViewById<LinearLayout>(R.id.dialog_share_wechat)
                val circle = view.findViewById<LinearLayout>(R.id.dialog_share_circle)
                val qq = view.findViewById<LinearLayout>(R.id.dialog_share_qq)
                val space = view.findViewById<LinearLayout>(R.id.dialog_share_space)
                val cancel = view.findViewById<Button>(R.id.dialog_share_cancel)
                val dialog = BottomSheetDialog(baseContext, R.style.BottomSheetDialogStyle)

                wechat.onClick { dialog.dismiss() }
                circle.onClick { dialog.dismiss() }
                qq.onClick { dialog.dismiss() }
                space.onClick { dialog.dismiss() }
                cancel.onClick { dialog.dismiss() }

                dialog.setContentView(view)
                dialog.show()
            }
        }
    }
}
