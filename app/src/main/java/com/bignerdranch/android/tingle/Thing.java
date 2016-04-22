package com.bignerdranch.android.tingle;

import java.util.UUID;

public class Thing {

    private String mWhat = null;
    private String mWhere = null;
    private UUID mId = null;

    public Thing (String what, String where)
    {
        mId = UUID.randomUUID();
        mWhat = what;
        mWhere = where;
    }

    public Thing (String what, String where, UUID id)
    {
        mId = id;
        mWhat = what;
        mWhere = where;
    }

    @Override
    public String toString() { return oneLine("Item: ", "Is here: "); }

    public String getWhat() { return mWhat; }
    public void setWhat(String what) { mWhat = what; }
    public String getWhere() { return mWhere; }
    public void setWhere(String where) { mWhere = where; }
    public UUID getId() { return mId; }

    public String oneLine(String pre, String post) {
        return pre + mWhat + " " + post + mWhere;
    }

}
