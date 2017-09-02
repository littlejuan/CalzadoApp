package org.littlejuan.calzadoapp.database;

import android.provider.BaseColumns;

public final class CalzadoContract {
    private CalzadoContract() { }

    public static class CalzadoEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_CEDULA = "cedula";
        public static final String COLUMN_NOMBRE = "nombre";
        public static final String COLUMN_TIPO = "tipo";
        public static final String COLUMN_TALLA = "talla";
        public static final String COLUMN_RESULTADO = "resultado";
        public static final String COLUMN_CANTIDAD = "cantidad";

    }
}
