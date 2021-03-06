package com.mapuni.gdydcaiji.database.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.mapuni.gdydcaiji.bean.InPoint;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "IN_POINT".
*/
public class InPointDao extends AbstractDao<InPoint, Long> {

    public static final String TABLENAME = "IN_POINT";

    /**
     * Properties of entity InPoint.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", false, "ID");
        public final static Property Bm = new Property(1, Long.class, "bm", true, "_id");
        public final static Property Lytype = new Property(2, String.class, "lytype", false, "LYTYPE");
        public final static Property Lyxz = new Property(3, String.class, "lyxz", false, "LYXZ");
        public final static Property Name = new Property(4, String.class, "name", false, "NAME");
        public final static Property Fl = new Property(5, String.class, "fl", false, "FL");
        public final static Property Dz = new Property(6, String.class, "dz", false, "DZ");
        public final static Property Dy = new Property(7, String.class, "dy", false, "DY");
        public final static Property Lxdh = new Property(8, String.class, "lxdh", false, "LXDH");
        public final static Property Dj = new Property(9, String.class, "dj", false, "DJ");
        public final static Property Lycs = new Property(10, String.class, "lycs", false, "LYCS");
        public final static Property Lng = new Property(11, Double.class, "lng", false, "LNG");
        public final static Property Lat = new Property(12, Double.class, "lat", false, "LAT");
        public final static Property Oprator = new Property(13, String.class, "oprator", false, "OPRATOR");
        public final static Property Opttime = new Property(14, java.util.Date.class, "opttime", false, "OPTTIME");
        public final static Property Deleteflag = new Property(15, String.class, "deleteflag", false, "DELETEFLAG");
        public final static Property Createtime = new Property(16, java.util.Date.class, "createtime", false, "CREATETIME");
        public final static Property Note = new Property(17, String.class, "note", false, "NOTE");
        public final static Property Authcontent = new Property(18, String.class, "authcontent", false, "AUTHCONTENT");
        public final static Property Authflag = new Property(19, String.class, "authflag", false, "AUTHFLAG");
        public final static Property Lyzhs = new Property(20, String.class, "lyzhs", false, "LYZHS");
        public final static Property Img = new Property(21, String.class, "img", false, "IMG");
        public final static Property Flag = new Property(22, int.class, "flag", false, "FLAG");
        public final static Property Homearea = new Property(23, String.class, "homearea", false, "HOMEAREA");
    }


    public InPointDao(DaoConfig config) {
        super(config);
    }
    
    public InPointDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"IN_POINT\" (" + //
                "\"ID\" INTEGER," + // 0: id
                "\"_id\" INTEGER PRIMARY KEY ," + // 1: bm
                "\"LYTYPE\" TEXT," + // 2: lytype
                "\"LYXZ\" TEXT," + // 3: lyxz
                "\"NAME\" TEXT," + // 4: name
                "\"FL\" TEXT," + // 5: fl
                "\"DZ\" TEXT," + // 6: dz
                "\"DY\" TEXT," + // 7: dy
                "\"LXDH\" TEXT," + // 8: lxdh
                "\"DJ\" TEXT," + // 9: dj
                "\"LYCS\" TEXT," + // 10: lycs
                "\"LNG\" REAL," + // 11: lng
                "\"LAT\" REAL," + // 12: lat
                "\"OPRATOR\" TEXT," + // 13: oprator
                "\"OPTTIME\" INTEGER," + // 14: opttime
                "\"DELETEFLAG\" TEXT," + // 15: deleteflag
                "\"CREATETIME\" INTEGER," + // 16: createtime
                "\"NOTE\" TEXT," + // 17: note
                "\"AUTHCONTENT\" TEXT," + // 18: authcontent
                "\"AUTHFLAG\" TEXT," + // 19: authflag
                "\"LYZHS\" TEXT," + // 20: lyzhs
                "\"IMG\" TEXT," + // 21: img
                "\"FLAG\" INTEGER NOT NULL ," + // 22: flag
                "\"HOMEAREA\" TEXT);"); // 23: homearea
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"IN_POINT\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, InPoint entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long bm = entity.getBm();
        if (bm != null) {
            stmt.bindLong(2, bm);
        }
 
        String lytype = entity.getLytype();
        if (lytype != null) {
            stmt.bindString(3, lytype);
        }
 
        String lyxz = entity.getLyxz();
        if (lyxz != null) {
            stmt.bindString(4, lyxz);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
 
        String fl = entity.getFl();
        if (fl != null) {
            stmt.bindString(6, fl);
        }
 
        String dz = entity.getDz();
        if (dz != null) {
            stmt.bindString(7, dz);
        }
 
        String dy = entity.getDy();
        if (dy != null) {
            stmt.bindString(8, dy);
        }
 
        String lxdh = entity.getLxdh();
        if (lxdh != null) {
            stmt.bindString(9, lxdh);
        }
 
        String dj = entity.getDj();
        if (dj != null) {
            stmt.bindString(10, dj);
        }
 
        String lycs = entity.getLycs();
        if (lycs != null) {
            stmt.bindString(11, lycs);
        }
 
        Double lng = entity.getLng();
        if (lng != null) {
            stmt.bindDouble(12, lng);
        }
 
        Double lat = entity.getLat();
        if (lat != null) {
            stmt.bindDouble(13, lat);
        }
 
        String oprator = entity.getOprator();
        if (oprator != null) {
            stmt.bindString(14, oprator);
        }
 
        java.util.Date opttime = entity.getOpttime();
        if (opttime != null) {
            stmt.bindLong(15, opttime.getTime());
        }
 
        String deleteflag = entity.getDeleteflag();
        if (deleteflag != null) {
            stmt.bindString(16, deleteflag);
        }
 
        java.util.Date createtime = entity.getCreatetime();
        if (createtime != null) {
            stmt.bindLong(17, createtime.getTime());
        }
 
        String note = entity.getNote();
        if (note != null) {
            stmt.bindString(18, note);
        }
 
        String authcontent = entity.getAuthcontent();
        if (authcontent != null) {
            stmt.bindString(19, authcontent);
        }
 
        String authflag = entity.getAuthflag();
        if (authflag != null) {
            stmt.bindString(20, authflag);
        }
 
        String lyzhs = entity.getLyzhs();
        if (lyzhs != null) {
            stmt.bindString(21, lyzhs);
        }
 
        String img = entity.getImg();
        if (img != null) {
            stmt.bindString(22, img);
        }
        stmt.bindLong(23, entity.getFlag());
 
        String homearea = entity.getHomearea();
        if (homearea != null) {
            stmt.bindString(24, homearea);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, InPoint entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long bm = entity.getBm();
        if (bm != null) {
            stmt.bindLong(2, bm);
        }
 
        String lytype = entity.getLytype();
        if (lytype != null) {
            stmt.bindString(3, lytype);
        }
 
        String lyxz = entity.getLyxz();
        if (lyxz != null) {
            stmt.bindString(4, lyxz);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
 
        String fl = entity.getFl();
        if (fl != null) {
            stmt.bindString(6, fl);
        }
 
        String dz = entity.getDz();
        if (dz != null) {
            stmt.bindString(7, dz);
        }
 
        String dy = entity.getDy();
        if (dy != null) {
            stmt.bindString(8, dy);
        }
 
        String lxdh = entity.getLxdh();
        if (lxdh != null) {
            stmt.bindString(9, lxdh);
        }
 
        String dj = entity.getDj();
        if (dj != null) {
            stmt.bindString(10, dj);
        }
 
        String lycs = entity.getLycs();
        if (lycs != null) {
            stmt.bindString(11, lycs);
        }
 
        Double lng = entity.getLng();
        if (lng != null) {
            stmt.bindDouble(12, lng);
        }
 
        Double lat = entity.getLat();
        if (lat != null) {
            stmt.bindDouble(13, lat);
        }
 
        String oprator = entity.getOprator();
        if (oprator != null) {
            stmt.bindString(14, oprator);
        }
 
        java.util.Date opttime = entity.getOpttime();
        if (opttime != null) {
            stmt.bindLong(15, opttime.getTime());
        }
 
        String deleteflag = entity.getDeleteflag();
        if (deleteflag != null) {
            stmt.bindString(16, deleteflag);
        }
 
        java.util.Date createtime = entity.getCreatetime();
        if (createtime != null) {
            stmt.bindLong(17, createtime.getTime());
        }
 
        String note = entity.getNote();
        if (note != null) {
            stmt.bindString(18, note);
        }
 
        String authcontent = entity.getAuthcontent();
        if (authcontent != null) {
            stmt.bindString(19, authcontent);
        }
 
        String authflag = entity.getAuthflag();
        if (authflag != null) {
            stmt.bindString(20, authflag);
        }
 
        String lyzhs = entity.getLyzhs();
        if (lyzhs != null) {
            stmt.bindString(21, lyzhs);
        }
 
        String img = entity.getImg();
        if (img != null) {
            stmt.bindString(22, img);
        }
        stmt.bindLong(23, entity.getFlag());
 
        String homearea = entity.getHomearea();
        if (homearea != null) {
            stmt.bindString(24, homearea);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1);
    }    

    @Override
    public InPoint readEntity(Cursor cursor, int offset) {
        InPoint entity = new InPoint( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // bm
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // lytype
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // lyxz
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // name
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // fl
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // dz
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // dy
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // lxdh
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // dj
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // lycs
            cursor.isNull(offset + 11) ? null : cursor.getDouble(offset + 11), // lng
            cursor.isNull(offset + 12) ? null : cursor.getDouble(offset + 12), // lat
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // oprator
            cursor.isNull(offset + 14) ? null : new java.util.Date(cursor.getLong(offset + 14)), // opttime
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // deleteflag
            cursor.isNull(offset + 16) ? null : new java.util.Date(cursor.getLong(offset + 16)), // createtime
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // note
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // authcontent
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), // authflag
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // lyzhs
            cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21), // img
            cursor.getInt(offset + 22), // flag
            cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23) // homearea
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, InPoint entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setBm(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setLytype(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setLyxz(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setFl(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setDz(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setDy(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setLxdh(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setDj(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setLycs(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setLng(cursor.isNull(offset + 11) ? null : cursor.getDouble(offset + 11));
        entity.setLat(cursor.isNull(offset + 12) ? null : cursor.getDouble(offset + 12));
        entity.setOprator(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setOpttime(cursor.isNull(offset + 14) ? null : new java.util.Date(cursor.getLong(offset + 14)));
        entity.setDeleteflag(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setCreatetime(cursor.isNull(offset + 16) ? null : new java.util.Date(cursor.getLong(offset + 16)));
        entity.setNote(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setAuthcontent(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setAuthflag(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setLyzhs(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setImg(cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21));
        entity.setFlag(cursor.getInt(offset + 22));
        entity.setHomearea(cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(InPoint entity, long rowId) {
        entity.setBm(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(InPoint entity) {
        if(entity != null) {
            return entity.getBm();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(InPoint entity) {
        return entity.getBm() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
