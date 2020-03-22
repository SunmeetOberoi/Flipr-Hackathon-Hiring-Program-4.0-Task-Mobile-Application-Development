package com.protal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.protal.R;
import com.protal.activities.BoardsActivity;
import com.protal.activities.MainHomePageActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalBoardsFragment extends Fragment {

    private static ArrayAdapter<String> listAdapter;
    private List<String> BoardNamesList = new ArrayList<>();
    private List<String> BoardIdList = new ArrayList<>();
    private ProgressBar pbLoadingPersonalBoards;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_boards, container, false);

        ListView lvPersonalBoards = view.findViewById(R.id.lvPersonalBoards);
        pbLoadingPersonalBoards = view.findViewById(R.id.pbLoadingPersonalBoards);

        listAdapter = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_list_item_1,
                BoardNamesList);

        lvPersonalBoards.setAdapter(listAdapter);

        lvPersonalBoards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(), BoardsActivity.class);
                intent.putExtra("id", BoardIdList.get(position));
                startActivity(intent);
            }
        });

        get_data();

        return view;
    }

    private void get_data(){
        pbLoadingPersonalBoards.setVisibility(View.VISIBLE);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final CollectionReference BoardIDRef = db.collection("Users").document(
                FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Boards");
        final CollectionReference BoardsRef = db.collection("Boards");

        BoardIDRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                // fetch all boards id
                if (queryDocumentSnapshots != null) {
                    BoardIdList.clear();
                    for (QueryDocumentSnapshot documents : queryDocumentSnapshots){
                        if(!documents.getBoolean("Archived"))
                            BoardIdList.add(documents.getId());
                    }
                    // get name of users boards
                    db.runTransaction(new Transaction.Function<List<String>>() {
                        @Nullable
                        @Override
                        public List<String> apply(@NonNull Transaction transaction)
                                throws FirebaseFirestoreException {

                            //TODO: check for not archived
                            DocumentSnapshot snapshot;
                            List<String> temp = new ArrayList<>();
                            for(String id : BoardIdList) {
                                snapshot=transaction.get(BoardsRef.document(id));
                                if(snapshot.getString("Type").equals("Personal"))
                                    temp.add(snapshot.getString("Name"));
                            }
                            return temp;
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<List<String>>() {
                        @Override
                        public void onSuccess(List<String> stringList) {
                            BoardNamesList.clear();
                            BoardNamesList.addAll(stringList);
                            listAdapter.notifyDataSetChanged();
                            pbLoadingPersonalBoards.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }
}
