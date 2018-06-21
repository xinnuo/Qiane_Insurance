package com.ruanmeng.qiane_insurance

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.model.CommonData
import com.ruanmeng.view.FullyLinearLayoutManager
import kotlinx.android.synthetic.main.activity_client_add.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.startActivity

class ClientAddActivity : BaseActivity() {

    private val listTel = ArrayList<CommonData>()
    private val listEmail = ArrayList<CommonData>()
    private val listLicense = ArrayList<CommonData>()
    private val listAddress = ArrayList<CommonData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_add)
        init_title("手动添加", "完成")

        (client_tel_list.adapter as SlimAdapter).updateData(listTel)
        (client_email_list.adapter as SlimAdapter).updateData(listEmail)
        (client_license_list.adapter as SlimAdapter).updateData(listLicense)
        (client_addr_list.adapter as SlimAdapter).updateData(listAddress)
    }

    override fun init_title() {
        super.init_title()
        client_tel_list.apply {
            isNestedScrollingEnabled = false
            layoutManager = FullyLinearLayoutManager(baseContext)
            adapter = SlimAdapter.create()
                    .register<CommonData>(R.layout.item_client_tel_list) { data, injector ->
                        injector.text(R.id.item_tel_input, "")
                                .clicked(R.id.item_tel_close) {
                                    val index = listTel.indexOf(data)
                                    listTel.remove(data)
                                    (this.adapter as SlimAdapter).notifyItemRemoved(index)
                                }
                    }
                    .attachTo(this)
        }

        client_email_list.apply {
            isNestedScrollingEnabled = false
            layoutManager = FullyLinearLayoutManager(baseContext)
            adapter = SlimAdapter.create()
                    .register<CommonData>(R.layout.item_client_email_list) { data, injector ->
                        injector.text(R.id.item_email_input, "")
                                .clicked(R.id.item_email_close) {
                                    val index = listEmail.indexOf(data)
                                    listEmail.remove(data)
                                    (this.adapter as SlimAdapter).notifyItemRemoved(index)
                                }
                    }
                    .attachTo(this)
        }

        client_license_list.apply {
            isNestedScrollingEnabled = false
            layoutManager = FullyLinearLayoutManager(baseContext)
            adapter = SlimAdapter.create()
                    .register<CommonData>(R.layout.item_client_license_list) { data, injector ->
                        injector.text(R.id.item_license_input, "")
                                .clicked(R.id.item_license_close) {
                                    val index = listLicense.indexOf(data)
                                    listLicense.remove(data)
                                    (this.adapter as SlimAdapter).notifyItemRemoved(index)
                                }
                    }
                    .attachTo(this)
        }

        client_addr_list.apply {
            isNestedScrollingEnabled = false
            layoutManager = FullyLinearLayoutManager(baseContext)
            adapter = SlimAdapter.create()
                    .register<CommonData>(R.layout.item_client_address_list) { data, injector ->
                        injector.text(R.id.item_address_input, "")
                                .clicked(R.id.item_address_close) {
                                    val index = listAddress.indexOf(data)
                                    listAddress.remove(data)
                                    (this.adapter as SlimAdapter).notifyItemRemoved(index)
                                }
                    }
                    .attachTo(this)
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.client_tel -> {
                val index = listTel.size
                listTel.add(CommonData("$index"))
                (client_tel_list.adapter as SlimAdapter).notifyItemInserted(index)
            }
            R.id.client_email -> {
                val index = listEmail.size
                listEmail.add(CommonData("$index"))
                (client_email_list.adapter as SlimAdapter).notifyItemInserted(index)
            }
            R.id.client_license -> {
                val index = listLicense.size
                listLicense.add(CommonData("$index"))
                (client_license_list.adapter as SlimAdapter).notifyItemInserted(index)
            }
            R.id.client_addr -> {
                val index = listAddress.size
                listAddress.add(CommonData("$index"))
                (client_addr_list.adapter as SlimAdapter).notifyItemInserted(index)
            }
            R.id.client_bank_ll -> startActivity<ClientBankActivity>()
        }
    }
}
