package ui;

import android.os.Bundle; import android.view.*; import android.widget.TextView;
import androidx.annotation.*; import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager; import androidx.recyclerview.widget.RecyclerView;
import com.example.acteurapp.R; import model.Actor; import net.*;
import java.util.*; import retrofit2.*;

public class AllAuthorsFragment extends Fragment {
    private TextView tvTotal; private ActorsAdapter adapter; private ApiService api;

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inf,@Nullable ViewGroup c,@Nullable Bundle b){
        View v=inf.inflate(R.layout.fragment_all_authors,c,false);
        tvTotal=v.findViewById(R.id.tvTotal);
        adapter=new ActorsAdapter(false,a->{},a->{});
        RecyclerView rv=v.findViewById(R.id.recycler); rv.setLayoutManager(new LinearLayoutManager(getContext())); rv.setAdapter(adapter);
        api=RetrofitClient.get("http://10.0.2.2:8000/").create(ApiService.class);
        loadAll(); return v;
    }

    private void loadAll(){
        api.getTotal().enqueue(new Callback<Map<String,Integer>>() {
            @Override public void onResponse(Call<Map<String,Integer>> call, Response<Map<String,Integer>> res){
                Integer t=(res.body()!=null)? res.body().get("total"): null;
                tvTotal.setText("Total: "+(t!=null?t:"?"));
            }
            @Override public void onFailure(Call<Map<String,Integer>> call, Throwable t){ }
        });
        api.getActors().enqueue(new Callback<List<Actor>>() {
            @Override public void onResponse(Call<List<Actor>> call, Response<List<Actor>> res){
                if(res.isSuccessful()&&res.body()!=null) adapter.submit(res.body());
            }
            @Override public void onFailure(Call<List<Actor>> call, Throwable t){ }
        });
    }
}