package com.routee.qianbaotest.view.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.routee.qianbaotest.R
import exocr.bankcard.CardRecoActivity
import exocr.bankcard.EXBankCardInfo
import exocr.exocrengine.DictManager
import exocr.exocrengine.EXIDCardResult
import exocr.idcard.CaptureActivity

class OcrActivity : AppCompatActivity() {
    private var SCAN_BANK_REQUESTCODE = 100
    private var SCAN_ID_REQUESTCODE = 101
    lateinit var mIv: ImageView
    lateinit var mTv: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr)
        initView()
        DictManager.InitDict(this)
    }

    private fun initView() {
        mIv = findViewById(R.id.iv) as ImageView
        mTv = findViewById(R.id.tv) as TextView
    }

    fun scanId(view: View) {
        startActivityForResult(Intent(this, CaptureActivity::class.java), SCAN_ID_REQUESTCODE)
    }

    fun scanBank(view: View) {
        var intent = Intent(this, CardRecoActivity::class.java)
        intent.putExtra("ShouldFront", true)
        startActivityForResult(intent, SCAN_BANK_REQUESTCODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SCAN_ID_REQUESTCODE) {
            getIDCardSuccessfull(resultCode, data)
        } else if (requestCode == SCAN_BANK_REQUESTCODE) {
            getBankSuccessfull(resultCode, data)
        }
    }

    private fun getBankSuccessfull(resultCode: Int, data: Intent?) {
        if (data != null && data.hasExtra(exocr.bankcard.CardRecoActivity.BANK_RECO_RESULT)) {
            if (resultCode == exocr.bankcard.CardRecoActivity.BANK_RETURN_RESULT) {
                val extras = data.extras
                if (extras != null) {
                    val recoResult = extras.getParcelable<EXBankCardInfo>(exocr.bankcard.CardRecoActivity.BANK_RECO_RESULT) // 识别结果
//                    val finalResult = extras
//                            .getParcelable<EXBankCardInfo>(exocr.bankcard.CardRecoActivity.BANK_FINAL_RESULT) // 最终结果，可能被修改过
//                    val edited = extras.getBoolean(exocr.bankcard.CardRecoActivity.BANK_EDITED) // 是否修改过
//                    Log.d("xxx", "recogResult:" + recoResult!!.toString())
//                    Log.d("xxx", "finalResult:" + finalResult!!.toString())
//                    Log.d("xxx", "edited:" + edited)
                    // 获取照片
                    val bankCardImage: Bitmap = exocr.bankcard.CardRecoActivity.markedCardImage
                    val bankFullImage: Bitmap = exocr.bankcard.CardRecoActivity.cardFullImage
                    mIv.setImageBitmap(bankFullImage)
                    mTv.setText(recoResult.strNumbers)
                }
            }
        }
    }

    private fun getIDCardSuccessfull(resultCode: Int, data: Intent?) {
        val extras = data!!.getExtras()
        if (extras != null) {
            var capture = extras.getParcelable<EXIDCardResult>(CaptureActivity.EXTRA_SCAN_RESULT)
//            获取姓名
//            nameValue.setText(capture.name)
//            获取性别
//            sexValue.setText(capture.sex)
//            获取名族
//            nationValue.setText(capture.nation)
//            获取生日
//            birthdayValue.setText(capture.birth)
//            获取地址
//            addressValue.setText(capture.address)
            mTv.setText(capture.cardnum)
            mIv.setImageBitmap(CaptureActivity.IDCardFrontFullImage)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        DictManager.FinishDict()
    }
}