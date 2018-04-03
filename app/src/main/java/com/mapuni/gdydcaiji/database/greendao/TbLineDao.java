package com.mapuni.gdydcaiji.database.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.mapuni.gdydcaiji.bean.TbLine;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TB_LINE".
*/
public class TbLineDao extends AbstractDao<TbLine, Long> {

    public static final String TABLENAME = "TB_LINE";

    /**
     * Properties of entity TbLine.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", false, "ID");
        public final static Property Bm = new Property(1, Long.class, "bm", true, "_id");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Sfz = new Property(3, String.class, "sfz", false, "SFZ");
        public final static Property Zdz = new Property(4, String.class, "zdz", false, "ZDZ");
        public final static Property Polyarrays = new Property(5, String.class, "polyarrays", false, "POLYARRAYS");
        public final static Property Oprator = new Property(6, String.class, "oprator", false, "OPRATOR");
        public final static Property Opttime = new Property(7, java.util.Date.class, "opttime", false, "OPTTIME");
        public final static Property Deleteflag = new Property(8, String.class, "deleteflag", false, "DELETEFLAG");
        public final static Property Createtime = new Property(9, java.util.Date.class, "createtime", false, "CREATETIME");
        public final static Property Note = new Property(10, String.class, "note", false, "NOTE");
        public final static Property Img = new Property(11, String.class, "img", false, "IMG");
        public final static Property Flag = new Property(12, int.class, "flag", false, "FLAG");
    }


    public TbLineDao(DaoConfig config) {
        super(config);
    }
    
    public TbLineDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TB_LINE\" (" + //
                "\"ID\" INTEGER," + // 0: id
                "\"_id\" INTEGER PRIMARY KEY ," + // 1: bm
                "\"NAME\" TEXT," + // 2: name
                "\"SFZ\" TEXT," + // 3: sfz
                "\"ZDZ\" TEXT," + // 4: zdz
                "\"POLYARRAYS\" TEXT," + // 5: polyarrays
                "\"OPRATOR\" TEXT," + // 6: oprator
                "\"OPTTIME\" INTEGER," + // 7: opttime
                "\"DELETEFLAG\" TEXT," + // 8: deleteflag
                "\"CREATETIME\" INTEGER," + // 9: createtime
                "\"NOTE\" TEXT," + // 10: note
                "\"IMG\" TEXT," + // 11: img
                "\"FLAG\" INTEGER NOT NULL );"); // 12: flag
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TB_LINE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TbLine entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long bm = entity.getBm();
        if (bm != null) {
            stmt.bindLong(2, bm);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String sfz = entity.getSfz();
        if (sfz != null) {
            stmt.bindString(4, sfz);
        }
 
        String zdz = entity.getZdz();
        if (zdz != null) {
            stmt.bindString(5, zdz);
        }
 
        String polyarrays = entity.getPolyarrays();
        if (polyarrays != null) {
            stmt.bindString(6, polyarrays);
        }
 
        String oprator = entity.getOprator();
        if (oprator != null) {
            stmt.bindString(7, oprator);
        }
 
        java.util.Date opttime = entity.getOpttime();
        if (opttime != null) {
            stmt.bindLong(8, opttime.getTime());
        }
 
        String deleteflag = entity.getDeleteflag();
        if (deleteflag != null) {
            stmt.bindString(9, deleteflag);
        }
 
        java.util.Date createtime = entity.getCreatetime();
        if (createtime != null) {
            stmt.bindLong(10, createtime.getTime());
        }
 
        String note = entity.getNote();
        if (note != null) {
            stmt.bindString(11, note);
        }
 
        String img = entity.getImg();
        if (img != null) {
            stmt.bindString(12, img);
        }
        stmt.bindLong(13, entity.getFlag());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TbLine entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long bm = entity.getBm();
        if (bm != null) {
            stmt.bindLong(2, bm);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String sfz = entity.getSfz();
        if (sfz != null) {
            stmt.bindString(4, sfz);
        }
 
        String zdz = entity.getZdz();
        if (zdz != null) {
            stmt.bindString(5, zdz);
        }
 
        String polyarrays = entity.getPolyarrays();
        if (polyarrays != null) {
            stmt.bindString(6, polyarrays);
        }
 
        String oprator = entity.getOprator();
        if (oprator != null) {
            stmt.bindString(7, oprator);
        }
 
        java.util.Date opttime = entity.getOpttime();
        if (opttime != null) {
            stmt.bindLong(8, opttime.getTime());
        }
 
        String deleteflag = entity.getDeleteflag();
        if (deleteflag != null) {
            stmt.bindString(9, deleteflag);
        }
 
        java.util.Date createtime = entity.getCreatetime();
        if (createtime != null) {
            stmt.bindLong(10, createtime.getTime());
        }
 
        String note = entity.getNote();
        if (note != null) {
            stmt.bindString(11, note);
        }
 
        String img = entity.getImg();
        if (img != null) {
            stmt.bindString(12, img);
        }
        stmt.bindLong(13, entity.getFlag());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1);
    }    

    @Override
    public TbLine readEntity(Cursor cursor, int offset) {
        TbLine entity = new TbLine( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // bm
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // sfz
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // zdz
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // polyarrays
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // oprator
            cursor.isNull(offset + 7) ? null : new java.util.Date(cursor.getLong(offset + 7)), // opttime
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // deleteflag
            cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)), // createtime
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // note
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // img
            cursor.getInt(offset + 12) // flag
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TbLine entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setBm(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setSfz(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setZdz(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setPolyarrays(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setOprator(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setOpttime(cursor.isNull(offset + 7) ? null : new java.util.Date(cursor.getLong(offset + 7)));
        entity.setDeleteflag(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setCreatetime(cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)));
        entity.setNote(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setImg(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setFlag(cursor.getInt(offset + 12));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(TbLine entity, long rowId) {
        entity.setBm(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(TbLine entity) {
        if(entity != null) {
            return entity.getBm();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TbLine entity) {
        return entity.getBm() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
