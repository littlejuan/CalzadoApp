package org.littlejuan.calzadoapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.littlejuan.calzadoapp.database.CalzadoContract;
import org.littlejuan.calzadoapp.database.CalzadoOpenHelper;

public class MainActivity extends AppCompatActivity {

    CalzadoOpenHelper mDbHelper;

    private EditText mNombreEditText, mCedulaEditText, mResultadoEditText, mCantidadEditText;

    private Spinner mTipoSpinner, mTallaSpinner;

    private double mTipo;
    private double mTalla;

    private String mTipoText;
    private int mTallaValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbHelper = CalzadoOpenHelper.getInstance(this);

        mNombreEditText = (EditText) findViewById(R.id.edit_nombre);
        mCedulaEditText = (EditText) findViewById(R.id.edit_cedula);
        mCantidadEditText = (EditText) findViewById(R.id.edit_cantidad);
        mResultadoEditText = (EditText) findViewById(R.id.edit_resultado);
        mTipoSpinner = (Spinner) findViewById(R.id.spinner_tipo);
        mTallaSpinner = (Spinner) findViewById(R.id.spinner_talla);

        mTalla = 8;
        mTipo = 0;

        mTipoText = "Ejecutivo";
        mTallaValue = 8;

        setupTallaSpinner();
        setupTipoSpinner();
    }


    private void setupTallaSpinner() {
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.talla_options, android.R.layout.simple_spinner_item);

        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mTallaSpinner.setAdapter(genderSpinnerAdapter);

        mTallaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.ocho))) {
                        mTalla = 0;
                        mTallaValue = 8;
                    } else if (selection.equals(getString(R.string.nueve))) {
                        mTalla = 10.00;
                        mTallaValue = 9;
                    } else {
                        mTalla = 20.00;
                        mTallaValue = 10;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mTalla = 0;
                mTallaValue = 8;
            }
        });
    }


    private void setupTipoSpinner() {
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.calzado_options, android.R.layout.simple_spinner_item);

        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mTipoSpinner.setAdapter(genderSpinnerAdapter);

        mTipoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.calzado_a))) {
                        mTipo = 345.50;
                        mTipoText = getString(R.string.calzado_a);
                    } else if (selection.equals(getString(R.string.calzado_b))) {
                        mTipo = 298.70;
                        mTipoText = getString(R.string.calzado_b);
                    } else {
                        mTipo = 246.00;
                        mTipoText = getString(R.string.calzado_c);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mTipo = 345.50;
            }
        });
    }

    public void onEvaluar(View view){
        int cantidad = Integer.parseInt(mCantidadEditText.getEditableText().toString());
        mResultadoEditText.setText(String.valueOf((mTipo + mTalla)*cantidad));
    }

    public void onGuardar(View view){
        ContentValues contentValues = new ContentValues();

        String cedula = mCedulaEditText.getEditableText().toString();
        String nombre = mNombreEditText.getEditableText().toString();
        String cantidad = mCantidadEditText.getEditableText().toString();
        String resultado = mResultadoEditText.getEditableText().toString();

        contentValues.put(CalzadoContract.CalzadoEntry.COLUMN_CEDULA, cedula);
        contentValues.put(CalzadoContract.CalzadoEntry.COLUMN_NOMBRE, nombre);
        contentValues.put(CalzadoContract.CalzadoEntry.COLUMN_TIPO, mTipoText);
        contentValues.put(CalzadoContract.CalzadoEntry.COLUMN_TALLA, mTallaValue);
        contentValues.put(CalzadoContract.CalzadoEntry.COLUMN_CANTIDAD, cantidad);
        contentValues.put(CalzadoContract.CalzadoEntry.COLUMN_RESULTADO, resultado);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.insert(CalzadoContract.CalzadoEntry.TABLE_NAME, null, contentValues);
        db.close();

        onLimpiar(getWindow().getDecorView().getRootView());
    }

    public void onLimpiar(View view){
        mCedulaEditText.setText("");
        mNombreEditText.setText("");
        mCantidadEditText.setText("");
        mResultadoEditText.setText("");
        mTipoSpinner.setSelection(getIndex(mTipoSpinner, "Ejecutivo"));
        mTallaSpinner.setSelection(getIndex(mTallaSpinner, "8"));
    }

    public void onBuscar(View view){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String  cedula = mCedulaEditText.getEditableText().toString();

        Cursor result = db.rawQuery("SELECT * " +
                        "FROM " + CalzadoContract.CalzadoEntry.TABLE_NAME +
                        " WHERE " + CalzadoContract.CalzadoEntry.COLUMN_CEDULA + " = ?",
                new String[] {cedula});

        if (result.moveToFirst()){
            mNombreEditText.setText(result.getString(1));
            mTipoSpinner.setSelection(getIndex(mTipoSpinner, result.getString(2)));
            mTallaSpinner.setSelection(getIndex(mTallaSpinner, String.valueOf(result.getInt(3))));
            mResultadoEditText.setText(String.valueOf(result.getDouble(4)));
            mCantidadEditText.setText(String.valueOf(result.getInt(5)));
            Toast.makeText(this, "Usuario encontrado", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "No existe el usuario", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSalir(View view){
        finish();
    }

    private int getIndex(Spinner spinner, String myString){
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }
}
