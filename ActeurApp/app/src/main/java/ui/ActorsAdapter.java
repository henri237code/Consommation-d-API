package ui;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.acteurapp.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.Actor;

public class ActorsAdapter extends RecyclerView.Adapter<ActorsAdapter.VH> {

    public interface OnEdit { void edit(Actor a); }
    public interface OnDelete { void delete(Actor a); }

    private final List<Actor> data = new ArrayList<>();
    private final boolean showActions;
    private final OnEdit onEdit;
    private final OnDelete onDelete;

    public ActorsAdapter(boolean showActions, OnEdit onEdit, OnDelete onDelete) {
        this.showActions = showActions;
        this.onEdit = onEdit;
        this.onDelete = onDelete;
        setHasStableIds(true);
    }

    /** Remplace la liste entière */
    public void submit(List<Actor> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    /** Ajoute un élément en bas (utile après création) */
    public void append(Actor a) {
        if (a == null) return;
        data.add(a);
        notifyItemInserted(data.size() - 1);
    }

    /** Met à jour un acteur existant (match par id) */
    public void update(Actor a) {
        if (a == null) return;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId() == a.getId()) {
                data.set(i, a);
                notifyItemChanged(i);
                return;
            }
        }
    }

    /** Supprime par id */
    public void removeById(int id) {
        for (Iterator<Actor> it = data.iterator(); it.hasNext();) {
            Actor a = it.next();
            if (a.getId() == id) {
                int pos = data.indexOf(a);
                it.remove();
                notifyItemRemoved(pos);
                return;
            }
        }
    }

    @Override public long getItemId(int position) {
        try { return data.get(position).getId(); }
        catch (Exception e) { return RecyclerView.NO_ID; }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_actor, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Actor a = data.get(position);

        h.tvName.setText(a.getName());
        h.tvBio.setText(a.getBio());

        String url = a.getPicture();
        if (!TextUtils.isEmpty(url)) {
            Glide.with(h.img.getContext()).load(url).into(h.img);
        } else {
            h.img.setImageDrawable(null);
        }

        h.actions.setVisibility(showActions ? View.VISIBLE : View.GONE);
        h.btnEdit.setOnClickListener(v -> { if (onEdit != null) onEdit.edit(a); });
        h.btnDelete.setOnClickListener(v -> { if (onDelete != null) onDelete.delete(a); });
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName, tvBio;
        LinearLayout actions;
        Button btnEdit, btnDelete;

        VH(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.img);
            tvName = v.findViewById(R.id.tvName);
            tvBio = v.findViewById(R.id.tvBio);
            actions = v.findViewById(R.id.actions);
            btnEdit = v.findViewById(R.id.btnEdit);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}
