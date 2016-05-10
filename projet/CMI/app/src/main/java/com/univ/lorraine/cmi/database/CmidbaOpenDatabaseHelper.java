package com.univ.lorraine.cmi.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.univ.lorraine.cmi.R;
import com.univ.lorraine.cmi.database.model.Annotation;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.database.model.Livre;

import java.sql.SQLException;

/**
 * Created by jyeil on 10/05/16.
 */
public class CmidbaOpenDatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "cmidba";
    private static final int DATABASE_VERSION = 1;

    public CmidbaOpenDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    private Dao<Livre, Long> livreDao;
    private Dao<Bibliotheque, Long> bibliothequeDao;
    private Dao<Annotation, Long> annotationDao;

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            /* Creates the tables */
            TableUtils.createTable(connectionSource, Livre.class);
            TableUtils.createTable(connectionSource, Bibliotheque.class);
            TableUtils.createTable(connectionSource, Annotation.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            /* Recreates the database when onUpgrade is called */
            TableUtils.dropTable(connectionSource, Livre.class, false);
            TableUtils.dropTable(connectionSource, Bibliotheque.class, false);
            TableUtils.dropTable(connectionSource, Annotation.class, false);
            onCreate(database, connectionSource);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns an instance of the livre data access object
     * @throws SQLException
     */
    public Dao<Livre, Long> getLivreDao() throws SQLException {
        if(livreDao == null) {
            livreDao = getDao(Livre.class);
        }
        return livreDao;
    }

    /**
     * Returns an instance of the bibliotheque data access object
     * @throws SQLException
     */
    public Dao<Bibliotheque, Long> getBibliothequeDao() throws SQLException {
        if(bibliothequeDao == null) {
            bibliothequeDao = getDao(Bibliotheque.class);
        }
        return bibliothequeDao;
    }

    /**
     * Returns an instance of the annotation data access object
     * @throws SQLException
     */
    public Dao<Annotation, Long> getAnnotationDao() throws SQLException {
        if(annotationDao == null) {
            annotationDao = getDao(Annotation.class);
        }
        return annotationDao;
    }


}
