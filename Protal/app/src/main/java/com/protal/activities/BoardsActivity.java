package com.protal.activities;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.protal.R;
import com.protal.adapters.ItemAdapter;
import com.woxthebox.draglistview.BoardView;
import com.woxthebox.draglistview.ColumnProperties;
import com.woxthebox.draglistview.DragItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BoardsActivity extends AppCompatActivity {

    private BoardView mBoardView;
    Map<String, List<Map<String, String>>> lists = new HashMap<>();
    private static int sCreatedItems = 0;

    //TODO: add new card by clicking header

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boards);
        Toolbar toolbar = findViewById(R.id.toolbarBoard);
        toolbar.setTitle(getIntent().getStringExtra("name"));
        setSupportActionBar(toolbar);

        mBoardView = findViewById(R.id.board_view);
        mBoardView.setSnapToColumnsWhenScrolling(true);
        mBoardView.setSnapToColumnWhenDragging(true);
        mBoardView.setSnapDragItemToTouch(true);
        mBoardView.setSnapToColumnInLandscape(false);
        mBoardView.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER);
        mBoardView.clearBoard();
        mBoardView.setCustomDragItem(new MyDragItem(this, R.layout.column_item));
//        mBoardView.setCustomColumnDragItem(new MyColumnDragItem(this, R.layout.column_drag_layout));

        mBoardView.setBoardListener(new BoardView.BoardListener() {
            @Override
            public void onItemDragStarted(int column, int row) {

            }

            @Override
            public void onItemDragEnded(int fromColumn, int fromRow, int toColumn, int toRow) {

            }

            @Override
            public void onItemChangedPosition(int oldColumn, int oldRow, int newColumn, int newRow) {

            }

            @Override
            public void onItemChangedColumn(int oldColumn, int newColumn) {
                //TODO: change column in server
            }

            @Override
            public void onFocusedColumnChanged(int oldColumn, int newColumn) {

            }

            @Override
            public void onColumnDragStarted(int position) {

            }

            @Override
            public void onColumnDragChangedPosition(int oldPosition, int newPosition) {

            }

            @Override
            public void onColumnDragEnded(int position) {

            }
        });

        getLists(getIntent().getStringExtra("id"));
    }

    private void getLists(final String id) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Boards").document(id)
                .collection("Lists").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable final QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                // fetch all boards id
                if (queryDocumentSnapshots != null) {
                    for (final QueryDocumentSnapshot document : queryDocumentSnapshots)
                        lists.put(document.getId(), new ArrayList<Map<String, String>>());
                }

                db.collectionGroup("Cards").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        System.out.println(task.getResult());
                        for (QueryDocumentSnapshot d: task.getResult()) {
                            if(d.getReference().getPath().contains(id)) {
                                String ListName = d.getReference().getPath().split("/")[3];
                                Map<String, String> m = new HashMap<>();
                                m.put("id", d.getId());
                                m.put("title", d.getString("title"));
                                List<Map<String, String>> l = lists.get(ListName);
                                l.add(m);
                                lists.put(ListName, l);
                            }
                        }
                        lists = createLists(lists);
                    }
                });
                //TODO: DELETE
//                final Map<String, Object> temp = new HashMap<>();
//                for (final String ListName : lists.keySet()) {
//                    db.collection("Boards").document(id).collection("Lists")
//                            .document(ListName).collection("Cards").get()
//                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                @Override
//                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                    if(task.isSuccessful()){
//                                        List<Map<String, String>> temp1 = new ArrayList<>();
//
//                                        for (QueryDocumentSnapshot snapshot1: task.getResult()){
//                                            Map<String, String> m = new HashMap<>();
//                                            m.put("id", snapshot1.getId());
//                                            m.put("title", snapshot1.getString("title"));
//                                            temp1.add(m);
//                                        }
//                                        temp.put(ListName, temp1);
//                                        lists.clear();
//                                        lists.putAll(temp);
//                                        Log.d("Debugggingggg", lists.toString());
//                                    }
//                                }
//                            });
//                }
            }
        });
    }

    private Map<String, List<Map<String, String>>> createLists(Map<String, List<Map<String, String>>> lists) {
        mBoardView.clearBoard();
        sCreatedItems = 0;
        Map<String, List<Map<String, String>>> sortedMap = new TreeMap<>(Collections.reverseOrder());
        sortedMap.putAll(lists);
        for (Map.Entry<String, List<Map<String, String>>> m : sortedMap.entrySet()) {
            addColumn(m.getKey(), m.getValue());
        }
        addColumn("Add New", null);
        return sortedMap;
    }

    private void addColumn(String name, List<Map<String, String>> cards) {
        final ArrayList<Pair<Long, String>> mItemArray = new ArrayList<>();
        if(!name.equals("Add New")) {
            for (Map<String, String> card : cards) {
                long id = sCreatedItems++;
                mItemArray.add(new Pair<>(id, card.get("title")));
            }
        }

        final ItemAdapter listAdapter = new ItemAdapter(mItemArray, R.layout.column_item, R.id.item_layout, true);
        final View header = View.inflate(this, R.layout.column_header, null);
        ((TextView) header.findViewById(R.id.tvTitle)).setText(name);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //TODO: will open a dialog for name
                LayoutInflater li = LayoutInflater.from(BoardsActivity.this);
                View dialogView = li.inflate(R.layout.dialog_get_card_name, null);

                final EditText etAddCardDialogName = dialogView.findViewById(R.id.etAddCardDialogName);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BoardsActivity.this);
                alertDialogBuilder.setView(dialogView);
                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Get user input and set it to result
                                if(!etAddCardDialogName.getText().toString().trim().isEmpty()) {
                                    insertCard(etAddCardDialogName.getText().toString(),
                                            lists.keySet().toArray()[mBoardView.getColumnOfHeader(v)].toString());
                                    long id2 = mItemArray.size();
                                    Pair item = new Pair<>(id2, etAddCardDialogName.getText().toString());
                                    mBoardView.addItem(mBoardView.getColumnOfHeader(v), 0, item, true);
                                }
                            }
                        }).create()
                        .show();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        ColumnProperties columnProperties = ColumnProperties.Builder.newBuilder(listAdapter)
                .setLayoutManager(layoutManager)
                .setHasFixedItemSize(false)
                .setColumnBackgroundColor(Color.TRANSPARENT)
                .setItemsSectionBackgroundColor(Color.TRANSPARENT)
                .setHeader(header)
                .build();

        mBoardView.addColumn(columnProperties);
    }

    private void insertCard(final String cardName, String ListName) {
        FirebaseFirestore.getInstance().collection("Boards").document(
                getIntent().getStringExtra("id")).collection("Lists")
                .document(ListName).collection("Cards")
                .add(new HashMap<String, String>(){{put("title", cardName);}})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BoardsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void add_to_database(String name) {

    }


    private static class MyColumnDragItem extends DragItem {

        MyColumnDragItem(Context context, int layoutId) {
            super(context, layoutId);
            setSnapToTouch(false);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            LinearLayout clickedLayout = (LinearLayout) clickedView;
            View clickedHeader = clickedLayout.getChildAt(0);
            RecyclerView clickedRecyclerView = (RecyclerView) clickedLayout.getChildAt(1);

            View dragHeader = dragView.findViewById(R.id.drag_header);
            ScrollView dragScrollView = dragView.findViewById(R.id.drag_scroll_view);
            LinearLayout dragLayout = dragView.findViewById(R.id.drag_list);

            Drawable clickedColumnBackground = clickedLayout.getBackground();
            if (clickedColumnBackground != null) {
                ViewCompat.setBackground(dragView, clickedColumnBackground);
            }

            Drawable clickedRecyclerBackground = clickedRecyclerView.getBackground();
            if (clickedRecyclerBackground != null) {
                ViewCompat.setBackground(dragLayout, clickedRecyclerBackground);
            }

            dragLayout.removeAllViews();

            ((TextView) dragHeader.findViewById(R.id.text)).setText(((TextView) clickedHeader
                    .findViewById(R.id.text)).getText());
            for (int i = 0; i < clickedRecyclerView.getChildCount(); i++) {
                View view = View.inflate(dragView.getContext(), R.layout.column_item, null);
                ((TextView) view.findViewById(R.id.text)).setText(((TextView) clickedRecyclerView
                        .getChildAt(i).findViewById(R.id.text)).getText());
                dragLayout.addView(view);

                if (i == 0) {
                    dragScrollView.setScrollY(-clickedRecyclerView.getChildAt(i).getTop());
                }
            }

            dragView.setPivotY(0);
            dragView.setPivotX(clickedView.getMeasuredWidth() / 2);
        }

        @Override
        public void onStartDragAnimation(View dragView) {
            super.onStartDragAnimation(dragView);
            dragView.animate().scaleX(0.9f).scaleY(0.9f).start();
        }

        @Override
        public void onEndDragAnimation(View dragView) {
            super.onEndDragAnimation(dragView);
            dragView.animate().scaleX(1).scaleY(1).start();
        }
    }

    private static class MyDragItem extends DragItem {

        MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            CharSequence text = ((TextView) clickedView.findViewById(R.id.text)).getText();
            ((TextView) dragView.findViewById(R.id.text)).setText(text);
            CardView dragCard = dragView.findViewById(R.id.card);
            CardView clickedCard = clickedView.findViewById(R.id.card);

            dragCard.setMaxCardElevation(40);
            dragCard.setCardElevation(clickedCard.getCardElevation());
            dragCard.setForeground(clickedView.getResources().getDrawable(R.drawable.card_view_drag_foreground));
        }

        @Override
        public void onMeasureDragView(View clickedView, View dragView) {
            CardView dragCard = dragView.findViewById(R.id.card);
            CardView clickedCard = clickedView.findViewById(R.id.card);
            int widthDiff = dragCard.getPaddingLeft() - clickedCard.getPaddingLeft() + dragCard.getPaddingRight() -
                    clickedCard.getPaddingRight();
            int heightDiff = dragCard.getPaddingTop() - clickedCard.getPaddingTop() + dragCard.getPaddingBottom() -
                    clickedCard.getPaddingBottom();
            int width = clickedView.getMeasuredWidth() + widthDiff;
            int height = clickedView.getMeasuredHeight() + heightDiff;
            dragView.setLayoutParams(new FrameLayout.LayoutParams(width, height));

            int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            dragView.measure(widthSpec, heightSpec);
        }

        @Override
        public void onStartDragAnimation(View dragView) {
            CardView dragCard = dragView.findViewById(R.id.card);
            ObjectAnimator anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.getCardElevation(), 40);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(ANIMATION_DURATION);
            anim.start();
        }

        @Override
        public void onEndDragAnimation(View dragView) {
            CardView dragCard = dragView.findViewById(R.id.card);
            ObjectAnimator anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.getCardElevation(), 6);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(ANIMATION_DURATION);
            anim.start();
        }
    }

}
