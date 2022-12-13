package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private Context context;

    public PersistentTransactionDAO(Context context){
        this.context=context;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        DataBaseHelper dataBaseHelper=new DataBaseHelper(context);
        dataBaseHelper.logTransaction(date,accountNo,expenseType,amount);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        return dataBaseHelper.getAllTransactionLogs();
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {
        DataBaseHelper dataBaseHelper=new DataBaseHelper(context);
        int size = dataBaseHelper.getAllTransactionLogs().size();
        if (size <= limit) {
            return dataBaseHelper.getAllTransactionLogs();
        }
        // return the last <code>limit</code> number of transaction logs
        return getAllTransactionLogs().subList(size - limit, size);
    }

}
