package net.lrivas.miagenda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import net.lrivas.miagenda.clases.ConexionSQLite;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ConexionSQLite objConexion;
    final String NOMBRE_BASE_DATOS = "miagenda";
    Button botonAgregar, botonBuscar;
    ListView listaContactos;
    ArrayList<String> lista;
    ArrayAdapter adaptador;
    EditText txtCriterio;
    List<Integer> arregloID = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        objConexion = new ConexionSQLite(MainActivity.this, NOMBRE_BASE_DATOS, null, 1);
        txtCriterio = findViewById(R.id.txtCriterio);
        botonAgregar = (Button) findViewById(R.id.btnAgregar);
        botonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ventana = new Intent(MainActivity.this, RegistrarContacto.class);
                startActivity(ventana);
            }
        });

        listaContactos = (ListView) findViewById(R.id.lvContactos);
        lista = llenarLista();
        adaptador = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, lista);
        listaContactos.setAdapter(adaptador);

        listaContactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int idSeleccionado = arregloID.get(position);
                // Capturar y Enviar Valores a la Actividad RegistrarContacto
                Intent ventanaModificar = new Intent(MainActivity.this, ModificarContacto.class);
                ventanaModificar.putExtra("id_contacto", idSeleccionado);
                startActivity(ventanaModificar);
                // Fin
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            lista.clear();
            lista = llenarLista();
            adaptador = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, lista);
            listaContactos.setAdapter(adaptador);
        } catch (Exception error) {
            Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList llenarLista() {
        ArrayList<String> miLista = new ArrayList<>();
        SQLiteDatabase base = objConexion.getReadableDatabase();
        String criterio = txtCriterio.getText().toString();
        String consulta = "SELECT id_contacto, nombre, telefono FROM contactos " +
                "WHERE nombre LIKE '%" + criterio + "%' " +
                "ORDER BY nombre ASC";

        Cursor cadaRegistro = base.rawQuery(consulta, null);

        arregloID.clear();
        if(cadaRegistro.moveToFirst()) {
            do {
                miLista.add(cadaRegistro.getString(1).toString() + " - " + cadaRegistro.getString(2).toString());
                arregloID.add(cadaRegistro.getInt(0));
            } while (cadaRegistro.moveToNext());
        }
        return miLista;
    }
}