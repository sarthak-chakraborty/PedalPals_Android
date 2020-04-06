package com.example.pedalpals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Random;

public class Database extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "pedalpals.db";

    public static final String TABLE_USER = "User";
    public static final String USER_COL_1 = "username";
    public static final String USER_COL_2 = "first_name";
    public static final String USER_COL_3 = "last_name";
    public static final String USER_COL_4 = "email_id";
    public static final String USER_COL_5="room";
    public static final String USER_COL_6="hall";
    public static final String USER_COL_7="passw";
    public static final String USER_COL_8 = "rating";
    public static final String USER_COL_9 = "mobile_number";

    public static final String TABLE_ADMIN = "Admin";
    public static final String ADMIN_COL_1 = "username";
    public static final String ADMIN_COL_2 = "first_name";
    public static final String ADMIN_COL_3 = "last_name";
    public static final String ADMIN_COL_4 = "email_id";
    public static final String ADMIN_COL_5="passw";

    public static final String TABLE_CYCLE = "Cycle";
    public static final String CYCLE_COL_1 = "reg_no";
    public static final String CYCLE_COL_2 = "model";
    public static final String CYCLE_COL_3 = "color";
    public static final String CYCLE_COL_4 = "location";
    public static final String CYCLE_COL_5 = "price";
    public static final String CYCLE_COL_6 = "username";
    public static final String CYCLE_COL_7 = "rating";
    public static final String CYCLE_COL_8 = "cycle_condition";

    public static final String TABLE_LOCATION = "Location";
    public static final String LOCATION_COL_1 = "name";

    public static final String TABLE_TRANSACTION = "Transactions";
    public static final String TRANSACTION_COL_1 = "transaction_id";
    public static final String TRANSACTION_COL_2 = "username";
    public static final String TRANSACTION_COL_3 = "owner";
    public static final String TRANSACTION_COL_4 = "reg_no";
    public static final String TRANSACTION_COL_5 = "start_date";
    public static final String TRANSACTION_COL_6 = "end_date";
    public static final String TRANSACTION_COL_7 = "price_per_day";
    public static final String TRANSACTION_COL_8 = "user_rating";
    public static final String TRANSACTION_COL_9 = "cycle_rating";

    public static final String TABLE_CONTACT = "contact_us";
    public static final String CONTACT_COL_1 = "name";
    public static final String CONTACT_COL_2 = "email_id";
    public static final String CONTACT_COL_3 = "subject";
    public static final String CONTACT_COL_4 = "body";
    public static final String CONTACT_COL_5 = "query_date";

    public Database(Context context){
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_USER + "(username varchar(30) primary key, " +
                "first_name varchar(30) not null," +
                "last_name varchar(30)," +
                "email_id varchar(40) not null," +
                "room varchar(10) not null," +
                "hall varchar(50) not null," +
                "passw varchar(50) not null," +
                "rating numeric(3,2) default 0,"+
                "mobile_number char(10) not null unique);");

        db.execSQL("create table " + TABLE_ADMIN + "(username varchar(30) primary key, " +
                "first_name varchar(30) not null," +
                "last_name varchar(30)," +
                "email_id varchar(40) not null," +
                "passw varchar(50) not null);" );

        db.execSQL("create table " + TABLE_CYCLE + "(reg_no int primary key," +
                "model varchar(30) not null," +
                "color varchar(20) not null," +
                "location varchar(30) not null," +
                "price int not null," +
                "username varchar(30) not null," +
                "rating numeric(3,2) default 0," +
                "cycle_condition varchar(50) not null," +
                "foreign key(username) references User(username) on delete cascade," +
                "foreign key(location) references Location(name) on delete cascade);" );

        db.execSQL("create table " + TABLE_LOCATION + "(name varchar(30) primary key);");

        db.execSQL("create table " + TABLE_TRANSACTION + "(transaction_id int primary key," +
                "username varchar(30)," +
                "owner varchar(20)," +
                "reg_no varchar(30)," +
                "start_date date," +
                "end_date date," +
                "price_per_day int," +
                "user_rating numeric(3,2) default 0," +
                "cycle_rating numeric(3,2) default 0," +
                "foreign key(username) references User(username) on delete cascade," +
                "foreign key(owner) references User(username) on delete cascade," +
                "foreign key(reg_no) references Cycle(reg_no) on delete cascade);" );

        db.execSQL("create table " + TABLE_CONTACT + "(name varchar(30) not null, " +
                "email_id varchar(40) not null," +
                "subject varchar(50) not null," +
                "body varchar(150) not null," +
                "query_date date not null," +
                "primary key(email_id,subject,query_date));" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_ADMIN);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_CYCLE);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);

        onCreate(db);
    }


    public boolean insertData_User(String first_name,String last_name,String email,String hall,String room, String username, String password, String mobile_number) {
        SQLiteDatabase db = this.getWritableDatabase();

        if(username.isEmpty() || first_name.isEmpty() || email.isEmpty() || password.isEmpty())
            return false;

        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COL_1, username);
        contentValues.put(USER_COL_2, first_name);
        contentValues.put(USER_COL_3, last_name);
        contentValues.put(USER_COL_4, email);
        contentValues.put(USER_COL_5, room);
        contentValues.put(USER_COL_6, hall);
        contentValues.put(USER_COL_7, password);
        contentValues.put(USER_COL_8, 0);
        contentValues.put(USER_COL_9, mobile_number);
        long result = db.insert(TABLE_USER,null, contentValues);

        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertData_Contact(String name,String email_id,String subject,String body, String q_date) {
        SQLiteDatabase db = this.getWritableDatabase();

        if(name.isEmpty() || email_id.isEmpty() || subject.isEmpty() || body.isEmpty())
            return false;

        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACT_COL_1, name);
        contentValues.put(CONTACT_COL_2, email_id);
        contentValues.put(CONTACT_COL_3, subject);
        contentValues.put(CONTACT_COL_4, body);
        contentValues.put(CONTACT_COL_5, q_date);

        long result = db.insert(TABLE_CONTACT,null, contentValues);

        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertData_Admin(String first_name,String last_name,String email,String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        if(username.isEmpty() || first_name.isEmpty() || email.isEmpty() || password.isEmpty() )
            return false;

        ContentValues contentValues = new ContentValues();
        contentValues.put(ADMIN_COL_1, username);
        contentValues.put(ADMIN_COL_2, first_name);
        contentValues.put(ADMIN_COL_3, last_name);
        contentValues.put(ADMIN_COL_4, email);
        contentValues.put(ADMIN_COL_5, password);
        long result = db.insert(TABLE_ADMIN,null, contentValues);

        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertData_Cycle(int reg_no, String model, String color, String location, int price, String username, String condition) {
        SQLiteDatabase db = this.getWritableDatabase();

        if(model.isEmpty() || color.isEmpty() || username.isEmpty() || location.isEmpty())
            return false;

        ContentValues contentValues = new ContentValues();
        contentValues.put(CYCLE_COL_1, reg_no);
        contentValues.put(CYCLE_COL_2, model);
        contentValues.put(CYCLE_COL_3, color);
        contentValues.put(CYCLE_COL_4, location);
        contentValues.put(CYCLE_COL_5, price);
        contentValues.put(CYCLE_COL_6, username);
        contentValues.put(CYCLE_COL_7, 0);
        contentValues.put(CYCLE_COL_8, condition);
        long result = db.insert(TABLE_CYCLE,null, contentValues);

        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertData_Transaction(String username, String owner, String reg_no, String start_date, String end_date, int price){
        SQLiteDatabase db = this.getWritableDatabase();

        Random rand = new Random();
        int t_id = rand.nextInt(1000);

        if (username.isEmpty() || owner.isEmpty() || start_date.isEmpty() || end_date.isEmpty())
            return false;

        ContentValues contentValues = new ContentValues();
        contentValues.put(TRANSACTION_COL_1, t_id);
        contentValues.put(TRANSACTION_COL_2, username);
        contentValues.put(TRANSACTION_COL_3, owner);
        contentValues.put(TRANSACTION_COL_4, Integer.parseInt(reg_no));
        contentValues.put(TRANSACTION_COL_5, start_date);
        contentValues.put(TRANSACTION_COL_6, end_date);
        contentValues.put(TRANSACTION_COL_7, price);
        contentValues.put(TRANSACTION_COL_8, 0);
        contentValues.put(TRANSACTION_COL_9, 0);
        long result = db.insert(TABLE_TRANSACTION, null, contentValues);

        if(result == -1)
            return false;
        else
            return true;
    }


    public boolean updateData_User(String username, String first_name, String last_name, String email, String hall,String room,String mobile_number) {
        SQLiteDatabase db = this.getWritableDatabase();

        if(username.isEmpty() || first_name.isEmpty() || email.isEmpty() || mobile_number.isEmpty())
            return false;

        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COL_1, username);
        contentValues.put(USER_COL_2, first_name);
        contentValues.put(USER_COL_3, last_name);
        contentValues.put(USER_COL_4, email);
        contentValues.put(USER_COL_5, room);
        contentValues.put(USER_COL_6, hall);
        contentValues.put(USER_COL_9, mobile_number);
        db.update(TABLE_USER, contentValues, "username=?", new String[]{username});
        return true;
    }


    public boolean updateData_User_pass(String username, String first_name, String last_name, String email, String hall,String room, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        if(username.isEmpty() || first_name.isEmpty() || email.isEmpty() || password.isEmpty())
            return false;

        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COL_1, username);
        contentValues.put(USER_COL_2, first_name);
        contentValues.put(USER_COL_3, last_name);
        contentValues.put(USER_COL_4, email);
        contentValues.put(USER_COL_5, room);
        contentValues.put(USER_COL_6, hall);
        contentValues.put(USER_COL_7, password);
        db.update(TABLE_USER, contentValues, "username=?", new String[]{username});
        return true;
    }


    public void updateData_User_rating(String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE "+TABLE_USER+" SET rating=(SELECT avg(user_rating) FROM "+TABLE_TRANSACTION+" WHERE username='"+username+"') WHERE username='"+username+"';");
    }

    public boolean updateData_Cycle(String reg_no, String model, String color, String location, int price,String condition) {
        SQLiteDatabase db = this.getWritableDatabase();

        if(model.isEmpty() || color.isEmpty() || location.isEmpty())
            return false;

        ContentValues contentValues = new ContentValues();
        contentValues.put(CYCLE_COL_1, Integer.parseInt(reg_no));
        contentValues.put(CYCLE_COL_2, model);
        contentValues.put(CYCLE_COL_3, color);
        contentValues.put(CYCLE_COL_4, location);
        contentValues.put(CYCLE_COL_5, price);
        contentValues.put(CYCLE_COL_8, condition);
        db.update(TABLE_CYCLE, contentValues, "reg_no=?", new String[]{reg_no});
        return true;
    }

    public void updateData_Cycle_rating(String reg_no) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE "+TABLE_CYCLE+" SET rating=(SELECT avg(cycle_rating) FROM "+TABLE_TRANSACTION+" WHERE reg_no="+Integer.parseInt(reg_no)+") WHERE reg_no="+Integer.parseInt(reg_no)+";");
    }


    public boolean updateTransaction_rating_cycle(String transac_id, float rating){
        SQLiteDatabase db = this.getWritableDatabase();

        if(rating == 0)
            return false;

        ContentValues contentValues = new ContentValues();
        contentValues.put(TRANSACTION_COL_1, Integer.parseInt(transac_id));
        contentValues.put(TRANSACTION_COL_9, rating);
        db.update(TABLE_TRANSACTION, contentValues, "transaction_id=?", new String[]{transac_id});
        return true;
    }


    public boolean updateTransaction_rating_user(String transac_id, float rating){
        SQLiteDatabase db = this.getWritableDatabase();

        if(rating == 0)
            return false;

        ContentValues contentValues = new ContentValues();
        contentValues.put(TRANSACTION_COL_1, Integer.parseInt(transac_id));
        contentValues.put(TRANSACTION_COL_8, rating);
        db.update(TABLE_TRANSACTION, contentValues, "transaction_id=?", new String[]{transac_id});
        return true;
    }


    public boolean insertData_Location(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        if(name.isEmpty())
            return false;

        ContentValues contentValues = new ContentValues();
        contentValues.put(LOCATION_COL_1, name);
        long result = db.insert(TABLE_LOCATION,null, contentValues);

        if(result == -1)
            return false;
        else
            return true;
    }


    public void deleteData_User(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, null, null);
    }

    public void deleteData_Admin(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ADMIN, null, null);
    }

    public int deleteUser(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_USER, "username=?", new String[]{username});
    }

    public int deleteQuery(String date,String email,String subject){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CONTACT, "query_date=? AND email_id=? AND subject=?", new String[]{date,email,subject});
    }

    public int deleteData_Cycle(String reg_no, String username){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CYCLE, "reg_no=? AND username=?", new String[]{reg_no, username});
    }

    public int deleteData_Contact(String email, String subject,String q_Date){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CONTACT, "email_id=? AND query_date=? AND subject=?", new String[]{email, q_Date, subject});
    }
    
    public void deleteData_Location(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCATION, null, null);
    }
    public int deleteData_Location(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_LOCATION, "name = ?", new String[]{name});
    }

    public void deleteData_Transaction(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTION, null, null);
    }


    public boolean login_User(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_USER+" WHERE username=? AND passw=?", new String[]{username, password});
        if (result.getCount() == 1)
            return true;
        else
            return false;
    }

    public boolean login_Admin(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_ADMIN+" WHERE username=? AND passw=?", new String[]{username, password});
        if (result.getCount() == 1)
            return true;
        else
            return false;
    }


    public Cursor getAllData_User(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_USER, null);
        return result;
    }

    public Cursor getAllData_Contact(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_CONTACT, null);
        return result;
    }

    public Cursor getAllData_User_cycle(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select username,count(reg_no) as c from "+ TABLE_USER+" natural left outer join cycle group by username", null);
        return result;
    }

    public Cursor getAllData_Location(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_LOCATION, null);
        return result;
    }

    public Cursor getAllData_Transaction(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_TRANSACTION, null);
        return result;
    }


    public Cursor getData_User_username(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_USER+" WHERE username=?", new String[]{username});
        return result;
    }

    public Cursor getRegNo_Cycle_username(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT reg_no FROM "+TABLE_CYCLE+" WHERE username=?", new String[]{username});
        return result;
    }

    public Cursor getLocation_name(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT name FROM "+TABLE_LOCATION+" WHERE name=?", new String[]{name});
        return result;
    }

    public Cursor getData_User_email_id(String email){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_USER+" WHERE email_id=?", new String[]{email});
        return result;
    }

    public Cursor getData_User_mobile_number(String mobile_number){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_USER+" WHERE mobile_number=?", new String[]{mobile_number});
        return result;
    }

    public Cursor getAllData_Cycle(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_CYCLE, null);
        return result;
    }

    public Cursor getData_Cycle_reg(String reg_no){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_CYCLE+" WHERE reg_no=?", new String[]{reg_no});
        return result;
    }

    public Cursor getData_Cycle_username(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_CYCLE+" WHERE username=?", new String[]{username});
        return result;
    }

    public Cursor getData_Cycle_GetRide(String username, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_CYCLE+" WHERE username!=? AND reg_no NOT IN (SELECT reg_no FROM "+TABLE_TRANSACTION+" WHERE end_date>=?)", new String[]{username, date});
        return result;
    }

    public Cursor getData_Transaction_delete(String reg_no, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_TRANSACTION+" WHERE reg_no=? AND end_date>=?", new String[]{reg_no, date});
        return result;
    }

    public Cursor getData_Transaction_user_delete(String username, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_TRANSACTION+" WHERE (username=? OR owner=? ) AND end_date>=?", new String[]{username, username, date});
        return result;
    }

    public Cursor getData_Transaction_ride(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_TRANSACTION+" WHERE username=? ORDER BY end_date DESC", new String[]{username});
        return result;
    }

    public Cursor getData_Transaction_rent(String owner){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_TRANSACTION+" WHERE owner=? ORDER BY end_date DESC", new String[]{owner});
        return result;
    }
}
