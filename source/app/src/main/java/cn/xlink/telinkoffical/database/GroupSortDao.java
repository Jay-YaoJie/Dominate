package cn.xlink.telinkoffical.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import cn.xlink.telinkoffical.bean.greenDao.GroupSort;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "GROUP_SORT".
*/
public class GroupSortDao extends AbstractDao<GroupSort, Long> {

    public static final String TABLENAME = "GROUP_SORT";

    /**
     * Properties of entity GroupSort.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property MeshAddress = new Property(2, Integer.class, "meshAddress", false, "MESH_ADDRESS");
        public final static Property Brightness = new Property(3, Integer.class, "brightness", false, "BRIGHTNESS");
        public final static Property Color = new Property(4, Integer.class, "color", false, "COLOR");
        public final static Property Temperature = new Property(5, Integer.class, "temperature", false, "TEMPERATURE");
        public final static Property IsShowOnHomeScreen = new Property(6, Boolean.class, "isShowOnHomeScreen", false, "IS_SHOW_ON_HOME_SCREEN");
        public final static Property Members = new Property(7, String.class, "members", false, "MEMBERS");
        public final static Property Type = new Property(8, String.class, "Type", false, "TYPE");
    };


    public GroupSortDao(DaoConfig config) {
        super(config);
    }
    
    public GroupSortDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"GROUP_SORT\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"NAME\" TEXT," + // 1: name
                "\"MESH_ADDRESS\" INTEGER," + // 2: meshAddress
                "\"BRIGHTNESS\" INTEGER," + // 3: brightness
                "\"COLOR\" INTEGER," + // 4: color
                "\"TEMPERATURE\" INTEGER," + // 5: temperature
                "\"IS_SHOW_ON_HOME_SCREEN\" INTEGER," + // 6: isShowOnHomeScreen
                "\"MEMBERS\" TEXT," + // 7: members
                "\"TYPE\" TEXT);"); // 8: Type
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"GROUP_SORT\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, GroupSort entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        Integer meshAddress = entity.getMeshAddress();
        if (meshAddress != null) {
            stmt.bindLong(3, meshAddress);
        }
 
        Integer brightness = entity.getBrightness();
        if (brightness != null) {
            stmt.bindLong(4, brightness);
        }
 
        Integer color = entity.getColor();
        if (color != null) {
            stmt.bindLong(5, color);
        }
 
        Integer temperature = entity.getTemperature();
        if (temperature != null) {
            stmt.bindLong(6, temperature);
        }
 
        Boolean isShowOnHomeScreen = entity.getIsShowOnHomeScreen();
        if (isShowOnHomeScreen != null) {
            stmt.bindLong(7, isShowOnHomeScreen ? 1L: 0L);
        }
 
        String members = entity.getMembers();
        if (members != null) {
            stmt.bindString(8, members);
        }
 
        String Type = entity.getType();
        if (Type != null) {
            stmt.bindString(9, Type);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public GroupSort readEntity(Cursor cursor, int offset) {
        GroupSort entity = new GroupSort( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // meshAddress
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // brightness
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // color
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // temperature
            cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0, // isShowOnHomeScreen
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // members
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8) // Type
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, GroupSort entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setMeshAddress(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setBrightness(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setColor(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setTemperature(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setIsShowOnHomeScreen(cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0);
        entity.setMembers(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setType(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(GroupSort entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(GroupSort entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
