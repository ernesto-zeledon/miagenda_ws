package net.lrivas.miagenda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import net.lrivas.miagenda.clases.ConexionSQLite;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ConexionSQLite objConexion;
    final String NOMBRE_BASE_DATOS = "miagenda";
    Button botonAgregar;
    ListView listaContactos;
    ArrayList<String> lista;
    ArrayAdapter adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        objConexion = new ConexionSQLite(MainActivity.this, NOMBRE_BASE_DATOS, null, 1);
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
        String consulta = "SELECT id_contacto, nombre, telefono FROM contactos ORDER BY nombre ASC";
        Cursor cadaRegistro = base.rawQuery(consulta, null);
        if(cadaRegistro.moveToFirst()) {
            do {
                miLista.add(cadaRegistro.getString(1).toString());
            } while (cadaRegistro.moveToNext());
        }
        return miLista;
    }
}