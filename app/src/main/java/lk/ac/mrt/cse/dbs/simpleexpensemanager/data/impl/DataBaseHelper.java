package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String ACCOUNT_Table = "Account";
    public static final String ACCOUNT_NO = "accountNo";
    public static final String BANK_NAME = "bankName";
    public static final String ACCOUNT_HOLDER_NAME = "accountHolderName";
    public static final String BALANCE = "balance";

    public static final String TRANSACTION_TABLE = "Transaction_Table";
    public static final String DATE = "date";
    public static final String EXPENSE_TYPE = "expenseType";
    public static final String AMOUNT = "amount";
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public DataBaseHelper(@Nullable Context context) {
        super(context, "expenseManger.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ACCOUNT_Table + "(" + ACCOUNT_NO + " text primary key," + BANK_NAME + " text," + ACCOUNT_HOLDER_NAME + " text," + BALANCE + " real)");
        db.execSQL("create table " + TRANSACTION_TABLE + "(" + ACCOUNT_NO + " text ," + DATE + " text," + EXPENSE_TYPE + " text," + AMOUNT + " real)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table "+TRANSACTION_TABLE);
        db.execSQL("drop table "+ ACCOUNT_Table);
    }

    public List<String> getAccountNumbersList() {
        List<String> accountNumberList=new ArrayList<String>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select "+ACCOUNT_NO+" from "+ ACCOUNT_Table,null);
        if (cursor.moveToFirst()){
            do {
                accountNumberList.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return accountNumberList;
    }

    public List<Account> getAccountsList() {
        List<Account> accountList=new ArrayList<Account>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+ ACCOUNT_Table,null);
        if (cursor.moveToFirst()){
            do {
                String accountNo=cursor.getString(0);
                String bankName=cursor.getString(1);
                String accountHolderName=cursor.getString(2);
                double balance=cursor.getDouble(3);
                accountList.add(new Account(accountNo,bankName,accountHolderName,balance));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return accountList;
    }

    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from "+ ACCOUNT_Table +" where "+ACCOUNT_NO+" = '"+accountNo+"'",null);
        if (cursor.moveToFirst()){
            String bankName=cursor.getString(1);
            String accountHolderName=cursor.getString(2);
            double balance=cursor.getDouble(3);
            Account account=new Account(accountNo,bankName,accountHolderName,balance);
            cursor.close();
            db.close();
            return account;
        }
        return null;
    }

    public void addAccount(Account account) {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(ACCOUNT_NO,account.getAccountNo());
        cv.put(BANK_NAME,account.getBankName());
        cv.put(ACCOUNT_HOLDER_NAME,account.getAccountHolderName());
        cv.put(BALANCE,account.getBalance());
        db.insert(ACCOUNT_Table,null,cv);
        db.close();
    }

    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from "+ACCOUNT_Table+" where "+ACCOUNT_NO+"='"+accountNo+"'");
        db.close();
    }

    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db=getWritableDatabase();
        Cursor cursor=db.rawQuery("select "+BALANCE+" from "+ACCOUNT_Table+" where "+ACCOUNT_NO+"='"+accountNo+"'",null);
        cursor.moveToFirst();
        double balance=cursor.getDouble(0);
        switch (expenseType) {
            case EXPENSE:
                db.execSQL("update "+ACCOUNT_Table+" set "+BALANCE+"="+(balance-amount)+" where "+ACCOUNT_NO+"='"+accountNo+"'");
                break;
            case INCOME:
                db.execSQL("update "+ACCOUNT_Table+" set "+BALANCE+"="+(balance+amount)+" where "+ACCOUNT_NO+"='"+accountNo+"'");
                break;
        }

    }

    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db=getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ContentValues cv=new ContentValues();
        cv.put(ACCOUNT_NO,accountNo);
        cv.put(DATE,dateFormat.format(date));
        cv.put(EXPENSE_TYPE,expenseType.toString());
        cv.put(AMOUNT,amount);
        db.insert(TRANSACTION_TABLE,null,cv);
        db.close();
    }

    public List<Transaction> getAllTransactionLogs() throws ParseException {
        List<Transaction> transactionList=new ArrayList<Transaction>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("select * from "+TRANSACTION_TABLE,null);
        if (cursor.moveToFirst()){
            do {
                String accountNo=cursor.getString(0);
                Date date=dateFormat.parse(cursor.getString(1));
                ExpenseType expenseType=ExpenseType.valueOf(cursor.getString(2));
                double amount=cursor.getDouble(3);
                transactionList.add(new Transaction(date,accountNo,expenseType,amount));
            }while (cursor.moveToNext());
        }
        db.close();
        return transactionList;
    }

    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {
        List<Transaction> transactions=getAllTransactionLogs();
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }
}
