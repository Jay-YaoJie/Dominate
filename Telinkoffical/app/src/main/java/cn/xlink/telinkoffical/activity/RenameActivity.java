package cn.xlink.telinkoffical.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.manage.DataToHostManage;
import cn.xlink.telinkoffical.model.Group;
import cn.xlink.telinkoffical.model.Groups;
import cn.xlink.telinkoffical.model.Light;
import cn.xlink.telinkoffical.model.Lights;
import cn.xlink.telinkoffical.model.Scene;
import cn.xlink.telinkoffical.model.Scenes;
import cn.xlink.telinkoffical.utils.GroupsDbUtils;
import cn.xlink.telinkoffical.utils.LightsDbUtils;
import cn.xlink.telinkoffical.utils.ScenesDbUtils;
import cn.xlink.telinkoffical.utils.TelinkCommon;
import cn.xlink.telinkoffical.view.TitleBar;

/**
 * Created by liucr on 2016/3/25.
 */
public class RenameActivity extends BaseActivity {

    @Bind(R.id.rename_titlebar)
    TitleBar titleBar;

    @Bind(R.id.rename_edit)
    EditText editText;

    @Bind(R.id.rename_tips)
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
        ButterKnife.unbind(this);
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
