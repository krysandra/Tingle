package com.bignerdranch.android.tingle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.tingle.database.ThingCursorWrapper;
import com.bignerdranch.android.tingle.database.TingleBaseHelper;
import com.bignerdranch.android.tingle.database.TingleDbSchema.TingleTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ThingsDB {
        private Context mContext;
        private SQLiteDatabase mDatabase;

        private static ThingsDB sThingsDB;
        private List<Thing> things;

        public static ThingsDB get(Context context) {
            if (sThingsDB == null) {
                sThingsDB= new ThingsDB(context);
            }
            return sThingsDB;
        }
        public List<Thing> getThingsDB() {
            //return mThingsDB;
            things = new ArrayList<>();
            ThingCursorWrapper cursor = queryThings(null, null);

            try {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    things.add(cursor.getThing());
                    cursor.moveToNext();
                }
            }finally {
                    cursor.close();

            }
            return things;
        }
        public void addThing(Thing thing) {
            ContentValues values = getContentValues(thing);
            mDatabase.insert(TingleTable.NAME, null, values);
            //things.add(thing);
        }
        public void updateThing(Thing thing) {
            String uuidString = thing.getId().toString();
            ContentValues values = getContentValues(thing);

            mDatabase.update(TingleTable.NAME, values, TingleTable.Cols.UUID + " = ? ", new String[]{uuidString});
        }
        public void deleteThing(int i) {
            String uuidString = things.get(i).getId().toString();
            things.remove(i);
            mDatabase.delete(TingleTable.NAME, TingleTable.Cols.UUID + " = ? ", new String[]{uuidString});
         }
        public int size() {
            if(things == null) {
                things = getThingsDB();
            }
            return things.size();
        }

        public Thing get(UUID id){ //return mThingsDB.get(i);
            ThingCursorWrapper cursor = queryThings(
                    TingleTable.Cols.UUID + " = ?",
                    new String[] {id.toString()}
            );
            try {
                if(cursor.getCount() == 0) { return null; }
                cursor.moveToFirst();
                return cursor.getThing();
            }
            finally {
                cursor.close();
            }
        }
        // Fill database for testing purposes
        private ThingsDB(Context context) {
            mContext = context.getApplicationContext();
            mDatabase = new TingleBaseHelper(mContext).getWritableDatabase();

            /*
            mThingsDB= new ArrayList<Thing>();
            mThingsDB.add(new Thing("Android Pnone", "Desk"));
            mThingsDB.add(new Thing("Big Nerd book", "Desk"));
            mThingsDB.add(new Thing("Cool thing", "The cool place"));
            */
        }

        private ThingCursorWrapper queryThings(String whereClause, String[] whereArgs) {
            Cursor cursor = mDatabase.query(
                    TingleTable.NAME,
                    null,
                    whereClause,
                    whereArgs,
                    null,
                    null,
                    null
            );
            return new ThingCursorWrapper(cursor);
        }

        private static ContentValues getContentValues(Thing thing) {
            ContentValues values = new ContentValues();
            values.put(TingleTable.Cols.UUID, thing.getId().toString());
            values.put(TingleTable.Cols.where_thing, thing.getWhere());
            values.put(TingleTable.Cols.what_thing, thing.getWhat());

            return values;
        }

    }

