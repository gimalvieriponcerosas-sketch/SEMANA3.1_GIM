package com.example.semana31_gim;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AdminSQLiteData bd;
    private ViewFlipper viewFlipper;

    // Empleados
    private ListView listViewEmpleados;
    private ArrayAdapter<String> adapterEmpleados;
    private List<String> textosEmpleados;
    private List<Integer> idsEmpleados;

    // Trámites
    private ListView listViewTramites;
    private ArrayAdapter<String> adapterTramites;
    private List<String> textosTramites;
    private List<Integer> idsTramites;

    // Buscar
    private ListView listViewBuscar;
    private ArrayAdapter<String> adapterBuscar;
    private List<String> textosBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bd = new AdminSQLiteData(this);
        viewFlipper = findViewById(R.id.viewFlipper);

        // ---------- MENÚ ----------
        findViewById(R.id.btnEmpleados).setOnClickListener(v -> {
            viewFlipper.setDisplayedChild(1);
            cargarEmpleados();
        });
        findViewById(R.id.btnTramites).setOnClickListener(v -> {
            viewFlipper.setDisplayedChild(2);
            cargarTramites();
        });
        findViewById(R.id.btnBuscarEmpleado).setOnClickListener(v -> {
            viewFlipper.setDisplayedChild(3);
            buscarEmpleados("");
        });

        // ---------- EMPLEADOS ----------
        listViewEmpleados = findViewById(R.id.listViewEmpleados);
        textosEmpleados = new ArrayList<>();
        idsEmpleados = new ArrayList<>();
        adapterEmpleados = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, textosEmpleados);
        listViewEmpleados.setAdapter(adapterEmpleados);

        findViewById(R.id.btnVolverEmpleados).setOnClickListener(v -> viewFlipper.setDisplayedChild(0));
        findViewById(R.id.btnAgregarEmpleado).setOnClickListener(v -> mostrarDialogoAgregarEmpleado());

        listViewEmpleados.setOnItemClickListener((parent, view, position, id) ->
                mostrarDialogoEditarEmpleado(position));

        listViewEmpleados.setOnItemLongClickListener((parent, view, position, id) -> {
            confirmarEliminarEmpleado(idsEmpleados.get(position), textosEmpleados.get(position));
            return true;
        });

        // ---------- TRÁMITES ----------
        listViewTramites = findViewById(R.id.listViewTramites);
        textosTramites = new ArrayList<>();
        idsTramites = new ArrayList<>();
        adapterTramites = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, textosTramites);
        listViewTramites.setAdapter(adapterTramites);

        findViewById(R.id.btnVolverTramites).setOnClickListener(v -> viewFlipper.setDisplayedChild(0));
        findViewById(R.id.btnAgregarTramite).setOnClickListener(v -> mostrarDialogoAgregarTramite());

        listViewTramites.setOnItemClickListener((parent, view, position, id) ->
                mostrarDialogoEditarTramite(position));

        listViewTramites.setOnItemLongClickListener((parent, view, position, id) -> {
            confirmarEliminarTramite(idsTramites.get(position), textosTramites.get(position));
            return true;
        });

        // ---------- BUSCAR ----------
        listViewBuscar = findViewById(R.id.listViewBuscar);
        textosBuscar = new ArrayList<>();
        adapterBuscar = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, textosBuscar);
        listViewBuscar.setAdapter(adapterBuscar);

        findViewById(R.id.btnVolverBuscar).setOnClickListener(v -> viewFlipper.setDisplayedChild(0));

        EditText etBuscar = findViewById(R.id.etBuscar);
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                buscarEmpleados(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onBackPressed() {
        if (viewFlipper.getDisplayedChild() != 0) {
            viewFlipper.setDisplayedChild(0);
        } else {
            super.onBackPressed();
        }
    }

    // ================= EMPLEADOS =================

    private void cargarEmpleados() {
        textosEmpleados.clear();
        idsEmpleados.clear();

        Cursor cursor = bd.obtenerEmpleados();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(AdminSQLiteData.E_ID));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLiteData.E_NOMBRE));
                String apellido = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLiteData.E_APELLIDO));
                String dni = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLiteData.E_DNI));
                String cargo = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLiteData.E_CARGO));

                idsEmpleados.add(id);
                textosEmpleados.add(nombre + " " + apellido + "  |  DNI: " + dni + "  |  " + cargo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapterEmpleados.notifyDataSetChanged();
    }

    private void mostrarDialogoAgregarEmpleado() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, pad);

        EditText etNombre = new EditText(this);
        etNombre.setHint("Nombre");
        EditText etApellido = new EditText(this);
        etApellido.setHint("Apellido");
        EditText etDni = new EditText(this);
        etDni.setHint("DNI");
        EditText etCargo = new EditText(this);
        etCargo.setHint("Cargo");

        layout.addView(etNombre);
        layout.addView(etApellido);
        layout.addView(etDni);
        layout.addView(etCargo);

        new AlertDialog.Builder(this)
                .setTitle("Nuevo empleado")
                .setView(layout)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    String apellido = etApellido.getText().toString().trim();
                    String dni = etDni.getText().toString().trim();
                    String cargo = etCargo.getText().toString().trim();

                    if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || cargo.isEmpty()) {
                        Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    long resultado = bd.insertarEmpleado(nombre, apellido, dni, cargo);
                    if (resultado != -1) {
                        Toast.makeText(this, "Empleado agregado", Toast.LENGTH_SHORT).show();
                        cargarEmpleados();
                    } else {
                        Toast.makeText(this, "Error al agregar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoEditarEmpleado(int position) {
        int id = idsEmpleados.get(position);

        Cursor cursor = bd.obtenerEmpleadoPorId(id);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return;
        }

        String nombreActual = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLiteData.E_NOMBRE));
        String apellidoActual = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLiteData.E_APELLIDO));
        String dniActual = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLiteData.E_DNI));
        String cargoActual = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLiteData.E_CARGO));
        cursor.close();

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, pad);

        EditText etNombre = new EditText(this);
        etNombre.setHint("Nombre");
        etNombre.setText(nombreActual);

        EditText etApellido = new EditText(this);
        etApellido.setHint("Apellido");
        etApellido.setText(apellidoActual);

        EditText etDni = new EditText(this);
        etDni.setHint("DNI");
        etDni.setText(dniActual);

        EditText etCargo = new EditText(this);
        etCargo.setHint("Cargo");
        etCargo.setText(cargoActual);

        layout.addView(etNombre);
        layout.addView(etApellido);
        layout.addView(etDni);
        layout.addView(etCargo);

        new AlertDialog.Builder(this)
                .setTitle("Editar empleado")
                .setView(layout)
                .setPositiveButton("Guardar cambios", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    String apellido = etApellido.getText().toString().trim();
                    String dni = etDni.getText().toString().trim();
                    String cargo = etCargo.getText().toString().trim();

                    if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || cargo.isEmpty()) {
                        Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int filas = bd.actualizarEmpleado(id, nombre, apellido, dni, cargo);
                    if (filas > 0) {
                        Toast.makeText(this, "Empleado actualizado", Toast.LENGTH_SHORT).show();
                        cargarEmpleados();
                    } else {
                        Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarEliminarEmpleado(int id, String texto) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar empleado")
                .setMessage("¿Eliminar a " + texto + "?\n\nNota: si tiene trámites asociados, no se podrá eliminar.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    try {
                        bd.eliminarEmpleado(id);
                        cargarEmpleados();
                    } catch (Exception e) {
                        Toast.makeText(this, "No se puede eliminar: tiene trámites asociados", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ================= TRÁMITES =================

    private void cargarTramites() {
        textosTramites.clear();
        idsTramites.clear();

        Cursor cursor = bd.obtenerTramites();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(AdminSQLiteData.T_ID));
                String nombreTramite = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLiteData.T_NOMBRE));
                String estado = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLiteData.T_ESTADO));
                String nombreEmpleado = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLiteData.E_NOMBRE))
                        + " " + cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLiteData.E_APELLIDO));

                idsTramites.add(id);
                textosTramites.add(nombreTramite + "  |  " + estado + "  |  " + nombreEmpleado);
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapterTramites.notifyDataSetChanged();
    }

    private void mostrarDialogoAgregarTramite() {

        List<Integer> idsEmpleadosSpinner = new ArrayList<>();
        List<String> nombresEmpleadosSpinner = new ArrayList<>();

        Cursor cursorEmpleados = bd.obtenerEmpleados();
        if (cursorEmpleados.moveToFirst()) {
            do {
                int id = cursorEmpleados.getInt(cursorEmpleados.getColumnIndexOrThrow(AdminSQLiteData.E_ID));
                String nombre = cursorEmpleados.getString(cursorEmpleados.getColumnIndexOrThrow(AdminSQLiteData.E_NOMBRE));
                String apellido = cursorEmpleados.getString(cursorEmpleados.getColumnIndexOrThrow(AdminSQLiteData.E_APELLIDO));
                idsEmpleadosSpinner.add(id);
                nombresEmpleadosSpinner.add(nombre + " " + apellido);
            } while (cursorEmpleados.moveToNext());
        }
        cursorEmpleados.close();

        if (idsEmpleadosSpinner.isEmpty()) {
            Toast.makeText(this, "Primero registra al menos un empleado", Toast.LENGTH_SHORT).show();
            return;
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, pad);

        EditText etNombreTramite = new EditText(this);
        etNombreTramite.setHint("Nombre del trámite");
        EditText etDescripcion = new EditText(this);
        etDescripcion.setHint("Descripción");

        TextView tvEstado = new TextView(this);
        tvEstado.setText("Estado");
        Spinner spinnerEstado = new Spinner(this);
        ArrayAdapter<String> estadoAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Pendiente", "En proceso", "Completado"});
        spinnerEstado.setAdapter(estadoAdapter);

        TextView tvEmpleado = new TextView(this);
        tvEmpleado.setText("Empleado encargado");
        Spinner spinnerEmpleado = new Spinner(this);
        ArrayAdapter<String> empleadoAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, nombresEmpleadosSpinner);
        spinnerEmpleado.setAdapter(empleadoAdapter);

        layout.addView(etNombreTramite);
        layout.addView(etDescripcion);
        layout.addView(tvEstado);
        layout.addView(spinnerEstado);
        layout.addView(tvEmpleado);
        layout.addView(spinnerEmpleado);

        new AlertDialog.Builder(this)
                .setTitle("Nuevo trámite")
                .setView(layout)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombreTramite = etNombreTramite.getText().toString().trim();
                    String descripcion = etDescripcion.getText().toString().trim();
                    String estado = spinnerEstado.getSelectedItem().toString();
                    int idEmpleado = idsEmpleadosSpinner.get(spinnerEmpleado.getSelectedItemPosition());

                    if (nombreTramite.isEmpty()) {
                        Toast.makeText(this, "Ingresa el nombre del trámite", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    long resultado = bd.insertarTramite(nombreTramite, descripcion, estado, idEmpleado);
                    if (resultado != -1) {
                        Toast.makeText(this, "Trámite agregado", Toast.LENGTH_SHORT).show();
                        cargarTramites();
                    } else {
                        Toast.makeText(this, "Error al agregar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoEditarTramite(int position) {
        int id = idsTramites.get(position);

        Cursor cursorTramite = bd.obtenerTramitePorId(id);
        if (!cursorTramite.moveToFirst()) {
            cursorTramite.close();
            return;
        }

        String nombreActual = cursorTramite.getString(cursorTramite.getColumnIndexOrThrow(AdminSQLiteData.T_NOMBRE));
        String descripcionActual = cursorTramite.getString(cursorTramite.getColumnIndexOrThrow(AdminSQLiteData.T_DESCRIPCION));
        String estadoActual = cursorTramite.getString(cursorTramite.getColumnIndexOrThrow(AdminSQLiteData.T_ESTADO));
        int idEmpleadoActual = cursorTramite.getInt(cursorTramite.getColumnIndexOrThrow(AdminSQLiteData.T_EMPLEADO));
        cursorTramite.close();

        List<Integer> idsEmpleadosSpinner = new ArrayList<>();
        List<String> nombresEmpleadosSpinner = new ArrayList<>();
        int posicionEmpleadoActual = 0;

        Cursor cursorEmpleados = bd.obtenerEmpleados();
        if (cursorEmpleados.moveToFirst()) {
            int i = 0;
            do {
                int idEmp = cursorEmpleados.getInt(cursorEmpleados.getColumnIndexOrThrow(AdminSQLiteData.E_ID));
                String nombre = cursorEmpleados.getString(cursorEmpleados.getColumnIndexOrThrow(AdminSQLiteData.E_NOMBRE));
                String apellido = cursorEmpleados.getString(cursorEmpleados.getColumnIndexOrThrow(AdminSQLiteData.E_APELLIDO));
                idsEmpleadosSpinner.add(idEmp);
                nombresEmpleadosSpinner.add(nombre + " " + apellido);
                if (idEmp == idEmpleadoActual) posicionEmpleadoActual = i;
                i++;
            } while (cursorEmpleados.moveToNext());
        }
        cursorEmpleados.close();

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, pad);

        EditText etNombreTramite = new EditText(this);
        etNombreTramite.setHint("Nombre del trámite");
        etNombreTramite.setText(nombreActual);

        EditText etDescripcion = new EditText(this);
        etDescripcion.setHint("Descripción");
        etDescripcion.setText(descripcionActual);

        TextView tvEstado = new TextView(this);
        tvEstado.setText("Estado");
        Spinner spinnerEstado = new Spinner(this);
        String[] estados = new String[]{"Pendiente", "En proceso", "Completado"};
        ArrayAdapter<String> estadoAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, estados);
        spinnerEstado.setAdapter(estadoAdapter);
        for (int i = 0; i < estados.length; i++) {
            if (estados[i].equals(estadoActual)) {
                spinnerEstado.setSelection(i);
                break;
            }
        }

        TextView tvEmpleado = new TextView(this);
        tvEmpleado.setText("Empleado encargado");
        Spinner spinnerEmpleado = new Spinner(this);
        ArrayAdapter<String> empleadoAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, nombresEmpleadosSpinner);
        spinnerEmpleado.setAdapter(empleadoAdapter);
        spinnerEmpleado.setSelection(posicionEmpleadoActual);

        layout.addView(etNombreTramite);
        layout.addView(etDescripcion);
        layout.addView(tvEstado);
        layout.addView(spinnerEstado);
        layout.addView(tvEmpleado);
        layout.addView(spinnerEmpleado);

        new AlertDialog.Builder(this)
                .setTitle("Editar trámite")
                .setView(layout)
                .setPositiveButton("Guardar cambios", (dialog, which) -> {
                    String nombreTramite = etNombreTramite.getText().toString().trim();
                    String descripcion = etDescripcion.getText().toString().trim();
                    String estado = spinnerEstado.getSelectedItem().toString();
                    int idEmpleado = idsEmpleadosSpinner.get(spinnerEmpleado.getSelectedItemPosition());

                    if (nombreTramite.isEmpty()) {
                        Toast.makeText(this, "Ingresa el nombre del trámite", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int filas = bd.actualizarTramite(id, nombreTramite, descripcion, estado, idEmpleado);
                    if (filas > 0) {
                        Toast.makeText(this, "Trámite actualizado", Toast.LENGTH_SHORT).show();
                        cargarTramites();
                    } else {
                        Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarEliminarTramite(int id, String texto) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar trámite")
                .setMessage("¿Eliminar \"" + texto + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    bd.eliminarTramite(id);
                    cargarTramites();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ================= BUSCAR =================

    private void buscarEmpleados(String texto) {
        textosBuscar.clear();

        Cursor cursor = texto.isEmpty() ? bd.obtenerEmpleados() : bd.buscarEmpleados(texto);
        if (cursor.moveToFirst()) {
            do {
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLiteData.E_NOMBRE));
                String apellido = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLiteData.E_APELLIDO));
                String dni = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLiteData.E_DNI));
                String cargo = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLiteData.E_CARGO));

                textosBuscar.add(nombre + " " + apellido + "  |  DNI: " + dni + "  |  " + cargo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapterBuscar.notifyDataSetChanged();
    }
}