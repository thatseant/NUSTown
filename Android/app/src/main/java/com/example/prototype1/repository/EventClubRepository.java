package com.example.prototype1.repository;


import com.example.prototype1.model.Filters;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
            if (!filters.getDisplayPast()) {
                query = query.whereEqualTo("isPastEvent", false);
            }
            query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
        }

        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }

        if (limit != 0) {
            query = query.limit(15);
        }

        //TODO: Add DisplayPast to query

        query.get().addOnCompleteListener(task -> { //Performs query
            if (task.isSuccessful()) {
                //Adds to list of NEvent objects that matches query
                mResults.addAll(Objects.requireNonNull(task.getResult().getDocuments()));
                myDocumentsCallback.onCallback(mResults); //Callback required as Firebase query performed asynchronously; code after onCompleteListener will execute before it finishes
            }
        });
    }

    public void multipleDocumentSearches(List<String> searchList, String searchType, String collection, final MyDocumentsCallback myDocumentsCallback) {
        ArrayList<DocumentSnapshot> mResults = new ArrayList<>();
        List<Task<?>> tasks = new ArrayList<>();

        for (String searchTerm : searchList) {

            if (searchType.equals("id")) {
                tasks.add(FirebaseFirestore.getInstance().collection(collection).document(searchTerm).get());
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

    public void getDoc(String fieldName, String fieldType, String collection, final MyDocumentCallback myDocumentCallback) {

        if (fieldType.equals("id")) {
            FirebaseFirestore.getInstance().collection(collection).document(fieldName).get().addOnSuccessListener(myDocumentCallback::onCallback);
        } else {
            FirebaseFirestore.getInstance().collection(collection).whereEqualTo(fieldType, fieldName).get().addOnSuccessListener(querySnapshot -> {
                myDocumentCallback.onCallback(querySnapshot.getDocuments().get(0));
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

}


