package com.bignerdranch.android.tingle.database;

import java.sql.Date;

public class TingleDbSchema {
    public static final class TingleTable {
        public static final String NAME = "things";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String what_thing = "what_thing";
            public static final String where_thing = "where_thing";
        }
    }

}
