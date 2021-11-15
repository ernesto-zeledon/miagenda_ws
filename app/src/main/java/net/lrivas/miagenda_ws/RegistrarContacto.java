package net.lrivas.miagenda_ws;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegistrarContacto extends AppCompatActivity {

    final String NOMBRE_BASE_DATOS = "miagenda";
    EditText nombre, telefono;
    Button botonAgregar, botonRegresar;
    Configuraciones objConfiguracion = new Configuraciones();
    String URL = objConfiguracion.urlWebServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_contacto);
        nombre = (EditText) findViewById(R.id.txtNombreCompleto);
        telefono = (EditText) findViewById(R.id.txtTelefono);
        botonAgregar = (Button) findViewById(R.id.btnGuardarContacto);
        botonRegresar = (Button) findViewById(R.id.btnRegresar);

        botonRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               regresar();
            }
        });

        botonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrar();
            }
        });
    }

    private void registrar() {
        try {
            RequestQueue objetoPeticion = Volley.newRequestQueue(RegistrarContacto.this);
            StringRequest peticion = new StringRequest(Request.Method.POST, this.URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject objJSONResultado = new JSONObject(response.toString());
                        String estado = objJSONResultado.getString("estado");

                        if (estado.equals("1")) {
                            Toast.makeText(RegistrarContacto.this, "Contacto registrado con exito", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegistrarContacto.this, "Error: " + estado, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(RegistrarContacto.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("accion", "registrar");
                    params.put("nombre", nombre.getText().toString());
                    params.put("telefono", telefono.getText().toString());
                    return params;
                }
            };

            objetoPeticion.add(peticion);

        } catch (Exception error) {
            Toast.makeText(RegistrarContacto.this, "Error en tiempo de ejecución: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void regresar() {
        Intent actividad = new Intent(RegistrarContacto.this, MainActivity.class);
        startActivity(actividad);
        RegistrarContacto.this.finish();
    }
}