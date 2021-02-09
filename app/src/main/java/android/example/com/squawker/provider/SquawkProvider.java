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
package android.example.com.squawker.provider;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class SquawkProvider extends ContentProvider {

    public static final String AUTHORITY = "android.example.com.squawker.provider.provider";

    private SquawkDatabase helper;
    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int SQUAWKS = 100;
    static {
        matcher.addURI(SquawkContract.CONTENT_AUTHORITY, SquawkContract.TABLE_PATH, SQUAWKS);
    }

    @Override
    public boolean onCreate() {
        helper = new SquawkDatabase(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        if (matcher.match(uri) != SQUAWKS) {
            return null;
        }
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor returnValue = db.query(SquawkContract.TABLE_PATH, projection, selection, selectionArgs, null, null, sortOrder);
        returnValue.setNotificationUri(getContext().getContentResolver(), uri);
        return returnValue;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}