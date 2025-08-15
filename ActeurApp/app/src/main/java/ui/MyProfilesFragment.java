package ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.acteurapp.R;

import java.util.*;
import data.OwnedStore;
import model.Actor;
import net.ApiService;
import net.RetrofitClient;
import retrofit2.*;

public class MyProfilesFragment extends Fragment {

    private TextView tvInfo;
    private EditText etName, etBio, etPicture;
    private Button btnAdd;
    private androidx.recyclerview.widget.RecyclerView recycler;
    private ActorsAdapter adapter;

    private ApiService api;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_my_profiles, container, false);

        tvInfo   = v.findViewById(R.id.tvInfo);
        etName   = v.findViewById(R.id.etName);
        etBio    = v.findViewById(R.id.etBio);
        etPicture= v.findViewById(R.id.etPicture);
        btnAdd   = v.findViewById(R.id.btnAdd);
        recycler = v.findViewById(R.id.recycler);

        // Adapter avec actions visibles sur Accueil
        adapter = new ActorsAdapter(
                true,
                this::onEditClicked,
                this::onDeleteClicked
        );
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        api = RetrofitClient.get("http://10.0.2.2:8000/").create(ApiService.class);

        btnAdd.setOnClickListener(vw -> onAddClicked());

        // Charge mes profils à l’ouverture
        refreshMine();
        return v;
    }

    /** Recharge les acteurs et ne garde que ceux dont l’ID est dans OwnedStore */
    private void refreshMine() {
        final Set<Integer> mine = OwnedStore.get(requireContext());
        api.getActors().enqueue(new Callback<List<Actor>>() {
            @Override public void onResponse(Call<List<Actor>> call, Response<List<Actor>> resp) {
                List<Actor> all = resp.body() != null ? resp.body() : Collections.emptyList();
                List<Actor> onlyMine = new ArrayList<>();
                for (Actor a : all) if (mine.contains(a.getId())) onlyMine.add(a);
                adapter.submit(onlyMine);
                updateInfo(onlyMine.size());
            }
            @Override public void onFailure(Call<List<Actor>> call, Throwable t) {
                // laisse l’UI telle quelle
            }
        });
    }

    /** Met à jour "Profils: X/2" et active/désactive Ajouter */
    private void updateInfo(int count) {
        tvInfo.setText("Profils: " + count + "/2");
        btnAdd.setEnabled(count < 2);
    }

    private void onAddClicked() {
        String name = etName.getText().toString().trim();
        String bio  = etBio.getText().toString().trim();
        String pic  = etPicture.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(bio) || TextUtils.isEmpty(pic)) {
            Toast.makeText(getContext(), "Champs manquants", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1) Récupère les IDs déjà utilisés pour fabriquer un ID libre [10..100]
        api.getActors().enqueue(new Callback<List<Actor>>() {
            @Override public void onResponse(Call<List<Actor>> call, Response<List<Actor>> resp) {
                Set<Integer> used = new HashSet<>();
                if (resp.isSuccessful() && resp.body()!=null) {
                    for (Actor a : resp.body()) used.add(a.getId());
                }
                int newId = generateUnusedId(used);

                // 2) Construit et POST
                Actor a = new Actor();
                a.setId(newId);
                a.setName(name);
                a.setBio(bio);
                a.setPicture(pic);

                api.createActor(a).enqueue(new Callback<Actor>() {
                    @Override public void onResponse(Call<Actor> call, Response<Actor> r) {
                        if (r.isSuccessful()) {
                            Actor created = r.body() != null ? r.body() : a;

                            // Marque comme “à moi”, ajoute visuellement, MAJ compteur
                            OwnedStore.add(requireContext(), created.getId());
                            adapter.append(created);
                            updateInfo(adapter.getItemCount());

                            etName.setText(""); etBio.setText(""); etPicture.setText("");
                            Toast.makeText(getContext(), "Ajout OK", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Erreur ajout ("+r.code()+")", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onFailure(Call<Actor> call, Throwable t) {
                        Toast.makeText(getContext(), "Réseau indisponible", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override public void onFailure(Call<List<Actor>> call, Throwable t) {
                Toast.makeText(getContext(), "Réseau indisponible", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int generateUnusedId(Set<Integer> used) {
        List<Integer> pool = new ArrayList<>();
        for (int i=10;i<=100;i++) pool.add(i);
        Collections.shuffle(pool);
        for (int id : pool) if (!used.contains(id)) return id;
        int max = used.stream().mapToInt(x->x).max().orElse(0);
        return max + 1;
    }

    /** Edition (ici on montre juste un toast — à toi d’ouvrir un dialog d’édition si tu veux) */
    private void onEditClicked(Actor a) {
        // TODO: ouvrir un dialog pour modifier name/bio/picture puis:
        // api.updateActor(a.getId(), payload).enqueue(... => adapter.update(a); )
        Toast.makeText(getContext(), "Edit " + a.getName(), Toast.LENGTH_SHORT).show();
    }

    /** Suppression : API + mise à jour locale + compteur */
    private void onDeleteClicked(Actor a) {
        api.deleteActor(a.getId()).enqueue(new Callback<Actor>() {
            @Override public void onResponse(Call<Actor> call, Response<Actor> resp) {
                if (resp.isSuccessful()) {
                    OwnedStore.remove(requireContext(), a.getId());
                    adapter.removeById(a.getId());
                    updateInfo(adapter.getItemCount());
                    Toast.makeText(getContext(), "Supprimé", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Erreur suppr ("+resp.code()+")", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(getContext(), "Réseau indisponible", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
