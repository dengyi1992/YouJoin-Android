package me.zq.youjoin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.zq.youjoin.R;
import me.zq.youjoin.model.ImageInfo;
import me.zq.youjoin.model.ResultInfo;
import me.zq.youjoin.network.NetworkManager;
import me.zq.youjoin.network.ResponseListener;
import me.zq.youjoin.utils.LogUtils;
import me.zq.youjoin.widget.enter.AutoHeightGridView;
import me.zq.youjoin.widget.enter.EmojiFragment;
import me.zq.youjoin.widget.enter.EnterEmojiLayout;
import me.zq.youjoin.widget.enter.EnterLayout;

public class PublishActivity extends BaseActivity implements EmojiFragment.EnterEmojiLayout {

    public static final int PHOTO_MAX_COUNT = 9;

    @Bind(R.id.comment)
    EditText yjPublishEdit;
    @Bind(R.id.lay_photo_container)
    LinearLayout layPhotoContainer;
    @Bind(R.id.gridView)
    AutoHeightGridView gridView;
    @Bind(R.id.popPhoto)
    ImageButton btnPopPhoto;
    @Bind(R.id.btn_send)
    ImageButton btnSend;

    ArrayList<String> mSelectPath;
    ArrayList<ImageInfo> mData = new ArrayList<>();
    LayoutInflater mInflater;
    EnterEmojiLayout enterLayout;
    EditText msgEdit;

    private boolean mFirstFocus = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yj_activity_publish);
        ButterKnife.bind(this);

        btnPopPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiImageSelectorActivity.startSelect(PublishActivity.this, 2, 9,
                        MultiImageSelectorActivity.MODE_MULTI);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkManager.postSendTweet(msgEdit.getText().toString(), mData,
                        new ResponseListener<ResultInfo>() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        LogUtils.e("hehe", volleyError.toString());
                    }

                    @Override
                    public void onResponse(ResultInfo result) {
                        Toast.makeText(PublishActivity.this, result.getResult(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        initEnter();
    }

    private void initEnter() {
        enterLayout = new EnterEmojiLayout(this, null);
        msgEdit = enterLayout.content;
        enterLayout.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterLayout.popKeyboard();
            }
        });
        enterLayout.content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mFirstFocus && hasFocus) {
                    mFirstFocus = false;
                    enterLayout.popKeyboard();
                }
            }
        });

    }

    @Override
    protected void onStop() {
        enterLayout.closeEnterPanel();
        super.onStop();
    }

    @Override
    public EnterLayout getEnterLayout() {
        return enterLayout;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
//                StringBuilder sb = new StringBuilder();
                layPhotoContainer.removeAllViews();
                for (String p : mSelectPath) {
//                    sb.append(p);
//                    sb.append("\n");

                    View itemView = View.inflate(PublishActivity.this, R.layout.yj_item_publish_photo, null);
                    ImageView img = (ImageView) itemView.findViewById(R.id.img);
                    itemView.setTag(p);

                    Picasso.with(PublishActivity.this)
                            .load(new File(p))
                            .resize(200, 200)
                            .centerCrop()
                            .into(img);
                    if (layPhotoContainer != null) {
                        layPhotoContainer.addView(itemView,
                                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));
                    }

                    mData.add(new ImageInfo(p));
                }
                //yjPublishEdit.setText(sb.toString());
            }
        }
    }


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, PublishActivity.class);
        context.startActivity(intent);
    }
}
