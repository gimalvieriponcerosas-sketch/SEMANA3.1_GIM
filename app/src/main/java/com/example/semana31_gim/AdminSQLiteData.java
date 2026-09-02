package com.example.semana31_gim;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteData extends SQLiteOpenHelper {

    private static final String NOMBRE_BD = "municipalidad_chinchao.db";
    private static final int VERSION_BD = 3;

    public static final String TABLA_EMPLEADOS = "empleados";

    public static final String E_ID = "id_empleado";
    public static final String E_NOMBRE = "nombre";
    public static final String E_APELLIDO = "apellido";
    public static final String E_DNI = "dni";
    public static final String E_CARGO = "cargo";

    public static final String TABLA_TRAMITES = "tramites";

    public static final String T_ID = "id_tramite";
    public static final String T_NOMBRE = "nombre_tramite";
    public static final String T_DESCRIPCION = "descripcion";
    public static final String T_ESTADO = "estado";
    public static final String T_EMPLEADO = "empleado";

    public AdminSQLiteData(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String empleados =
                "CREATE TABLE " + TABLA_EMPLEADOS + " (" +
                        E_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        E_NOMBRE + " TEXT NOT NULL, " +
                        E_APELLIDO + " TEXT NOT NULL, " +
                        E_DNI + " TEXT NOT NULL, " +
                        E_CARGO + " TEXT NOT NULL)";

        String tramites =
                "CREATE TABLE " + TABLA_TRAMITES + " (" +
                        T_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        T_NOMBRE + " TEXT NOT NULL, " +
                        T_DESCRIPCION + " TEXT, " +
                        T_ESTADO + " TEXT NOT NULL, " +
                        T_EMPLEADO + " INTEGER NOT NULL, " +
                        "FOREIGN KEY (" + T_EMPLEADO + ") REFERENCES " +
                        TABLA_EMPLEADOS + "(" + E_ID + "))";

        db.execSQL(empleados);
        db.execSQL(tramites);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLA_TRAMITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_EMPLEADOS);

        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    public long insertarEmpleado(
            String nombre,
            String apellido,
            String dni,
            String cargo) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put(E_NOMBRE, nombre);
        valores.put(E_APELLIDO, apellido);
        valores.put(E_DNI, dni);
        valores.put(E_CARGO, cargo);

        return db.insert(TABLA_EMPLEADOS, null, valores);
    }

    public Cursor obtenerEmpleados() {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLA_EMPLEADOS +
                        " ORDER BY " + E_ID + " DESC",
                null
        );
    }

    public Cursor buscarEmpleados(String texto) {

        SQLiteDatabase db = getReadableDatabase();

        String busqueda = "%" + texto + "%";

        return db.rawQuery(
                "SELECT * FROM " + TABLA_EMPLEADOS +
                        " WHERE " + E_NOMBRE + " LIKE ?" +
                        " OR " + E_APELLIDO + " LIKE ?" +
                        " OR " + E_DNI + " LIKE ?" +
                        " OR (" + E_NOMBRE +
                        " || ' ' || " + E_APELLIDO +
                        ") LIKE ?" +
                        " ORDER BY " + E_NOMBRE + " ASC",
                new String[]{
                        busqueda,
                        busqueda,
                        busqueda,
                        busqueda
                }
        );
    }

    public Cursor obtenerEmpleadoPorId(int id) {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLA_EMPLEADOS +
                        " WHERE " + E_ID + "=?",
                new String[]{
                        String.valueOf(id)
                }
        );
    }

    public int actualizarEmpleado(
            int id,
            String nombre,
            String apellido,
            String dni,
            String cargo) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put(E_NOMBRE, nombre);
        valores.put(E_APELLIDO, apellido);
        valores.put(E_DNI, dni);
        valores.put(E_CARGO, cargo);

        return db.update(
                TABLA_EMPLEADOS,
                valores,
                E_ID + "=?",
                new String[]{
                        String.valueOf(id)
                }
        );
    }

    public int eliminarEmpleado(int id) {

        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLA_EMPLEADOS,
                E_ID + "=?",
                new String[]{
                        String.valueOf(id)
                }
        );
    }

    public long insertarTramite(
            String nombre,
            String descripcion,
            String estado,
            int empleado) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put(T_NOMBRE, nombre);
        valores.put(T_DESCRIPCION, descripcion);
        valores.put(T_ESTADO, estado);
        valores.put(T_EMPLEADO, empleado);

        return db.insert(
                TABLA_TRAMITES,
                null,
                valores
        );
    }

    public Cursor obtenerTramites() {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT t." + T_ID + ", " +
                        "t." + T_NOMBRE + ", " +
                        "t." + T_DESCRIPCION + ", " +
                        "t." + T_ESTADO + ", " +
                        "e." + E_NOMBRE + ", " +
                        "e." + E_APELLIDO +
                        " FROM " + TABLA_TRAMITES + " t" +
                        " INNER JOIN " + TABLA_EMPLEADOS + " e" +
                        " ON t." + T_EMPLEADO +
                        " = e." + E_ID +
                        " ORDER BY t." + T_ID + " DESC",
                null
        );
    }

    public Cursor obtenerTramitesPorEmpleado(int idEmpleado) {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLA_TRAMITES +
                        " WHERE " + T_EMPLEADO + "=?" +
                        " ORDER BY " + T_ID + " DESC",
                new String[]{
                        String.valueOf(idEmpleado)
                }
        );
    }

    public Cursor obtenerTramitePorId(int id) {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLA_TRAMITES +
                        " WHERE " + T_ID + "=?",
                new String[]{
                        String.valueOf(id)
                }
        );
    }

    public int actualizarTramite(
            int id,
            String nombre,
            String descripcion,
            String estado,
            int empleado) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put(T_NOMBRE, nombre);
        valores.put(T_DESCRIPCION, descripcion);
        valores.put(T_ESTADO, estado);
        valores.put(T_EMPLEADO, empleado);

        return db.update(
                TABLA_TRAMITES,
                valores,
                T_ID + "=?",
                new String[]{
                        String.valueOf(id)
                }
        );
    }

    public int eliminarTramite(int id) {

        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLA_TRAMITES,
                T_ID + "=?",
                new String[]{
                        String.valueOf(id)
                }
        );
    }

    public int contarTramitesEmpleado(int idEmpleado) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " +
                        TABLA_TRAMITES +
                        " WHERE " + T_EMPLEADO + "=?",
                new String[]{
                        String.valueOf(idEmpleado)
                }
        );

        int cantidad = 0;

        if (cursor.moveToFirst()) {
            cantidad = cursor.getInt(0);
        }

        cursor.close();

        return cantidad;
    }
}