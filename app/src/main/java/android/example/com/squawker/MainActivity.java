/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package android.example.com.squawker;

import android.content.Intent;
import android.database.Cursor;
import android.example.com.squawker.databinding.ActivityMainBinding;
import android.example.com.squawker.following.FollowingPreferenceActivity;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int LOADER_ID_MESSAGES = 0;

    LinearLayoutManager mLayoutManager;
    SquawkAdapter mAdapter;

    static final String[] MESSAGES_PROJECTION = {
            SquawkContract.COLUMN_AUTHOR,
            SquawkContract.COLUMN_MESSAGE,
            SquawkContract.COLUMN_DATE,
            SquawkContract.COLUMN_AUTHOR_KEY
    };

    static final int COL_NUM_AUTHOR = 0;
    static final int COL_NUM_MESSAGE = 1;
    static final int COL_NUM_DATE = 2;
    static final int COL_NUM_AUTHOR_KEY = 3;

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        binding.squawksRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        binding.squawksRecyclerView.setLayoutManager(mLayoutManager);

        // Add dividers
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                binding.squawksRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        binding.squawksRecyclerView.addItemDecoration(dividerItemDecoration);

        // Specify an adapter
        mAdapter = new SquawkAdapter();
        binding.squawksRecyclerView.setAdapter(mAdapter);

        // Start the loader
        LoaderManager.getInstance(this).initLoader(LOADER_ID_MESSAGES, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_following_preferences) {
            // Opens the following activity when the menu icon is pressed
            Intent startFollowingActivity = new Intent(this, FollowingPreferenceActivity.class);
            startActivity(startFollowingActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Loader callbacks
     */

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This method generates a selection off of only the current followers
        String selection = SquawkContract.createSelectionForCurrentFollowers(
                PreferenceManager.getDefaultSharedPreferences(this));
        Log.d(LOG_TAG, "Selection is " + selection);
        return new CursorLoader(this, SquawkContract.CONTENT_URI,
                MESSAGES_PROJECTION, selection, null, SquawkContract.COLUMN_DATE + " DESC");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
