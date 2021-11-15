package net.lrivas.miagenda_ws;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.lrivas.miagenda_ws.clases.Configuraciones;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button botonAgregar, botonBuscar;
    ListView listaContactos;
    ArrayAdapter adaptador;
    EditText txtCriterio;
    Configuraciones objConfiguracion = new Configuraciones();
    String URL = objConfiguracion.urlWebServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        listaContactos.setAdapter(adaptador);

        botonBuscar = (Button) findViewById(R.id.btnBuscar);
        botonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llenarLista();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            llenarLista();
        } catch (Exception error) {
            Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void llenarLista() {

        final String criterio = txtCriterio.getText().toString();
        RequestQueue objetoPeticion = Volley.newRequestQueue(MainActivity.this);
        StringRequest peticion = new StringRequest(Request.Method.POST, this.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objJSONResultado = new JSONObject(response.toString());
                    JSONArray aDatosResultado = objJSONResultado.getJSONArray("resultado");

                    AdaptadorListaContactos miAdaptador = new AdaptadorListaContactos();
                    miAdaptador.arregloDatos = aDatosResultado;
                    listaContactos.setAdapter(miAdaptador);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accion", "listar_contactos");
                params.put("filtro", criterio);
                return params;
            }
        };

        objetoPeticion.add(peticion);
    }

    class AdaptadorListaContactos extends BaseAdapter {
        public JSONArray arregloDatos;

        @Override
        public int getCount() {
            return arregloDatos.length();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.fila_contactos, null);
            TextView txtTitulo = convertView.findViewById(R.id.tvTituloFilaContacto);
            TextView txtTelefono = convertView.findViewById(R.id.tvTelefonoFilaContacto);
            Button btnVer = convertView.findViewById(R.id.btnVerContacto);

            JSONObject objJSON = null;

            try {
                objJSON = arregloDatos.getJSONObject(position);
                final String id_contacto, nombre, telefono;
                id_contacto = objJSON.getString("id_contacto");
                nombre = objJSON.getString("nombre");
                telefono = objJSON.getString("telefono");

                txtTitulo.setText(nombre);
                txtTelefono.setText(telefono);
                btnVer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent ventanaModificar = new Intent(MainActivity.this, ModificarContacto.class);
                        ventanaModificar.putExtra("id_contacto", id_contacto);
                        ventanaModificar.putExtra("nombre", nombre);
                        ventanaModificar.putExtra("telefono", telefono);
                        startActivity(ventanaModificar);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }
}