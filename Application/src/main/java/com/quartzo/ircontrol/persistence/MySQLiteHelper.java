package com.quartzo.ircontrol.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
                + " code TEXT, "
                + " position_id INTEGER, "
                + " appliance_id INTEGER, "
                + " PRIMARY KEY (position_id, appliance_id),"
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

    public void insertUpdateAppliance(Appliance appliance) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_APPLIANCE_DESCRIPTION, appliance.getDescription());

        if (appliance.getId() != 0) {
            db.update(TB_APPLIANCE, values, KEY_APPLIANCE_ID + " = ?", new String[]{String.valueOf(appliance.getId())});
        } else {
            values.put(KEY_APPLIANCE_DEVICE_ID, appliance.getDevice().getId());
            db.insert(TB_APPLIANCE, null, values);
        }

        // 4. close
        db.close();
    }

    public void deleteAppliance(long id) {
        String query = "DELETE FROM " + TB_APPLIANCE + " WHERE " + KEY_APPLIANCE_ID + " = " + id;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(query);
        //db.endTransaction();
        if (db != null) {
            db.close();
        }
    }

    public void deleteDevice(long idDevice) {
        String query = "DELETE FROM " + TB_DEVICE + " WHERE " + KEY_DEVICE_ID + " = " + idDevice;

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


    public List<Appliance> listAppliancesByDeviceId(long idDevice) throws ParseException {

        List<Appliance> appliances = new LinkedList<Appliance>();

        // 1. build the query
        //String query = "SELECT * FROM " + TB_SOLICITACAO + " WHERE cancelamento = 0";

        String query = "SELECT * FROM " + TB_APPLIANCE + " AS a LEFT OUTER JOIN " + TB_DEVICE + " AS d ON a." + KEY_APPLIANCE_DEVICE_ID + " = d." + KEY_DEVICE_ID + " WHERE d." + KEY_DEVICE_ID + " = " + idDevice;

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
                appliance.setDevice(getDeviceById(cursor.getLong(2)));
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

    public List<Device> listDevice() {

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

    public Appliance getApplianceById(long idAppliance) {

        List<Appliance> appliances = new LinkedList<Appliance>();

        String query = "SELECT * FROM " + TB_APPLIANCE + " WHERE " + KEY_APPLIANCE_ID + " = " + idAppliance;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Appliance appliance = null;
        if (cursor.moveToFirst()) {
            do {

                appliance = new Appliance();
                appliance.setId(cursor.getInt(cursor.getColumnIndex(KEY_APPLIANCE_ID)));
                appliance.setDescription(cursor.getString(cursor.getColumnIndex(KEY_APPLIANCE_DESCRIPTION)));
                appliance.setDevice(getDeviceById(cursor.getLong(cursor.getColumnIndex(KEY_APPLIANCE_DEVICE_ID))));

                //appliance.setCommands(listarComandosPorDispositivoId(Integer.parseInt(cursor.getString(0))) == null ? null : new HashSet<Command>(listarComandosPorDispositivoId(cursor.getLong(2))));

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

    public Command getCommandById(long idAppliance, long idPosition) {

        String query = "SELECT * FROM " + TB_COMMAND + " AS c WHERE " + KEY_COMMAND_APPLIANCE_ID + " = " + idAppliance + " AND " + KEY_COMMAND_POSITION_ID + " = " + idPosition;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Command command = null;
        if (cursor.moveToFirst()) {
            do {

                command = new Command();
                //comando.setId(Integer.parseInt(cursor.getString(0)));
                command.setDescription(cursor.getString(cursor.getColumnIndex(KEY_COMMAND_DESCRIPTION)));
                command.setCode(cursor.getString(cursor.getColumnIndex(KEY_COMMAND_CODE)));
                command.setPosition(Position.values()[(int) cursor.getLong(cursor.getColumnIndex(KEY_COMMAND_POSITION_ID))]);
                command.setAppliance(getApplianceById(idAppliance));

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

        return command;

    }

    public Map listCommandByApplianceId(long idAppliance) {

        String query = "SELECT * FROM " + TB_COMMAND + " AS c WHERE " + KEY_COMMAND_APPLIANCE_ID + " = " + idAppliance;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Map<Position, Command> commands = new HashMap<>();
        if (cursor.moveToFirst()) {

            Command command = null;
            do {

                command = new Command();

                command.setDescription(cursor.getString(cursor.getColumnIndex(KEY_COMMAND_DESCRIPTION)));
                command.setCode(cursor.getString(cursor.getColumnIndex(KEY_COMMAND_CODE)));
                command.setPosition(Position.values()[(int) cursor.getLong(cursor.getColumnIndex(KEY_COMMAND_POSITION_ID))]);
                command.setAppliance(getApplianceById(idAppliance));

                commands.put(command.getPosition(), command);

            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }

        return commands;
    }

    public Device getDeviceById(Long idDevice) {

        String query = "SELECT * FROM " + TB_DEVICE + " WHERE " + KEY_DEVICE_ID + " = " + idDevice;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Device device = null;
        if (cursor.moveToFirst()) {
            do {

                device = new Device();
                device.setId(cursor.getInt(cursor.getColumnIndex(KEY_DEVICE_ID)));
                device.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DEVICE_DESCRIPTION)));
                device.setHost(cursor.getString(cursor.getColumnIndex(KEY_DEVICE_HOST)));
                device.setPort(cursor.getInt(cursor.getColumnIndex(KEY_DEVICE_PORT)));

            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }

        return device;

    }

    public void insertUpdateCommand(Command command) {

        SQLiteDatabase db;

        ContentValues values = new ContentValues();

        values.put(KEY_COMMAND_DESCRIPTION, command.getDescription());
        values.put(KEY_COMMAND_CODE, command.getCode());
        values.put(KEY_COMMAND_APPLIANCE_ID, command.getAppliance().getId());
        values.put(KEY_COMMAND_POSITION_ID, command.getPosition().ordinal());

        Command cmdDb = getCommandById(command.getAppliance().getId(), command.getPosition().ordinal());

        db = this.getWritableDatabase();

        if (cmdDb != null) {
            db.update(TB_COMMAND, values, KEY_COMMAND_APPLIANCE_ID + " = ? AND " + KEY_COMMAND_POSITION_ID + " =?", new String[]{String.valueOf(command.getAppliance().getId()), String.valueOf(command.getPosition().ordinal())});
        } else {
            db.insertOrThrow(TB_COMMAND, null, values);

        }

        // 4. close
        db.close();

    }
}