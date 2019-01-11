package com.jeff.dominatelight.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.jeff.dominatelight.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liucr on 2015/12/23.
 */
public class PopWindowAdapter extends BaseAdapter {

    private List<String> stirngs = new ArrayList<>();

    private LayoutInflater inflater;

    private Context context;

    private boolean isShowIcon = false;

    private int curStyle = 0;

    public PopWindowAdapter(Activity activity) {
        context = activity;
        inflater = activity.getLayoutInflater();
    }

    public void setStyle(int style) {
        curStyle = style;
    }

    public boolean isShowIcon() {
        return isShowIcon;
    }

    public void setIsShowIcon(boolean isShowIcon) {
        this.isShowIcon = isShowIcon;
    }

    public void setData(List<String> stirngs) {
        this.stirngs = stirngs;
    }

    public void clear() {
        stirngs.clear();
    }

    @Override
    public int getCount() {
        return stirngs.size();
    }

    @Override
    public String getItem(int i) {
        return stirngs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.view_popwindow_item, null, false);
        View bg = view.findViewById(R.id.view_popwindow_item_bg);
        TextView textView = (TextView) view.findViewById(R.id.view_popwindow_item_text);
        ImageView lineView = (ImageView) view.findViewById(R.id.view_popwindow_item_line);

        if (stirngs.get(i).contains("删除") || (stirngs.get(i).equals(context.getResources().getString(R.string.share_cancel)))) {
            textView.setTextColor(context.getResources().getColor(R.color.red_e63838));
        } else {
            textView.setTextColor(context.getResources().getColor(R.color.text_blue));
        }

        if (curStyle == 1) {
            bg.setBackgroundColor(context.getResources().getColor(R.color.home_down_bg));
            lineView.setImageResource(R.color.home_down_line);
            int padding = (int) context.getResources().getDimension(R.dimen.popupwind_line_padding);
            lineView.setPadding(padding, 0, padding, 0);
            textView.setTextColor(Color.WHITE);
        }

        if (i == stirngs.size() - 1) {
            lineView.setVisibility(View.GONE);
        } else {
            lineView.setVisibility(View.VISIBLE);
        }

        textView.setText(stirngs.get(i));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(i);
                }
            }
        });
        return view;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
