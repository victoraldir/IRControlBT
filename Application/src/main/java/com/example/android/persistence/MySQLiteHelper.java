package com.example.android.persistence;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {
 
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "ircontroldb";
    
    public final String TB_COMANDO = "comando";
    public final String TB_DISPOSITIVO = "dispositivo";
    public final String TB_AMBIENTE = "ambiente";

    // Table Comando names
    //public static final String KEY_COMANDO_ID = "id";
    public static final String KEY_COMANDO_DESCRICAO = "descricao";
    public static final String KEY_COMANDO_CODIGO = "codigo";
    public static final String KEY_COMANDO_POSICAO_ID = "posicao_id";
    public static final String KEY_COMANDO_DISPOSITIVO_ID = "dispositivo_id";
    
    
    // Table Dispositivo names
    public static final String KEY_DISPOSITIVO_ID = "id";
    public static final String KEY_DISPOSITIVO_DESCRICAO = "descricao";
    public static final String KEY_DISPOSITIVO_AMBIENTE_ID = "ambiente_id";

    // Table Ambiente names
    public static final String KEY_AMBIENTE_ID = "id";
    public static final String KEY_AMBIENTE_DESCRICAO = "descricao";

    private static MySQLiteHelper instance;
    
    public static synchronized MySQLiteHelper getInstance(Context ctx){
    	if(instance == null){
    		instance = new MySQLiteHelper(ctx);
    	}
    	
    	return instance;
    }	
    
    private MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }
    
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
 
	@Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table

        String CREATE_AMBIENTE_TABLE = "CREATE TABLE " + TB_AMBIENTE + " ( " +
                "id INTEGER PRIMARY KEY, " +
                "descricao TEXT)";

        String CREATE_DISPOSITIVO_TABLE = "CREATE TABLE " + TB_DISPOSITIVO + " ( " +
                "id INTEGER PRIMARY KEY, " + 
                "descricao TEXT, " +
                "ambiente_id INTEGER, " +
                "FOREIGN KEY (ambiente_id) REFERENCES " + TB_AMBIENTE + " (id)  ON DELETE CASCADE)";
 
        String CREATE_COMANDO_TABLE = "CREATE TABLE " + TB_COMANDO + " ( " +
                "descricao TEXT, "
                + "codigo TEXT, "
                + "posicao_id INTEGER, "
                + " dispositivo_id INTEGER,"
                + " PRIMARY KEY ( posicao_id, dispositivo_id),"
                + " FOREIGN KEY (dispositivo_id) REFERENCES " + TB_DISPOSITIVO + " (id)  ON DELETE CASCADE)";

        // create books table
        db.execSQL(CREATE_AMBIENTE_TABLE);
        db.execSQL(CREATE_DISPOSITIVO_TABLE);
        db.execSQL(CREATE_COMANDO_TABLE);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TB_AMBIENTE);
        db.execSQL("DROP TABLE IF EXISTS " + TB_DISPOSITIVO);
        db.execSQL("DROP TABLE IF EXISTS " + TB_COMANDO);
 
        // create fresh books table
        this.onCreate(db);
    }

    public void inserirAtualizarAmbiente(Ambiente ambiente){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_AMBIENTE_DESCRICAO, ambiente.getDescricao());

        if(ambiente.getId() != 0){
            values.put(KEY_AMBIENTE_ID, ambiente.getId());
            db.update(TB_AMBIENTE, values, KEY_AMBIENTE_ID + " = ?",new String[]{String.valueOf(ambiente.getId())});
        }else{
            db.insert(TB_AMBIENTE,null,values);
        }

        // 4. close
        db.close();
    }
    
    public void inserirAtualizarDispositivo(Dispositivo dispositivo){

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_DISPOSITIVO_DESCRICAO, dispositivo.getDescricao());
        values.put(KEY_DISPOSITIVO_AMBIENTE_ID, dispositivo.getAmbiente().getId());

        if(dispositivo.getId() != 0){
            values.put(KEY_DISPOSITIVO_ID, dispositivo.getId());

            db.update(TB_DISPOSITIVO, values, KEY_DISPOSITIVO_ID + " = ?",new String[]{String.valueOf(dispositivo.getId())});
        }else{
            db.insert(TB_DISPOSITIVO,null,values);
        }

		// 4. close
		db.close(); 
    }

	public void deletarDispositivo(long id){
		String query = "DELETE FROM " + TB_DISPOSITIVO + " WHERE " + KEY_DISPOSITIVO_ID + " = " + id;
		
		// 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(query);
        //db.endTransaction();
        if (db!=null){
            db.close();
        }
	}

    public void deleteRoom(long idRoom){
        String query = "DELETE FROM " + TB_AMBIENTE + " WHERE " + KEY_AMBIENTE_ID + " = " + idRoom;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(query);
        //db.endTransaction();
        if (db!=null){
            db.close();
        }
    }

    public List<Dispositivo> listarDispositivos() throws ParseException {
    	
    	List<Dispositivo> dispositivos = new LinkedList<Dispositivo>();
    	 
        // 1. build the query
        //String query = "SELECT * FROM " + TB_SOLICITACAO + " WHERE cancelamento = 0";
        
        String query = "SELECT * FROM " + TB_DISPOSITIVO + " AS d LEFT OUTER JOIN " + TB_COMANDO + " AS c ON d." + KEY_DISPOSITIVO_ID + " = c." + KEY_COMANDO_DISPOSITIVO_ID ;
        
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        //db.endTransaction();
  
        // 3. go over each row, build book and add it to list
        Dispositivo dispositivo = null;
        if (cursor.moveToFirst()) {
            do {

                dispositivo = new Dispositivo();
                dispositivo.setId(Integer.parseInt(cursor.getString(0)));
                dispositivo.setDescricao(cursor.getString(1));

                //dispositivo.setComandos(listarRastreiosPorSolicitacaoId(solicitaca  o.getId()));
                
                // Add book to books
                dispositivos.add(dispositivo);
            } while (cursor.moveToNext());
        }
  
        if (cursor!=null){
            cursor.close();
        }
        if (db!=null){
            db.close();
        }
        
        if(dispositivos.isEmpty()){
        	return null;
        }else{

        	return dispositivos;
        	
        }

    }

    public List<Dispositivo> listAppliancesByIdRoom(long idRoom) throws ParseException {

        List<Dispositivo> dispositivos = new LinkedList<Dispositivo>();

        // 1. build the query
        //String query = "SELECT * FROM " + TB_SOLICITACAO + " WHERE cancelamento = 0";

        String query = "SELECT * FROM " + TB_DISPOSITIVO + " AS d LEFT OUTER JOIN " + TB_COMANDO + " AS c ON d." + KEY_DISPOSITIVO_ID + " = c." + KEY_COMANDO_DISPOSITIVO_ID + " WHERE " + KEY_DISPOSITIVO_AMBIENTE_ID + " = " + idRoom;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        //db.endTransaction();

        // 3. go over each row, build book and add it to list
        Dispositivo dispositivo = null;
        if (cursor.moveToFirst()) {
            do {

                dispositivo = new Dispositivo();
                dispositivo.setId(Integer.parseInt(cursor.getString(0)));
                dispositivo.setDescricao(cursor.getString(1));
                dispositivo.setAmbiente(getRoomById(cursor.getLong(2)));
                //dispositivo.setComandos(listarRastreiosPorSolicitacaoId(solicitaca  o.getId()));

                // Add book to books
                dispositivos.add(dispositivo);
            } while (cursor.moveToNext());
        }

        if (cursor!=null){
            cursor.close();
        }
        if (db!=null){
            db.close();
        }

        if(dispositivos.isEmpty()){
            return null;
        }else{

            return dispositivos;

        }

    }

    public List<Ambiente> listarAmbientes(){

        List<Ambiente> ambientes = new LinkedList<Ambiente>();

        // 1. build the query
        //String query = "SELECT * FROM " + TB_SOLICITACAO + " WHERE cancelamento = 0";

        String query = "SELECT * FROM " + TB_AMBIENTE;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        //db.endTransaction();

        // 3. go over each row, build book and add it to list
        Ambiente ambiente = null;
        if (cursor.moveToFirst()) {
            do {

                ambiente = new Ambiente();
                ambiente.setId(Integer.parseInt(cursor.getString(0)));
                ambiente.setDescricao(cursor.getString(1));

                //dispositivo.setComandos(listarRastreiosPorSolicitacaoId(solicitaca  o.getId()));

                // Add book to books
                ambientes.add(ambiente);
            } while (cursor.moveToNext());
        }

        if (cursor!=null){
            cursor.close();
        }
        if (db!=null){
            db.close();
        }

        if(ambientes.isEmpty()){
            return null;
        }else{

            return ambientes;

        }

    }

    public Dispositivo getApplianceById(long id) throws ParseException {

        List<Dispositivo> dispositivos = new LinkedList<Dispositivo>();

        // 1. build the query
        //String query = "SELECT * FROM " + TB_SOLICITACAO + " WHERE cancelamento = 0";

        String query = "SELECT * FROM " + TB_DISPOSITIVO + " AS d INNER JOIN " + TB_AMBIENTE + " AS a ON d." + KEY_DISPOSITIVO_AMBIENTE_ID + " = a." + KEY_AMBIENTE_ID  +" WHERE d." + KEY_DISPOSITIVO_ID + " = " + id;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        //db.endTransaction();

        // 3. go over each row, build book and add it to list
        Dispositivo dispositivo = null;
        if (cursor.moveToFirst()) {
            do {

                dispositivo = new Dispositivo();
                dispositivo.setId(Integer.parseInt(cursor.getString(0)));
                dispositivo.setDescricao(cursor.getString(1));
                dispositivo.setAmbiente(getRoomById(cursor.getLong(2)));

                dispositivo.setComandos(listarComandosPorDispositivoId(Integer.parseInt(cursor.getString(0))) == null ? null : new HashSet<Comando>(listarComandosPorDispositivoId(cursor.getLong(2))));

                // Add book to books
                //dispositivos.add(dispositivo);
            } while (cursor.moveToNext());
        }

        if (cursor!=null){
            cursor.close();
        }
        if (db!=null){
            db.close();
        }

        return dispositivo;

    }

    public Comando getCommandById(long idAppliance, long idPosition) throws ParseException {

        //List<Comando> comando = new LinkedList<Comando>();

        // 1. build the query
        //String query = "SELECT * FROM " + TB_SOLICITACAO + " WHERE cancelamento = 0";

        String query = "SELECT * FROM " + TB_COMANDO + " WHERE " + KEY_COMANDO_DISPOSITIVO_ID + " = " + idAppliance + " AND " + KEY_COMANDO_POSICAO_ID + " = " + idPosition;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        //db.endTransaction();

        // 3. go over each row, build book and add it to list
        Comando comando = null;
        if (cursor.moveToFirst()) {
            do {

                comando = new Comando();
                //comando.setId(Integer.parseInt(cursor.getString(0)));
                comando.setDescricao(cursor.getString(cursor.getColumnIndex(KEY_COMANDO_DESCRICAO)));
                comando.setDispositivo(getApplianceById(cursor.getLong(cursor.getColumnIndex(KEY_COMANDO_DISPOSITIVO_ID))));
                comando.setPosicao(Posicao.values()[(int) cursor.getLong(cursor.getColumnIndex(KEY_COMANDO_POSICAO_ID))]);
                comando.setCodigo(cursor.getString(cursor.getColumnIndex(KEY_COMANDO_CODIGO)));

                //dispositivo.setComandos(listarComandosPorDispositivoId(Integer.parseInt(cursor.getString(0))) == null ? null : new HashSet<Comando>(listarComandosPorDispositivoId(cursor.getLong(2))));

                //dispositivos.add(dispositivo);
            } while (cursor.moveToNext());
        }

        if (cursor!=null){
            cursor.close();
        }
        if (db!=null){
            db.close();
        }

        return comando;

    }

    public Ambiente getRoomById(Long id) throws ParseException {

        List<Dispositivo> dispositivos = new LinkedList<Dispositivo>();

        // 1. build the query
        //String query = "SELECT * FROM " + TB_SOLICITACAO + " WHERE cancelamento = 0";

        String query = "SELECT * FROM " + TB_AMBIENTE + " WHERE " + KEY_AMBIENTE_ID + " = " + id;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        //db.endTransaction();

        // 3. go over each row, build book and add it to list
        Ambiente ambiente = null;
        if (cursor.moveToFirst()) {
            do {

                ambiente = new Ambiente();
                ambiente.setId(Integer.parseInt(cursor.getString(0)));
                ambiente.setDescricao(cursor.getString(1));

                // Add book to books
                //dispositivos.add(dispositivo);
            } while (cursor.moveToNext());
        }

        if (cursor!=null){
            cursor.close();
        }
        if (db!=null){
            db.close();
        }

        return ambiente;

    }

    public List<Comando> listarComandos() throws ParseException {

        List<Comando> comandos = new LinkedList<Comando>();

        // 1. build the query
        //String query = "SELECT * FROM " + TB_SOLICITACAO + " WHERE cancelamento = 0";

        String query = "SELECT * FROM " + TB_COMANDO + " AS c ";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        //db.endTransaction();

        // 3. go over each row, build book and add it to list
        Comando comando = null;
        if (cursor.moveToFirst()) {
            do {

                comando = new Comando();
                //comando.setId(Integer.parseInt(cursor.getString(0)));
                comando.setDescricao(cursor.getString(1));

                comando.setDispositivo(getApplianceById(cursor.getLong(2)));

                // Add book to books
                comandos.add(comando);
            } while (cursor.moveToNext());
        }

        if (cursor!=null){
            cursor.close();
        }
        if (db!=null){
            db.close();
        }

        if(comandos.isEmpty()){
            return null;
        }else{

            return comandos;

        }

    }

    public List<Comando> listarComandosPorDispositivoId(long id) throws ParseException {

        List<Comando> comandos = new LinkedList<Comando>();

        // 1. build the query
        //String query = "SELECT * FROM " + TB_SOLICITACAO + " WHERE cancelamento = 0";

        String query = "SELECT * FROM " + TB_COMANDO + " AS c WHERE c." + KEY_COMANDO_DISPOSITIVO_ID + " = " + id;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        //db.endTransaction();

        // 3. go over each row, build book and add it to list
        Comando comando = null;
        if (cursor.moveToFirst()) {
            do {

                comando = new Comando();
                //comando.setId(Integer.parseInt(cursor.getString(0)));
                comando.setDescricao(cursor.getString(1));

                comando.setDispositivo(getApplianceById(cursor.getLong(2)));

                // Add book to books
                comandos.add(comando);
            } while (cursor.moveToNext());
        }

        if (cursor!=null){
            cursor.close();
        }
        if (db!=null){
            db.close();
        }

        if(comandos.isEmpty()){
            return null;
        }else{

            return comandos;

        }

    }

    public void inserirAtualizarComando(Comando comando){

        SQLiteDatabase db;

        ContentValues values = new ContentValues();

        values.put(KEY_COMANDO_DESCRICAO, comando.getDescricao());
        values.put(KEY_COMANDO_CODIGO, comando.getCodigo());
        values.put(KEY_COMANDO_DISPOSITIVO_ID, comando.getDispositivo().getId());
        values.put(KEY_COMANDO_POSICAO_ID, comando.getPosicao().ordinal());


        try {
            Comando cmdDb = getCommandById(comando.getDispositivo().getId(), comando.getPosicao().ordinal());

            db = this.getWritableDatabase();

            if(cmdDb != null){
                db.update(TB_DISPOSITIVO, values, KEY_COMANDO_DISPOSITIVO_ID + " = ? AND " + KEY_COMANDO_POSICAO_ID + " =?",new String[]{String.valueOf(comando.getDispositivo().getId()),String.valueOf(comando.getPosicao().ordinal())});
            }else{
                db.insert(TB_DISPOSITIVO,null,values);
            }

            // 4. close
            db.close();

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}