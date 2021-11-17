package net.lrivas.miagenda_ws;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ModificarContacto extends AppCompatActivity {

    EditText nombre, telefono;
    Button botonAgregar, botonRegresar, botonEliminar, botonLlamar;
    String id_contacto, nombre_contacto, telefono_contacto;
    private static final int  CALL_PHONE_REQUEST_CODE = 101;
    Configuraciones objConfiguracion = new Configuraciones();
    String URL = objConfiguracion.urlWebServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_contacto);

        nombre = (EditText) findViewById(R.id.txtNombreCompletoEditar);
        telefono = (EditText) findViewById(R.id.txtTelefonoEditar);

        botonAgregar = (Button) findViewById(R.id.btnGuardarContactoEditar);
        botonRegresar = (Button) findViewById(R.id.btnRegresarEditar);
        botonEliminar = (Button) findViewById(R.id.btnEliminarEditar);
        botonLlamar = (Button) findViewById(R.id.btnLlamarEditar);

        botonRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regresar();
            }
        });
        botonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificar();
            }
        });
        botonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ModificarContacto.this);
                dialogBuilder.setMessage(R.string.delete_contact_dialog_message);

                dialogBuilder.setPositiveButton(R.string.yes_delete_contact, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminar();
                    }
                });

                dialogBuilder.setNegativeButton(R.string.no_delete_contact, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                Dialog dialog = dialogBuilder.show();
            }
        });

        botonLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.CALL_PHONE, CALL_PHONE_REQUEST_CODE);
            }
        });
    }

    private void modificar() {
        try {
            RequestQueue objetoPeticion = Volley.newRequestQueue(ModificarContacto.this);
            StringRequest peticion = new StringRequest(Request.Method.POST, this.URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject objJSONResultado = new JSONObject(response.toString());
                        String estado = objJSONResultado.getString("estado");

                        if (estado.equals("1")) {
                            Toast.makeText(ModificarContacto.this, "Contacto modificado con exito", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ModificarContacto.this, "Error: " + estado, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ModificarContacto.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("accion", "modificar");
                    params.put("id_contacto", id_contacto);
                    params.put("nombre", nombre.getText().toString());
                    params.put("telefono", telefono.getText().toString());
                    return params;
                }
            };

            objetoPeticion.add(peticion);

        } catch (Exception error) {
            Toast.makeText(ModificarContacto.this, "Error en tiempo de ejecución: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminar() {
        try {
            RequestQueue objetoPeticion = Volley.newRequestQueue(ModificarContacto.this);
            StringRequest peticion = new StringRequest(Request.Method.POST, this.URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject objJSONResultado = new JSONObject(response.toString());
                        String estado = objJSONResultado.getString("estado");

                        if (estado.equals("1")) {
                            Toast.makeText(ModificarContacto.this, "Contacto eliminado con exito", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ModificarContacto.this, "Error: " + estado, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ModificarContacto.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("accion", "eliminar");
                    params.put("id_contacto", id_contacto);
                    return params;
                }
            };

            objetoPeticion.add(peticion);

        } catch (Exception error) {
            Toast.makeText(ModificarContacto.this, "Error en tiempo de ejecución: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void regresar() {
        Intent actividad = new Intent(ModificarContacto.this, MainActivity.class);
        startActivity(actividad);
        ModificarContacto.this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle valoresAdicionales = getIntent().getExtras();
        if (valoresAdicionales == null) {
            Toast.makeText(ModificarContacto.this, "Debes enviar el ID de contacto", Toast.LENGTH_SHORT).show();
            id_contacto = "";
            regresar();
        } else {
            id_contacto = valoresAdicionales.getString("id_contacto");
            nombre_contacto = valoresAdicionales.getString("nombre");
            telefono_contacto = valoresAdicionales.getString("telefono");
            verContacto();
        }
    }

    private void verContacto() {
        nombre.setText(nombre_contacto);
        telefono.setText(telefono_contacto);

    }

    private void llamar() {
        String numero = telefono.getText().toString();
        Uri uriNumber = Uri.parse("tel:" + numero);

        Intent callIntent = new Intent(Intent.ACTION_CALL, uriNumber);

        try {
            startActivity(callIntent);
        } catch (SecurityException securityException) {
            Toast.makeText(ModificarContacto.this, "Error: Permisos no concedidos.", Toast.LENGTH_LONG).show();
        }
    }

    private void checkPermission(String permission, int requestCode) {
        if(ContextCompat.checkSelfPermission(ModificarContacto.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(ModificarContacto.this, new String[] { permission }, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == CALL_PHONE_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                llamar();
            }
        } else {
            Toast.makeText(ModificarContacto.this, "La aplicación no posee permisos.", Toast.LENGTH_SHORT).show();
        }
    }
}