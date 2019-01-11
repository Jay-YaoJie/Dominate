package com.jeff.dominatelight.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jeff.dominatelight.R;
import com.jeff.dominatelight.manage.DataToHostManage;
import com.jeff.dominatelight.model.*;
import com.jeff.dominatelight.utils.GroupsDbUtils;
import com.jeff.dominatelight.utils.LightsDbUtils;
import com.jeff.dominatelight.utils.ScenesDbUtils;
import com.jeff.dominatelight.utils.TelinkCommon;
import com.jeff.dominatelight.view.TitleBar;


/**
 * Created by liucr on 2016/3/25.
 */
public class RenameActivity extends BaseActivity {

    @BindView(R.id.rename_titlebar)
    TitleBar titleBar;

    @BindView(R.id.rename_edit)
    EditText editText;

    @BindView(R.id.rename_tips)
    TextView renameTips;

    private Light light;

    private Group group;

    private Scene scene;

    private String curType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_rename);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        curType = bundle.getString(TelinkCommon.ACTIVITY_TYPE);
        if(TelinkCommon.ACTIVITY_TYPE_LIGHT_MESH.equals(curType+"")){
            int mesh = bundle.getInt(TelinkCommon.ACTIVITY_TYPE_LIGHT_MESH, -1);
            light = Lights.getInstance().getByMeshAddress(mesh);
        }else if(TelinkCommon.ACTIVITY_TYPE_GTOUP_MESH.equals(curType+"")){
            int mesh = bundle.getInt(TelinkCommon.ACTIVITY_TYPE_GTOUP_MESH, -1);
            group = Groups.getInstance().getByMeshAddress(mesh);
        }else if(TelinkCommon.ACTIVITY_TYPE_SCENE_MESH.equals(curType+"")){
            int mesh = bundle.getInt(TelinkCommon.ACTIVITY_TYPE_SCENE_MESH, -1);
            scene = Scenes.getInstance().getById(mesh);
        }
    }

    @Override
    protected void initView() {

        titleBar.getCenterText().setText(getString(R.string.rename_title));
        titleBar.getRightText().setText(getString(R.string.finish));
        titleBar.setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(TelinkCommon.ACTIVITY_TYPE_LIGHT_MESH.equals(curType+"")){
            initRenameLight();
        }else if(TelinkCommon.ACTIVITY_TYPE_GTOUP_MESH.equals(curType+"")){
            initRennameGroup();
        }else if(TelinkCommon.ACTIVITY_TYPE_SCENE_MESH.equals(curType+"")){
            initRennameScene();
        }

    }

    private void initRenameLight(){

        renameTips.setText(getString(R.string.rename_light_tips));
        editText.setHint(light.getLightSort().getName());
        editText.setSelection(editText.getText().length());

        titleBar.setOnClickRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(editText.getText().toString())){
                    showTipsDialog(getString(R.string.device_name_empty_tips), getString(R.string.enter));
                    return;
                }

                if(Lights.checkNameHad(editText.getText().toString(), light.getLightSort().getName())){
                    showTipsDialog(getString(R.string.device_name_had), getString(R.string.enter));
                    return;
                }

                light.getLightSort().setName(editText.getText().toString());
                LightsDbUtils.getInstance().updataOrInsert(light.getLightSort());
                DataToHostManage.updataCurToHost();
                finish();
            }
        });

    }

    private void initRennameGroup(){
        renameTips.setText(getString(R.string.reanme_group_tips));
        editText.setHint(group.getGroupSort().getName());
        editText.setSelection(editText.getText().length());

        titleBar.setOnClickRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(editText.getText().toString())){
                    showTipsDialog(getString(R.string.group_name_empty_tips), getString(R.string.enter));
                    return;
                }

                if(Groups.checkNameHad(editText.getText().toString(), group.getGroupSort().getName())){
                    showTipsDialog(getString(R.string.group_name_had), getString(R.string.enter));
                    return;
                }

                group.getGroupSort().setName(editText.getText().toString());
                GroupsDbUtils.getInstance().updataOrInsert(group.getGroupSort());
                DataToHostManage.updataCurToHost();
                finish();
            }
        });
    }

    private void initRennameScene(){
        renameTips.setText(getString(R.string.rename_scene_tips));
        editText.setHint(scene.getSceneSort().getName());
        editText.setSelection(editText.getText().length());

        titleBar.setOnClickRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(editText.getText().toString())){
                    showTipsDialog(getString(R.string.scene_name_empty_tips), getString(R.string.enter));
                    return;
                }

                if(Scenes.checkNameHad(editText.getText().toString(), scene.getSceneSort().getName())){
                    showTipsDialog(getString(R.string.scene_name_had), getString(R.string.enter));
                    return;
                }

                scene.getSceneSort().setName(editText.getText().toString());
                Scenes.getInstance().add(scene);
                ScenesDbUtils.getInstance().updataOrInsert(scene.getSceneSort());
                DataToHostManage.updataCurToHost();
                finish();
            }
        });
    }
}
