package com.bignerdranch.android.tingle.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.tingle.Thing;
import com.bignerdranch.android.tingle.database.TingleDbSchema.TingleTable;

import java.util.UUID;

public class ThingCursorWrapper extends CursorWrapper {
    public ThingCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Thing getThing() {

            String where = getString(getColumnIndex(TingleTable.Cols.where_thing));
            String what = getString(getColumnIndex(TingleTable.Cols.what_thing));
            String uuidString = getString(getColumnIndex(TingleTable.Cols.UUID));

            Thing thing = new Thing(what, where, UUID.fromString(uuidString));
            return thing;

    }
}
