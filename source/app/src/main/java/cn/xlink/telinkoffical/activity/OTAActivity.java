package cn.xlink.telinkoffical.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.telink.bluetooth.event.NotificationEvent;
import com.telink.util.Event;
import com.telink.util.EventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xlink.telinkoffical.R;
import cn.xlink.telinkoffical.adapter.recycler_adapter.CommonAdapter;
import cn.xlink.telinkoffical.adapter.recycler_adapter.RecyclerViewHolder;
import cn.xlink.telinkoffical.model.Light;
import cn.xlink.telinkoffical.model.Lights;
import cn.xlink.telinkoffical.utils.EventBusUtils;
import cn.xlink.telinkoffical.utils.TelinkCommon;
import cn.xlink.telinkoffical.view.TitleBar;

/**
 * Created by liucr on 2016/4/6.
 */
public class OTAActivity extends BaseActivity implements EventListener{

    @Bind(R.id.act_ota_title)
    TitleBar titleBar;

    @Bind(R.id.act_ota_item1)
    View hadOtaView;

    @Bind(R.id.act_ota_item2)
    View noHadOtaView;

    @Bind(R.id.act_ota_had)
    View canOtaView;

    @Bind(R.id.act_ota_no_had)
    View notOtaView;

    @Bind(R.id.act_ota_had_recyclerview)
    RecyclerView canRecyclerView;

    @Bind(R.id.act_ota_no_had_recyclerview)
    RecyclerView notRecyclerView;

    @Bind(R.id.item_ota_button)
    TextView button;

    private List<Light> canLightList = new ArrayList<>();

    private List<Light> notLightList = new ArrayList<>();

    private CommonAdapter<Light> canCommonAdapter;

    private CommonAdapter<Light> noCommonAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ota);
        EventBusUtils.getInstance().addEventListener(NotificationEvent.ONLINE_STATUS,this);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
        updataUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBusUtils.getInstance().removeEventListener(this);
    }

    @Override
    protected void initData() {
        canLightList.clear();
        notLightList.clear();
        for(Light light : Lights.getInstance().get()){
            if(light.isCanUpdata() == 1){
                canLightList.add(light);
            }else if(light.isCanUpdata() == -1){
                notLightList.add(light);
            }
        }
    }

    @Override
    protected void initView() {
        titleBar.getCenterText().setText(getString(R.string.ota_title));
        titleBar.setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(canLightList.size() == 0 && notLightList.size() == 0){
            hadOtaView.setVisibility(View.GONE);
            noHadOtaView.setVisibility(View.VISIBLE);
            return;
        }

        if(canLightList.size()!=0){
            initCanView();
            canOtaView.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
        }

        if(notLightList.size()!=0){
            initNotView();
            notOtaView.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 初始化可升级的灯View
     */
    private void initCanView(){

        canRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        canRecyclerView.setItemAnimator(null);
        canCommonAdapter = new CommonAdapter<Light>(R.layout.item_ota, canLightList) {
            @Override
            public void convert(RecyclerViewHolder holder, final Light light, int position) {
                holder.setText(R.id.item_ota_name, light.getLightSort().getName());
                holder.getView(R.id.item_ota_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OTAActivity.this, OTATipsActivity.class);
                        intent.putExtra(TelinkCommon.ACTIVITY_TYPE_LIGHT_MESH, light.getLightSort().getMeshAddress());
                        startActivity(intent);
                    }
                });
            }
        };
        canRecyclerView.setAdapter(canCommonAdapter);
    }

    /**
     * 初始化不可升级的灯View
     */
    private void initNotView(){
        notRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notRecyclerView.setItemAnimator(null);
        noCommonAdapter = new CommonAdapter<Light>(R.layout.item_ota, notLightList) {
            @Override
            public void convert(RecyclerViewHolder holder, Light light, int position) {
                holder.getView(R.id.item_ota_button).setVisibility(View.INVISIBLE);
                holder.setImageResource(R.id.item_ota_image, light.getSelectIcon());

                holder.setText(R.id.item_ota_name, light.getLightSort().getName());

            }
        };
        notRecyclerView.setAdapter(noCommonAdapter);
    }

    private void updataUI(){

        if(canLightList.size() == 0 && notLightList.size() == 0){
            hadOtaView.setVisibility(View.GONE);
            noHadOtaView.setVisibility(View.VISIBLE);
            return;
        }

        if(canLightList.size()!=0){
            initCanView();
            canOtaView.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
        }

        if(notLightList.size()!=0){
            initNotView();
            notOtaView.setVisibility(View.VISIBLE);
        }

        if(noCommonAdapter  != null){
            noCommonAdapter.notifyDataSetChanged();
        }

        if(canCommonAdapter!=null){
            canCommonAdapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.item_ota_button)
    void ClickButton(){
        openActivity(OTATipsActivity.class);
    }

    @Override
    public void performed(Event event) {
        if(event.getType().equals(NotificationEvent.ONLINE_STATUS)){
            initData();
            updataUI();
        }
    }
}
