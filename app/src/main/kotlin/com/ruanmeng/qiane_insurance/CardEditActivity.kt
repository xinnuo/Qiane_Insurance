package com.ruanmeng.qiane_insurance

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.lzg.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import kotlinx.android.synthetic.main.activity_card_edit.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import java.io.File

class CardEditActivity : BaseActivity() {

    private var selectList = ArrayList<LocalMedia>()
    private var companyId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_edit)
        init_title("编辑名片")

        EventBus.getDefault().register(this@CardEditActivity)

        getData()
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.edit_img_ll -> {
                PictureSelector.create(this@CardEditActivity)
                        .openGallery(PictureMimeType.ofImage())
                        .theme(R.style.picture_customer_style)
                        .maxSelectNum(1)
                        .minSelectNum(1)
                        .imageSpanCount(4)
                        .selectionMode(PictureConfig.MULTIPLE)
                        .previewImage(true)
                        .previewVideo(false)
                        .enablePreviewAudio(false)
                        .isCamera(true)
                        .imageFormat(PictureMimeType.PNG)
                        .isZoomAnim(true)
                        // glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                        // .sizeMultiplier(0.5f)
                        .setOutputCameraPath(Const.SAVE_FILE)
                        .compress(true)
                        .glideOverride(160, 160)
                        .enableCrop(true)
                        .withAspectRatio(1, 1)
                        .hideBottomControls(true)
                        .compressSavePath(cacheDir.absolutePath)
                        .freeStyleCropEnabled(false)
                        .circleDimmedLayer(false)
                        .showCropFrame(true)
                        .showCropGrid(true)
                        .isGif(false)
                        .openClickSound(false)
                        .selectionMedia(selectList.apply { clear() })
                        .previewEggs(true)
                        .minimumCompressSize(100)
                        .isDragFrame(false)
                        .forResult(PictureConfig.CHOOSE_REQUEST)
            }
            R.id.edit_company_ll -> startActivity<CompanyActivity>(
                    "type" to "编辑名片",
                    "companyId" to companyId)
            R.id.edit_wechat_ll -> {
                PictureSelector.create(this@CardEditActivity)
                        .openGallery(PictureMimeType.ofImage())
                        .theme(R.style.picture_customer_style)
                        .maxSelectNum(1)
                        .minSelectNum(1)
                        .imageSpanCount(4)
                        .selectionMode(PictureConfig.MULTIPLE)
                        .previewImage(true)
                        .previewVideo(false)
                        .enablePreviewAudio(false)
                        .isCamera(true)
                        .imageFormat(PictureMimeType.PNG)
                        .isZoomAnim(true)
                        // glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                        // .sizeMultiplier(0.5f)
                        .setOutputCameraPath(Const.SAVE_FILE)
                        .compress(true)
                        .glideOverride(160, 160)
                        .enableCrop(false)
                        .compressSavePath(cacheDir.absolutePath)
                        .isGif(false)
                        .openClickSound(false)
                        .selectionMedia(selectList.apply { clear() })
                        .previewEggs(true)
                        .minimumCompressSize(100)
                        .isDragFrame(false)
                        .forResult(PictureConfig.REQUEST_CAMERA)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data) as ArrayList<LocalMedia>
                    // LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的

                    if (selectList[0].isCompressed) getHeadData()
                }
                PictureConfig.REQUEST_CAMERA -> {
                    selectList = PictureSelector.obtainMultipleResult(data) as ArrayList<LocalMedia>
                    if (selectList[0].isCompressed) getQRcodeData()
                }
            }
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.user_business_card)
                .tag(this@CardEditActivity)
                .headers("token", getString("token"))
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body()).optJSONObject("object")
                                ?: JSONObject()
                        companyId = obj.optString("companyId")

                        loadUserHead(obj.optString("userhead"))
                        edit_name.text = obj.optString("realName")
                        edit_company.text = obj.optString("companyName")
                    }

                })
    }

    private fun getHeadData() {
        OkGo.post<String>(BaseHttp.userinfo_uploadhead_sub)
                .tag(this@CardEditActivity)
                .headers("token", getString("token"))
                .params("img", File(selectList[0].compressPath))
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        showToast(msg)
                        val userhead = JSONObject(response.body()).optString("object")
                        putString("userhead", userhead)
                        loadUserHead(userhead)
                    }

                })
    }

    private fun getCompanyData(Id: String, name: String) {
        OkGo.post<String>(BaseHttp.edit_company)
                .tag(this@CardEditActivity)
                .headers("token", getString("token"))
                .params("companyId", Id)
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        showToast(msg)
                        putString("companyId", Id)
                        putString("companyName", name)
                        EventBus.getDefault().post(RefreshMessageEvent("更新公司", Id, name))
                        edit_company.text = name
                    }

                })
    }

    private fun getQRcodeData() {
        OkGo.post<String>(BaseHttp.userinfo_uploadWechat_sub)
                .tag(this@CardEditActivity)
                .headers("token", getString("token"))
                .params("img", File(selectList[0].compressPath))
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        showToast(msg)
                    }

                })
    }

    private fun loadUserHead(path: String) = edit_img.loadImage(BaseHttp.baseImg + path)

    override fun finish() {
        EventBus.getDefault().unregister(this@CardEditActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.type) {
            "编辑名片" -> {
                companyId = event.id
                getCompanyData(event.id, event.name)
            }
        }
    }
}
