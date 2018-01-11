

package toplab18.runcoffee;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private ArrayList<MenuCard> dataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvProduct, tvCurrency, tvSize, tvPricce, tvMeasure;
        Button buttonOrder;

        public MyViewHolder(View v) {
            super(v);

            tvProduct =  v.findViewById(R.id.tvProduct);
            tvProduct.setTypeface(Typeface.createFromAsset(itemView.getContext().getAssets(), "phenomena-bold.otf"));
            tvMeasure =  v.findViewById(R.id.tvMeasure);
            tvMeasure.setTypeface(Typeface.createFromAsset(itemView.getContext().getAssets(), "phenomena-regular.otf"));
            tvCurrency = v.findViewById(R.id.tvCurrency);
            tvPricce = v.findViewById(R.id.tvPrice);
            tvPricce.setTypeface(Typeface.createFromAsset(itemView.getContext().getAssets(), "phenomena-bold.otf"));
            tvSize = v.findViewById(R.id.tvSize);

            }
    }

    public CustomAdapter(ArrayList<MenuCard> data) {
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_card_layout, parent, false);

      //  view.setOnClickListener(MainActivity.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        holder.tvProduct.setText(dataSet.get(listPosition).getName());
        holder.tvSize.setText(dataSet.get(listPosition).getSize());
        holder.tvMeasure.setText(dataSet.get(listPosition).getMeasure());
        holder.tvPricce.setText(dataSet.get(listPosition).getPrice());
        holder.tvCurrency.setText(dataSet.get(listPosition).getCurrency());
      //  holder.buttonOrder.setTag(dataSet.get(listPosition).getId());

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}

