/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author haipn
 */
public class BuildQueryString {

    public static String buildInsertQuery(String table, String[] columns, int nValues) {
        String query = "INSERT INTO %s(%s) VALUES %s";
        String column = "", value = "";
        for (int i = 1; i < columns.length; i++) {
            if (i < columns.length - 1) {
                column += columns[i] + ", ";
                value += "?, ";
            } else {
                column += columns[i];
                value += "?";
            }
        }
        value = "(" + value + ")";
        String values = new String();
        for (int i = 1; i < nValues - 1; i++) {
            values += value + ",";
        }
        values += value;
        return String.format(query, table, column, values);
    }

    public static String buildInsertQueryFullCollumns(String table, String[] columns, int nValues) {
        String query = "INSERT INTO %s(%s) VALUES %s";
        String column = "", value = "";
        for (int i = 0; i < columns.length; i++) {
            if (i < columns.length - 1) {
                column += columns[i] + ", ";
                value += "?, ";
            } else {
                column += columns[i];
                value += "?";
            }
        }
        value = "(" + value + ")";
        String values = new String();
        for (int i = 0; i < nValues - 1; i++) {
            values += value + ",";
        }
        values += value;
        return String.format(query, table, column, values);
    }

    public static String buildUpdateQuery(String table, String[] primaryKeys, String[] updateColumns) {
        String query = "UPDATE %s SET %s WHERE %s";
        String updateClause = updateColumns[0] + " = ?";
        for (int i = 1; i < updateColumns.length; i++) {
            updateClause += ", " + updateColumns[i] + " = ?";
        }
        String condition = primaryKeys[0] + " = ?";
        for (int i = 1; i < primaryKeys.length; i++) {
            condition += " AND " + primaryKeys[i] + " = ?";
        }
        return String.format(query, table, updateClause, condition);
    }

    public static String buildDeleteQuery(String table, String... keys) {
        String query = "DELETE FROM %s WHERE " + keys[0] + " = ?";
        for (int i = 1; i < keys.length; i++) {
            query += " AND " + keys[i] + " = ?";
        }
        return String.format(query, table);
    }

    public static String buildSelectByKeyQuery(String table, String... keys) {
        String query = "SELECT * FROM %s WHERE " + keys[0] + " = ?";
        for (int i = 1; i < keys.length; i++) {
            query += " AND " + keys[i] + " = ?";
        }
        return String.format(query, table);
    }

    public static String buildSelectQuery(String table) {
        String query = "SELECT * FROM %s";
        return String.format(query, table);
    }

    public static String buildSelectQuery(String table, String select, String where,
            String groupBy, String having, String orderBy, int limit) {
        String query = "SELECT %s FROM %s";
        if (!StringUtils.isBlank(where)) {
            query += " WHERE " + where.toLowerCase().replace("where", "");
        } else if (!StringUtils.isBlank(groupBy)) {
            query += " GROUP BY " + groupBy.toLowerCase().replace("order by", "");
            if (!StringUtils.isBlank(having)) {
                query += " HAVING " + having.toLowerCase().replace("having", "");
            }
        }
        if (!StringUtils.isBlank(orderBy)) {
            query += " ORDER BY " + orderBy.toLowerCase().replace("order by", "");
        }
        if (limit > 0) {
            query += " LIMIT " + limit;
        }
        if (StringUtils.isBlank(select)) {
            select = "*";
        }
        return String.format(query, select.toLowerCase().replace("SELECT", ""), table);
    }

    public static void main(String[] args) {
    }
}
