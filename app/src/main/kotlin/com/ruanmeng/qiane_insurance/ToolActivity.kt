package com.ruanmeng.qiane_insurance

import android.os.Bundle
import com.ruanmeng.base.BaseActivity

class ToolActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tool)
        init_title("展业工具")
    }
}
