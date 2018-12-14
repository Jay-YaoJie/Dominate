package com.jeff.dominate.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.jeff.dominate.R;
import com.jeff.dominate.TelinkLightService;
import com.jeff.dominate.activity.MainActivity;
import com.jeff.dominate.activity.ModelTestSettingActivity;
import com.jeff.dominate.adapter.BaseRecyclerViewAdapter;
import com.jeff.dominate.adapter.TestModelListAdapter;
import com.jeff.dominate.model.TestModel;
import com.jeff.dominate.util.FileSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Jeff  5899859876@qq.com
 * Csdn :https://blog.csdn.net/Jeff_YaoJie
 * Github: https://github.com/Jay-YaoJie
 * Created :  2018-11-17.
 * description ：主页 fragment
 */

public class MainFragment extends Fragment implements View.OnClickListener {

    TestModelListAdapter mAdapter;
    private List<TestModel> models;
    private TextView tv_model_setting;
    private static final String TEST_FILE_NAME = "TEST_MODELS";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, null);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv_models = (RecyclerView) view.findViewById(R.id.rv_test_models);

        tv_model_setting = (TextView) view.findViewById(R.id.tv_model_setting);
        tv_model_setting.setOnClickListener(this);


        if (FileSystem.exists(getActivity(), TEST_FILE_NAME)) {
            models = (List<TestModel>) FileSystem.readAsObject(getActivity(), TEST_FILE_NAME);
        } else {
            models = new ArrayList<>();

            String[] names = getResources().getStringArray(R.array.model_names);

            // cnt: 12
            byte[][] params = new byte[][]{
                    {0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},

                    {0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, // holder

                    {0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, // holder

                    {0x00, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04},

                    {0x00, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},

                    {0x00, 0x00, 0x00, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},

                    {0x00, 0x00, 0x00, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},

                    {0x00, 0x00, 0x00, 0x50, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04},

                    {0x00, 0x00, 0x00, 0x60, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},

                    {0x00, 0x00, 0x00, 0x70, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04},


                    {0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, // holder

                    {0x00, 0x00, 0x00, (byte) 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04},
            };
            TestModel model;
            for (int i = 0; i < 12; i++) {
                model = new TestModel();
                model.setId(i);
                model.setName(names[i]);
                model.setOpCode((byte) 0xCA);
                model.setVendorId(0x0211);
                model.setAddress(0xFFFF);
                if (i == 1 || i == 2 || i == 10){
                    model.setHolder(true);
                }else {
                    model.setHolder(false);
                }
//            model.setParams(new byte[]{0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11});
                model.setParams(params[i]);
                models.add(model);
            }

            FileSystem.writeAsObject(getActivity(), TEST_FILE_NAME, models);
        }


        mAdapter = new TestModelListAdapter(getActivity(), models);
        mAdapter.setSettingMode(false);
//        rv_models.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rv_models.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rv_models.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                sendModel(models.get(position));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        models = (List<TestModel>) FileSystem.readAsObject(getActivity(), TEST_FILE_NAME);
        mAdapter.notifyDataSetChanged();
    }

    private void sendModel(TestModel model) {
        if (model == null) return;

        boolean sendResult = TelinkLightService.Instance().sendVendorCommand(model.getOpCode(), model.getVendorId(), model.getAddress(), model.getParams());
        if (sendResult) {
            showErrorMsg("send success");
        } else {
            showErrorMsg("send fail");
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_model_setting:
                startActivity(new Intent(getActivity(), ModelTestSettingActivity.class));
                break;
        }
    }


    public void showErrorMsg(String msg) {
        ((MainActivity) getActivity()).showToast(msg);
    }

}
