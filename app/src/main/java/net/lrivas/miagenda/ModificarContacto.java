package net.lrivas.miagenda;

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

import net.lrivas.miagenda.clases.ConexionSQLite;

import java.util.Currency;

public class ModificarContacto extends AppCompatActivity {

    ConexionSQLite objConexion;
    final String NOMBRE_BASE_DATOS = "miagenda";
    EditText nombre, telefono;
    Button botonAgregar, botonRegresar, botonEliminar, botonLlamar;
    int id_contacto;
    private static final int  CALL_PHONE_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_contacto);

        objConexion = new ConexionSQLite(ModificarContacto.this, NOMBRE_BASE_DATOS, null, 1);
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
            SQLiteDatabase miBaseDatos = objConexion.getWritableDatabase();
            String comando = "UPDATE contactos SET nombre='" + nombre.getText() + "'," +
                    "telefono='" + telefono.getText() + "'WHERE id_contacto='" + id_contacto + "'";
            miBaseDatos.execSQL(comando);
            miBaseDatos.close();
            Toast.makeText(ModificarContacto.this, "Datos modificados con exito", Toast.LENGTH_SHORT).show();
        } catch (Exception error) {
            Toast.makeText(ModificarContacto.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminar() {
        try {
            SQLiteDatabase miBaseDatos = objConexion.getWritableDatabase();
            String comando = "DELETE FROM contactos WHERE id_contacto='" + id_contacto + "'";
            miBaseDatos.execSQL(comando);
            miBaseDatos.close();
            Toast.makeText(ModificarContacto.this, "Datos modificados con exito", Toast.LENGTH_SHORT).show();
            regresar();
        } catch (Exception error) {
            Toast.makeText(ModificarContacto.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
        } else {
            id_contacto = valoresAdicionales.getInt("id_contacto");
            verContacto();
        }
    }

    private void verContacto() {
        SQLiteDatabase base = objConexion.getReadableDatabase();
        String consulta = "SELECT id_contacto, nombre, telefono FROM contactos " +
                "WHERE id_contacto='" + id_contacto + "'";
        Cursor cadaRegistro = base.rawQuery(consulta, null);
        if (cadaRegistro.moveToFirst()) {
            do {
                nombre.setText(cadaRegistro.getString(1));
                telefono.setText(cadaRegistro.getString(2));
            } while (cadaRegistro.moveToNext());
        }
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
            Toast.makeText(ModificarContacto.this, "La aplicación no posee permisos.", Toast.LENGTH_SHORT);
        }
    }
}