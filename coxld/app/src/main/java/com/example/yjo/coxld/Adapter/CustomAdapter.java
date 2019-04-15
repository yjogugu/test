package com.example.yjo.coxld.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.yjo.coxld.R;
import com.example.yjo.coxld.model.ChatModelAI;
import com.github.library.bubbleview.BubbleTextView;

import java.util.List;

public class CustomAdapter extends BaseAdapter {
    private List<ChatModelAI> list_chat_models;
    private Context context;
    private LayoutInflater layoutInflater;


    public CustomAdapter(List<ChatModelAI> list_chat_models, Context context) {
        this.list_chat_models = list_chat_models;
        this.context = context;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {

        return list_chat_models.size();
    }

    @Override
    public Object getItem(int position) {

        return list_chat_models.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null)
        {
            if(list_chat_models.get(position).isSend)
                view = layoutInflater.inflate(R.layout.item_message_send,null);
            else
                view = layoutInflater.inflate(R.layout.list_item_message_recv,null);

            BubbleTextView text_message = (BubbleTextView)view.findViewById(R.id.text_Message);
            text_message.setText(list_chat_models.get(position).message);

        }
        return view;
    }



}
