package com.my.kellen.dailychecklist;

import java.util.ArrayList;

class QueryMaker {
    private final String today;
    private final boolean showAll;
    private final boolean showComplete;

    QueryMaker(String today, boolean showAll, boolean showComplete) {
        this.today = today;
        this.showAll = showAll;
        this.showComplete = showComplete;
    }

    String makeQuery(){
        ArrayList<String> queryList = new ArrayList<>();
        if(!showAll){
            queryList.add(Contract.Entry.COLUMN_DATE + "='" + today + "'");
        }
        if(!showComplete){
            queryList.add(Contract.Entry.COLUMN_COMPLETE + "=" + "0");
        }
        return this.combineQueries(queryList);
    }

    private String combineQueries(ArrayList<String> queryList){
        if (queryList.size() <= 0){
           return null;
        }
        StringBuilder finalQuery = new StringBuilder(queryList.get(0));
        for (int i = 1; i <queryList.size() ; i++) {
            finalQuery.append(" AND ").append(queryList.get(i));
        }
        return finalQuery.toString();
    }
}
