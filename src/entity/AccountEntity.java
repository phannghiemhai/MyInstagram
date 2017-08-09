/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import utils.Constants.AccessOption;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author haipn
 */
public class AccountEntity {

    public enum AccountType {

        GUEST(-1),
        ADMIN(0),
        SUPER_ADMIN(1);

        private int val;

        AccountType(int val) {
            this.val = val;
        }

        public int getVal() {
            return val;
        }

        public static AccountType findByVal(int val) {
            for (AccountType type : values()) {
                if (type.val == val) {
                    return type;
                }
            }
            return GUEST;
        }
    }

    public int id;
    public String userId;
    public String email;
    public String username;
    public AccountType accountType;
    public Set<AccessOption> setAccessOptions;
    public Set<Integer> likedImgIds;

    public AccountEntity(int id, String userId, String email, String username, AccountType accountType, Set<AccessOption> setAccessOptions) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.accountType = accountType;
        this.setAccessOptions = setAccessOptions;
    }
    
    public AccountEntity(ResultSet rs) {
        try {
            this.id = rs.getInt("id");
            this.userId = rs.getString("user_id");
            this.email = rs.getString("email");
            this.username = rs.getString("username");
            this.accountType = AccountType.findByVal(rs.getInt("account_type"));
            this.setAccessOptions = new HashSet<>();
        } catch (SQLException ex) {
            Logger.getLogger(AccountEntity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int setParameters(PreparedStatement stmt, int idx) {
        return idx;
    }
}
