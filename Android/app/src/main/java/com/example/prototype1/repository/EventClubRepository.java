package com.example.prototype1.repository;


import com.example.prototype1.model.Filters;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class EventClubRepository {

    public EventClubRepository() {
    }

    public void searchDocuments(Filters filters, String collection, int limit, DocumentSnapshot lastVisible, final MyDocumentsCallback myDocumentsCallback) {
        ArrayList<DocumentSnapshot> mResults = new ArrayList<>();

        Query query = FirebaseFirestore.getInstance().collection(collection);

        if (filters.hasCategory()) {
            query = query.whereEqualTo("category", filters.getCategory());
        }
        if (filters.hasPlace()) {
            query = query.whereEqualTo("place", filters.getPlace());
        }

        if (filters.hasSortBy()) {
            if (filters.hasDisplayPast()) {
                if (!filters.getDisplayPast()) {
                    query = query.whereEqualTo("isPastEvent", false);
                }
            }
            query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
        }

        if (filters.hasClubCategory()) {
            if (filters.getClubCategory() != 0) {
                query = query.whereEqualTo("cat", filters.getClubCategory());
            }
        }

        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }

        if (limit != 0) {
            query = query.limit(15);
        }

        query.get().addOnCompleteListener(task -> { //Performs query
            if (task.isSuccessful()) {
                //Adds to list of NEvent objects that matches query
                mResults.addAll(Objects.requireNonNull(task.getResult().getDocuments()));
                myDocumentsCallback.onCallback(mResults); //Callback required as Firebase query performed asynchronously; code after onCompleteListener will execute before it finishes
            }
        });
    }

    public void searchDocumentsSnapshot(Filters filters, String collection, int limit, DocumentSnapshot lastVisible, final MyDocumentsCallback myDocumentsCallback, final MyListenerCallback myListenerCallback) {
        ArrayList<DocumentSnapshot> mResults = new ArrayList<>();

        Query query = FirebaseFirestore.getInstance().collection(collection);

        if (filters.hasCategory()) {
            query = query.whereEqualTo("category", filters.getCategory());
        }
        if (filters.hasPlace()) {
            query = query.whereEqualTo("place", filters.getPlace());
        }

        if (filters.hasSortBy()) {
            if (filters.hasDisplayPast()) {
                if (!filters.getDisplayPast()) {
                    query = query.whereEqualTo("isPastEvent", false);
                }
            }
            query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
        }

        if (filters.hasClubCategory()) {
            if (filters.getClubCategory() != 0) {
                query = query.whereEqualTo("cat", filters.getClubCategory());
            }
        }

        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }

        if (limit != 0) {
            query = query.limit(15);
        }

        ListenerRegistration queryListener = query.addSnapshotListener((snapshot, e) -> { //Performs query
            if (snapshot != null) {
                //Adds to list of NEvent objects that matches query
                mResults.clear();
                mResults.addAll(Objects.requireNonNull(snapshot.getDocuments()));
                myDocumentsCallback.onCallback(mResults); //Callback required as Firebase query performed asynchronously; code after onCompleteListener will execute before it finishes
            }
        });

        myListenerCallback.onCallback(queryListener);
    }

    public void multipleDocumentSearches(List<String> searchList, String searchType, String collection, final MyDocumentsCallback myDocumentsCallback) {
        ArrayList<DocumentSnapshot> mResults = new ArrayList<>();
        List<Task<?>> tasks = new ArrayList<>();

        for (String searchTerm : searchList) {

            if (searchType.equals("id")) {
                if (!searchTerm.equals("")) {
                    tasks.add(FirebaseFirestore.getInstance().collection(collection).document(searchTerm).get());
                }
            } else {
                tasks.add(FirebaseFirestore.getInstance().collection(collection).whereEqualTo(searchType, searchTerm).get());
            }
        }

        Tasks.whenAllSuccess(tasks).addOnSuccessListener(queryList -> {

            if (!searchType.equals("id")) {
                for (Object query : queryList) {
                    QuerySnapshot querySnapshot = ((QuerySnapshot) query);
                    mResults.addAll(querySnapshot.getDocuments());
                }
            } else {
                for (Object query : queryList) {
                    DocumentSnapshot documentSnapshot = ((DocumentSnapshot) query);
                    mResults.add(documentSnapshot);
                }
            }
            myDocumentsCallback.onCallback(mResults);
        });
    }

    public void getDoc(String fieldName, String fieldType, String collection, final MyDocumentCallback myDocumentCallback, final MyListenerCallback myListenerCallback) {

        if (fieldType.equals("id")) {
            ListenerRegistration docListener = FirebaseFirestore.getInstance().collection(collection).document(fieldName).addSnapshotListener((snapshot, e) -> {
                if (snapshot != null && snapshot.exists()) {
                    myDocumentCallback.onCallback(snapshot);
                }
            });

            myListenerCallback.onCallback(docListener);
        } else {
            FirebaseFirestore.getInstance().collection(collection).whereEqualTo(fieldType, fieldName).addSnapshotListener((snapshot, e) -> {
                if (snapshot.getDocuments().size() != 0) {
                    myDocumentCallback.onCallback(snapshot.getDocuments().get(0));
                }
            });
        }
    }


    public void updateDoc(String docID, Object updatedDoc, String collection) {
        FirebaseFirestore.getInstance().collection(collection).document(docID).set(updatedDoc);
    }

    public void deleteDoc(String docID, String collection) {
        FirebaseFirestore.getInstance().collection(collection).document(docID).delete();
    }

    public void addDoc(Object newDoc, String collection) {
        FirebaseFirestore.getInstance().collection(collection).add(newDoc);
    }


    //Callbacks
    public interface MyDocumentCallback {
        void onCallback(DocumentSnapshot mDocument);
    }

    public interface MyDocumentsCallback {
        void onCallback(ArrayList<DocumentSnapshot> resultList);
    }

    public interface MyListenerCallback {
        void onCallback(ListenerRegistration docListener);
    }

}


