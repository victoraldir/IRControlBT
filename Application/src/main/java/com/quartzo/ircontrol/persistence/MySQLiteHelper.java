package com.quartzo.ircontrol.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Table Comando names
    //public static final String KEY_COMANDO_ID = "id";
    public static final String KEY_COMMAND_DESCRIPTION = "description";
    public static final String KEY_COMMAND_CODE = "code";
    public static final String KEY_COMMAND_POSITION_ID = "position_id";
    public static final String KEY_COMMAND_APPLIANCE_ID = "appliance_id";
    // Table Dispositivo names
    public static final String KEY_APPLIANCE_ID = "id";
    public static final String KEY_APPLIANCE_DESCRIPTION = "description";
    public static final String KEY_APPLIANCE_DEVICE_ID = "device_id";
    // Table Ambiente names
    public static final String KEY_DEVICE_ID = "id";
    public static final String KEY_DEVICE_HOST = "host";
    public static final String KEY_DEVICE_PORT = "port";
    public static final String KEY_DEVICE_DESCRIPTION = "description";
    // Database Version
    private static final int DATABASE_VERSION = 2;
    // Database Name
    private static final String DATABASE_NAME = "ircontroldb";
    private static MySQLiteHelper instance;
    public final String TB_COMMAND = "command";
    public final String TB_APPLIANCE = "appliance";
    public final String TB_DEVICE = "device";

    private MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized MySQLiteHelper getInstance(Context ctx) {
        if (instance == null) {
            instance = new MySQLiteHelper(ctx);
        }

        return instance;
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

        String CREATE_DEVICE_TABLE = "CREATE TABLE " + TB_DEVICE + " ( " +
                "id INTEGER PRIMARY KEY, " +
                "host TEXT, " +
                "port INTEGER, " +
                "description TEXT)";

        String CREATE_APPLIANCE_TABLE = "CREATE TABLE " + TB_APPLIANCE + " ( " +
                "id INTEGER PRIMARY KEY, " +
                "description TEXT, " +
                "device_id INTEGER, " +
                "FOREIGN KEY (device_id) REFERENCES " + TB_DEVICE + " (id)  ON DELETE CASCADE)";

        String CREATE_COMMAND_TABLE = "CREATE TABLE " + TB_COMMAND + " ( " +
                "description TEXT, "
                + "code TEXT, "
                + "position_id INTEGER, "
                + " appliance_id INTEGER,"
                + " PRIMARY KEY ( position_id, appliance_id),"
                + " FOREIGN KEY (appliance_id) REFERENCES " + TB_APPLIANCE + " (id)  ON DELETE CASCADE)";

        // create books table
        db.execSQL(CREATE_DEVICE_TABLE);
        db.execSQL(CREATE_APPLIANCE_TABLE);
        db.execSQL(CREATE_COMMAND_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TB_DEVICE);
        db.execSQL("DROP TABLE IF EXISTS " + TB_APPLIANCE);
        db.execSQL("DROP TABLE IF EXISTS " + TB_COMMAND);

        // create fresh books table
        this.onCreate(db);
    }

    public void insertUpdateDevice(Device device) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_DEVICE_HOST, device.getHost());
        values.put(KEY_DEVICE_PORT, device.getPort());
        values.put(KEY_DEVICE_DESCRIPTION, device.getDescription());

        if (device.getId() != 0) {
            values.put(KEY_DEVICE_ID, device.getId());
            db.update(TB_DEVICE, values, KEY_DEVICE_ID + " = ?", new String[]{String.valueOf(device.getId())});
        } else {
            db.insert(TB_DEVICE, null, values);
        }

        // 4. close
        db.close();
    }

    public void inserirAtualizarDispositivo(Appliance appliance) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_APPLIANCE_DESCRIPTION, appliance.getDescription());
        values.put(KEY_APPLIANCE_DEVICE_ID, appliance.getDevice().getId());

        if (appliance.getId() != 0) {
            values.put(KEY_APPLIANCE_DEVICE_ID, appliance.getId());

            db.update(TB_APPLIANCE, values, KEY_APPLIANCE_ID + " = ?", new String[]{String.valueOf(appliance.getId())});
        } else {
            db.insert(TB_APPLIANCE, null, values);
        }

        // 4. close
        db.close();
    }

    public void deletarDispositivo(long id) {
        String query = "DELETE FROM " + TB_APPLIANCE + " WHERE " + KEY_APPLIANCE_ID + " = " + id;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(query);
        //db.endTransaction();
        if (db != null) {
            db.close();
        }
    }

    public void deleteRoom(long idRoom) {
        String query = "DELETE FROM " + TB_DEVICE + " WHERE " + KEY_DEVICE_ID + " = " + idRoom;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(query);
        //db.endTransaction();
        if (db != null) {
            db.close();
        }
    }

    public void deleteCommand(long idAppliance, long idPosition) {
        String query = "DELETE FROM " + TB_COMMAND + " WHERE " + KEY_COMMAND_APPLIANCE_ID + " = " + idAppliance + " AND " + KEY_COMMAND_POSITION_ID + " = " + idPosition;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(query);
        //db.endTransaction();
        if (db != null) {
            db.close();
        }
    }

    public List<Appliance> listarDispositivos() throws ParseException {

        List<Appliance> appliances = new LinkedList<Appliance>();

        // 1. build the query
        //String query = "SELECT * FROM " + TB_SOLICITACAO + " WHERE cancelamento = 0";

        String query = "SELECT * FROM " + TB_APPLIANCE + " AS d LEFT OUTER JOIN " + TB_COMMAND + " AS c ON d." + KEY_APPLIANCE_ID + " = c." + KEY_COMMAND_APPLIANCE_ID;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        //db.endTransaction();

        // 3. go over each row, build book and add it to list
        Appliance appliance = null;
        if (cursor.moveToFirst()) {
            do {

                appliance = new Appliance();
                appliance.setId(Integer.parseInt(cursor.getString(0)));
                appliance.setDescription(cursor.getString(1));

                //dispositivo.setComandos(listarRastreiosPorSolicitacaoId(solicitaca  o.getId()));

                // Add book to books
                appliances.add(appliance);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }

        if (appliances.isEmpty()) {
            return null;
        } else {

            return appliances;

        }

    }

    public List<Appliance> listAppliancesByIdRoom(long idRoom) throws ParseException {

        List<Appliance> appliances = new LinkedList<Appliance>();

        // 1. build the query
        //String query = "SELECT * FROM " + TB_SOLICITACAO + " WHERE cancelamento = 0";

        String query = "SELECT * FROM " + TB_APPLIANCE + " AS d LEFT OUTER JOIN " + TB_COMMAND + " AS c ON d." + KEY_APPLIANCE_ID + " = c." + KEY_COMMAND_APPLIANCE_ID + " WHERE " + KEY_APPLIANCE_DEVICE_ID + " = " + idRoom;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        //db.endTransaction();

        // 3. go over each row, build book and add it to list
        Appliance appliance = null;
        if (cursor.moveToFirst()) {
            do {

                appliance = new Appliance();
                appliance.setId(Integer.parseInt(cursor.getString(0)));
                appliance.setDescription(cursor.getString(1));
                appliance.setDevice(getRoomById(cursor.getLong(2)));
                //dispositivo.setComandos(listarRastreiosPorSolicitacaoId(solicitaca  o.getId()));

                // Add book to books
                appliances.add(appliance);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }

        if (appliances.isEmpty()) {
            return null;
        } else {

            return appliances;

        }

    }

    public List<Device> listarAmbientes() {

        List<Device> ambientes = new LinkedList<Device>();

        // 1. build the query
        //String query = "SELECT * FROM " + TB_SOLICITACAO + " WHERE cancelamento = 0";

        String query = "SELECT * FROM " + TB_DEVICE;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        //db.endTransaction();

        // 3. go over each row, build book and add it to list
        Device ambiente = null;
        if (cursor.moveToFirst()) {
            do {

                ambiente = new Device();
                ambiente.setId(Integer.parseInt(cursor.getString(0)));
                ambiente.setHost(cursor.getString(1));
                ambiente.setPort(cursor.getInt(2));
                ambiente.setDescription(cursor.getString(3));

                //dispositivo.setComandos(listarRastreiosPorSolicitacaoId(solicitaca  o.getId()));

                // Add book to books
                ambientes.add(ambiente);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }

        if (ambientes.isEmpty()) {
            return null;
        } else {

            return ambientes;

        }

    }

    public Appliance getApplianceById(long id) throws ParseException {

        List<Appliance> appliances = new LinkedList<Appliance>();

        // 1. build the query
        //String query = "SELECT * FROM " + TB_SOLICITACAO + " WHERE cancelamento = 0";

        String query = "SELECT * FROM " + TB_APPLIANCE + " AS d INNER JOIN " + TB_DEVICE + " AS a ON d." + KEY_APPLIANCE_DEVICE_ID + " = a." + KEY_DEVICE_ID + " WHERE d." + KEY_APPLIANCE_ID + " = " + id;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        //db.endTransaction();

        // 3. go over each row, build book and add it to list
        Appliance appliance = null;
        if (cursor.moveToFirst()) {
            do {

                appliance = new Appliance();
                appliance.setId(Integer.parseInt(cursor.getString(0)));
                appliance.setDescription(cursor.getString(1));
                appliance.setDevice(getRoomById(cursor.getLong(2)));

                appliance.setCommands(listarComandosPorDispositivoId(Integer.parseInt(cursor.getString(0))) == null ? null : new HashSet<Comando>(listarComandosPorDispositivoId(cursor.getLong(2))));

                // Add book to books
                //dispositivos.add(dispositivo);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }

        return appliance;

    }

    public Comando getCommandById(long idAppliance, long idPosition) throws ParseException {

        //List<Comando> comando = new LinkedList<Comando>();

        // 1. build the query
        //String query = "SELECT * FROM " + TB_SOLICITACAO + " WHERE cancelamento = 0";

        String query = "SELECT * FROM " + TB_COMMAND + " WHERE " + KEY_COMMAND_APPLIANCE_ID + " = " + idAppliance + " AND " + KEY_COMMAND_POSITION_ID + " = " + idPosition;

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
                comando.setDescricao(cursor.getString(cursor.getColumnIndex(KEY_COMMAND_DESCRIPTION)));
                comando.setAppliance(getApplianceById(cursor.getLong(cursor.getColumnIndex(KEY_COMMAND_APPLIANCE_ID))));
                comando.setPosicao(Posicao.values()[(int) cursor.getLong(cursor.getColumnIndex(KEY_COMMAND_POSITION_ID))]);
                comando.setCodigo(cursor.getString(cursor.getColumnIndex(KEY_COMMAND_CODE)));

                //dispositivo.setComandos(listarComandosPorDispositivoId(Integer.parseInt(cursor.getString(0))) == null ? null : new HashSet<Comando>(listarComandosPorDispositivoId(cursor.getLong(2))));

                //dispositivos.add(dispositivo);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }

        return comando;

    }

    public Device getRoomById(Long id) throws ParseException {

        List<Appliance> appliances = new LinkedList<Appliance>();

        // 1. build the query
        //String query = "SELECT * FROM " + TB_SOLICITACAO + " WHERE cancelamento = 0";

        String query = "SELECT * FROM " + TB_DEVICE + " WHERE " + KEY_DEVICE_ID + " = " + id;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        //db.endTransaction();

        // 3. go over each row, build book and add it to list
        Device ambiente = null;
        if (cursor.moveToFirst()) {
            do {

                ambiente = new Device();
                ambiente.setId(Integer.parseInt(cursor.getString(0)));
                ambiente.setDescription(cursor.getString(1));

                // Add book to books
                //dispositivos.add(dispositivo);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }

        return ambiente;

    }

    public List<Comando> listarComandos() throws ParseException {

        List<Comando> comandos = new LinkedList<Comando>();

        // 1. build the query
        //String query = "SELECT * FROM " + TB_SOLICITACAO + " WHERE cancelamento = 0";

        String query = "SELECT * FROM " + TB_COMMAND + " AS c ";

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

                comando.setAppliance(getApplianceById(cursor.getLong(2)));

                // Add book to books
                comandos.add(comando);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }

        if (comandos.isEmpty()) {
            return null;
        } else {

            return comandos;

        }

    }

    public List<Comando> listarComandosPorDispositivoId(long id) throws ParseException {

        List<Comando> comandos = new LinkedList<Comando>();

        // 1. build the query
        //String query = "SELECT * FROM " + TB_SOLICITACAO + " WHERE cancelamento = 0";

        String query = "SELECT * FROM " + TB_COMMAND + " AS c WHERE c." + KEY_COMMAND_APPLIANCE_ID + " = " + id;

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

                comando.setAppliance(getApplianceById(cursor.getLong(2)));

                // Add book to books
                comandos.add(comando);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }

        if (comandos.isEmpty()) {
            return null;
        } else {

            return comandos;

        }

    }

    public void inserirAtualizarComando(Comando comando) {

        SQLiteDatabase db;

        ContentValues values = new ContentValues();

        values.put(KEY_COMMAND_DESCRIPTION, comando.getDescricao());
        values.put(KEY_COMMAND_CODE, comando.getCodigo());
        values.put(KEY_COMMAND_APPLIANCE_ID, comando.getAppliance().getId());
        values.put(KEY_COMMAND_POSITION_ID, comando.getPosicao().ordinal());


        try {
            Comando cmdDb = getCommandById(comando.getAppliance().getId(), comando.getPosicao().ordinal());

            db = this.getWritableDatabase();

            if (cmdDb != null) {
                db.update(TB_COMMAND, values, KEY_COMMAND_APPLIANCE_ID + " = ? AND " + KEY_COMMAND_POSITION_ID + " =?", new String[]{String.valueOf(comando.getAppliance().getId()), String.valueOf(comando.getPosicao().ordinal())});
            } else {
                db.insertOrThrow(TB_COMMAND, null, values);

            }

            // 4. close
            db.close();

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}