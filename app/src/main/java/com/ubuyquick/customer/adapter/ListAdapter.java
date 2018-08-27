package com.ubuyquick.customer.adapter;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ubuyquick.customer.ListItemsActivity;
import com.ubuyquick.customer.R;
import com.ubuyquick.customer.model.ListProduct;
import com.ubuyquick.customer.utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private static final String TAG = "ListAdapter";

    private Context context;
    private List<com.ubuyquick.customer.model.List> lists;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public ListAdapter(Context context) {
        this.context = context;
        this.lists = new ArrayList<>();
    }

    public void setLists(List<com.ubuyquick.customer.model.List> lists) {
        this.lists = lists;
        notifyDataSetChanged();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_list_name;
        private TextView tv_created_at;
        private ImageButton btn_delete;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tv_list_name = (TextView) itemView.findViewById(R.id.tv_list_name);
            this.tv_created_at = (TextView) itemView.findViewById(R.id.tv_created_at);
            this.btn_delete = (ImageButton) itemView.findViewById(R.id.btn_delete);
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    builder.setMessage("Load List: " + lists.get(getAdapterPosition()).getListName())
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    String list_id = lists.get(getAdapterPosition()).getListId();
//                                    Intent intent = new Intent("list id transaction");
//                                    intent.putExtra("list_id", list_id);
//                                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//                                }
//                            })
//                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//
//                                }
//                            }).show();
//                }
//            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ListItemsActivity.class);
                    i.putExtra("list_id", lists.get(getAdapterPosition()).getListId());
                    i.putExtra("list_name", lists.get(getAdapterPosition()).getListName());
                    context.startActivity(i);
                    ((Activity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
            });

            this.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String list_id = lists.get(getAdapterPosition()).getListId();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Delete list?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                            .collection("lists").document(list_id).delete();
                                    lists.remove(getAdapterPosition());
                                    notifyItemRemoved(getAdapterPosition());
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                }
            });

        }

        public void bind(com.ubuyquick.customer.model.List list) {
            tv_list_name.setText(list.getListName());
            tv_created_at.setText(list.getListCreatedAt());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(this.context).inflate(R.layout.card_list_select, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this.lists.get(position));
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }
}