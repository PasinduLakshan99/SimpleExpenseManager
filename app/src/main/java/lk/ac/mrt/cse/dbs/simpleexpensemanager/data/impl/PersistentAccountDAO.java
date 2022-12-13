package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;

import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {

    private Context context;

    public PersistentAccountDAO(Context context){
        this.context=context;
    }
    @Override
    public List<String> getAccountNumbersList() {
        DataBaseHelper dataBaseHelper=new DataBaseHelper(context);
        return dataBaseHelper.getAccountNumbersList();
    }

    @Override
    public List<Account> getAccountsList() {
        DataBaseHelper dataBaseHelper=new DataBaseHelper(context);
        return dataBaseHelper.getAccountsList();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        DataBaseHelper dataBaseHelper=new DataBaseHelper(context);
        Account account=dataBaseHelper.getAccount(accountNo);
        if (account != null) {
            return account;
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {

        DataBaseHelper dataBaseHelper=new DataBaseHelper(context);
        dataBaseHelper.addAccount(account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        DataBaseHelper dataBaseHelper=new DataBaseHelper(context);
        if (dataBaseHelper.getAccount(accountNo)==null) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        dataBaseHelper.removeAccount(accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        Account account = dataBaseHelper.getAccount(accountNo);
        if (account == null) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        dataBaseHelper.updateBalance(accountNo,expenseType,amount);

    }
}

