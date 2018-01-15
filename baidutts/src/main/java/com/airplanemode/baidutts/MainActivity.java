package com.airplanemode.baidutts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;

public class MainActivity extends AppCompatActivity {

    private EditText          mEt;
    private SpeechSynthesizer mSpeechSynthesizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEt = findViewById(R.id.et);
        initialTts();
    }

    //    AppID ="8125566";
    //    apiKey ="xNsVWzc4Ufy7YMQxco28lAgt";
    //    secretKey ="5c1be8f0c7b121d333308866c0191e78";

    protected void initialTts() {
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(this);
        mSpeechSynthesizer.setAppId("8125566"/*这里只是为了让Demo运行使用的APPID,请替换成自己的id。*/);
        mSpeechSynthesizer.setApiKey("xNsVWzc4Ufy7YMQxco28lAgt", "5c1be8f0c7b121d333308866c0191e78"/*这里只是为了让Demo正常运行使用APIKey,请替换成自己的APIKey*/);
        mSpeechSynthesizer.auth(TtsMode.MIX);
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "6");
        mSpeechSynthesizer.initTts(TtsMode.MIX);
    }

    public void speak(View view) {
        int i = 0;
        if (TextUtils.isEmpty(mEt.getText().toString())) {
            i = mSpeechSynthesizer.speak("你要叫我说啥呢");
        } else {
            i = mSpeechSynthesizer.speak(mEt.getText().toString());
        }
        Toast.makeText(this, "i ====" + i, Toast.LENGTH_SHORT).show();
    }
}
