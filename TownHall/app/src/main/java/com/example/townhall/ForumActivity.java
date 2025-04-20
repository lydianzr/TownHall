package com.example.townhall;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

public class ForumActivity extends AppCompatActivity implements PostDetailsActivity.OnBookmarkChangedListener {
    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private FirebaseFirestore db;
    private ListenerRegistration postsRegistration;
    private String currentUserId;
    private Button btnForYou, btnBookmarks;
    private List<ListenerRegistration> bookmarkListeners = new ArrayList<>();
    private int activeColor = Color.parseColor("#009688");
    private int inactiveColor = Color.parseColor("#222222");
    private String currentPage = "forYou";
    private SearchView searchView;
    private List<Post> allPosts = new ArrayList<>();
    private List<Post> allBookmarkedPosts = new ArrayList<>();
    private GradientDrawable background1, background2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);



        postRecyclerView = findViewById(R.id.recyclerViewPosts);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, this);
        postRecyclerView.setAdapter(postAdapter);

        db = FirebaseFirestore.getInstance();
        //currentUserId = UserManager.getCurrentUser().getUserId();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = currentUser.getUid();

        btnForYou = findViewById(R.id.btnForYou);
        btnBookmarks = findViewById(R.id.btnBookmarks);
        searchView = findViewById(R.id.searchView);

        background1 = (GradientDrawable) btnForYou.getBackground() ;
        background2 = (GradientDrawable) btnBookmarks.getBackground();

        // Initial load: show all posts
        listenForRealTimeUpdates();
        updateButtonColors();

        btnForYou.setOnClickListener(v -> {
            currentPage = "forYou";
            updateButtonColors();
            listenForRealTimeUpdates();
        });

        btnBookmarks.setOnClickListener(v -> {
            currentPage = "bookmarks";
            updateButtonColors();
            listenForBookmarkedPosts();
        });

        findViewById(R.id.BtnCreatePost).setOnClickListener(v -> {
            Intent intent = new Intent(ForumActivity.this, CreatePostActivity.class);
            startActivity(intent);
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });
    }

    private void updateButtonColors() {
        if (currentPage.equals("forYou")) {
            background1.setColor(activeColor);
            background2.setColor(inactiveColor);
        } else {
            background1.setColor(inactiveColor);
            background2.setColor(activeColor);
        }
    }

    private void performSearch(String query) {
        List<Post> currentPosts;
        if (currentPage.equals("forYou")) {
            currentPosts = allPosts;
        } else {
            currentPosts = allBookmarkedPosts;
        }
        SearchUtils.searchPosts(query, currentPosts, new SearchUtils.SearchCallback() {
            @Override
            public void onSearchResults(List<Post> searchResults) {
                postList.clear();
                postList.addAll(searchResults);
                postAdapter.notifyDataSetChanged();
            }
        });
    }
    private void listenForRealTimeUpdates() {
        if (postsRegistration != null) {
            postsRegistration.remove();
        }
        postsRegistration = db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        // Log the error if the listener fails
                        e.printStackTrace();
                        return;
                    }
                    if (snapshots != null) {
                        allPosts.clear();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            Date timestamp = doc.getDate("timestamp");
                            Post post = doc.toObject(Post.class);
                            post.setPostId(doc.getId());
                            post.setTimestamp(timestamp);
                            allPosts.add(post);
                        }
                        if (currentPage.equals("forYou")) {
                            postList.clear();
                            postList.addAll(allPosts);
                            postAdapter.notifyDataSetChanged();
                        }
                        else {
                            listenForBookmarkedPosts();
                        }
                        performSearch(searchView.getQuery().toString());
                    }
                });
    }

    private void listenForBookmarkedPosts() {
        if (!bookmarkListeners.isEmpty()) {
            for (ListenerRegistration listener : bookmarkListeners) {
                listener.remove();
            }
            bookmarkListeners.clear();
        }
        BookmarkUtils.getBookmarkedPostIds(currentUserId, new BookmarkUtils.BookmarksCallback() {
            @Override
            public void onBookmarksReceived(List<String> bookmarkedIds) {
                Log.d("ForumActivity", "Bookmarked IDs received: " + bookmarkedIds);
                if (bookmarkedIds.isEmpty()) {
                    postList.clear();
                    allBookmarkedPosts.clear();
                    postAdapter.notifyDataSetChanged();
                    return;
                }
                postsRegistration = db.collection("posts")
                        .addSnapshotListener((snapshots, e) -> {
                            if (e != null) {
                                Log.e("ForumActivity", "Error listening to bookmarked posts", e);
                                return;
                            }
                            if (snapshots != null) {
                                Log.d("ForumActivity", "Snapshot received, size: " + snapshots.size());
                                postList.clear();
                                allBookmarkedPosts.clear();
                                for (QueryDocumentSnapshot doc : snapshots) {
                                    Log.d("ForumActivity", "Document ID: " + doc.getId());
                                    Date timestamp = doc.getDate("timestamp");
                                    Post post = doc.toObject(Post.class);
                                    post.setPostId(doc.getId());
                                    post.setTimestamp(timestamp);
                                    if (bookmarkedIds.contains(post.getPostId())) {
                                        Log.d("ForumActivity", "Bookmarked Post added: " + post.getPostId());
                                        postList.add(post);
                                        allBookmarkedPosts.add(post);
                                    }
                                }
                                postAdapter.notifyDataSetChanged();
                                Log.d("ForumActivity", "Adapter notified of changes");
                                performSearch(searchView.getQuery().toString());
                            }
                        });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove Firestore listener to avoid memory leaks
        if (postsRegistration != null) {
            postsRegistration.remove();
        }
        if (!bookmarkListeners.isEmpty()) {
            for (ListenerRegistration listener : bookmarkListeners) {
                listener.remove();
            }
        }
    }

    @Override
    public void onBookmarkChanged() {
        Log.d("ForumActivity", "onBookmarkChanged: Bookmark status changed, refreshing data");
        if (currentPage.equals("forYou")) {
            listenForRealTimeUpdates();
        } else {
            listenForBookmarkedPosts();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String postId = data.getStringExtra("postId");
            boolean isBookmarked = data.getBooleanExtra("isBookmarked", false);

            if (postId != null) {
                for (int i = 0; i < postList.size(); i++) {
                    if (postList.get(i).getPostId().equals(postId)) {
                        // Update the bookmark status of the post
                        postList.get(i).setBookmarked(isBookmarked);
                        postAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return this;
    }
}