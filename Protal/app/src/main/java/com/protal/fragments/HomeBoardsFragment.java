package com.protal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.protal.R;
import com.protal.activities.BoardsActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeBoardsFragment extends Fragment {

    private static ArrayAdapter<String> listAdapter;
    private List<String> BoardNamesList = new ArrayList<>();
    private List<String> BoardIdList = new ArrayList<>();
    private ProgressBar pbLoadingHomeBoards;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_boards, container, false);
        ListView lvTeamBoards = view.findViewById(R.id.lvHomeBoards);
        pbLoadingHomeBoards = view.findViewById(R.id.pbLoadingHomeBoards);

        listAdapter = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_list_item_1,
                BoardNamesList);

        lvTeamBoards.setAdapter(listAdapter);

        lvTeamBoards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(), BoardsActivity.class);
                intent.putExtra("id", BoardIdList.get(position));
                intent.putExtra("name", BoardNamesList.get(position));
                startActivity(intent);
            }
        });

        get_data();

        return view;
    }

    private void get_data(){
        pbLoadingHomeBoards.setVisibility(View.VISIBLE);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final CollectionReference BoardIDRef = db.collection("Users").document(
                FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Boards");

        BoardIDRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                // get name of users boards
                db.collectionGroup("Boards").whereIn("Type",
                        Arrays.asList("Personal", "Team"))
                        .whereArrayContains("Members", FirebaseAuth.getInstance()
                                .getCurrentUser().getUid()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    BoardNamesList.clear();
                                    for (QueryDocumentSnapshot snapshot:task.getResult()){
                                        BoardNamesList.add(snapshot.getString("Name"));
                                        BoardIdList.add(snapshot.getId());
                                    }
                                    listAdapter.notifyDataSetChanged();
                                    pbLoadingHomeBoards.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        });
    }

}
