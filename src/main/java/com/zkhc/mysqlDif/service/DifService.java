package com.zkhc.mysqlDif.service;

import com.zkhc.mysqlDif.util.Context;
import com.zkhc.mysqlDif.util.MySqlDBHelper;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;


/**
 * 比较两个MYSQL数据库中的对象结构差异 Service. 
 * @author ADMIN
 */
public class DifService {

	private static Properties prop = new Properties();  
	public static final Logger log = Logger.getLogger(new DifService().getClass());
	private static MySqlDBHelper db1;
	private static MySqlDBHelper db2;
	private static MySqlDBHelper db3;
	private static String schema1="";
	private static String schema2="";
	private static String schema3="";
	
	/**
	 * DifService初始化工作，定义数据库操作类对象.
 	 * @author: ADMIN
	 */
    public static void init( ) {
		try {
			if(db1 == null){
				db1 = new MySqlDBHelper();
			}
			if(db2 == null){
				db2 = new MySqlDBHelper();
			}
			if(db3 == null){
				db3 = new MySqlDBHelper();
			}
			prop.load(DifService.class.getClassLoader().getResourceAsStream("jdbc.properties"));
			db1.setURL(prop.get("jdbc1.URL").toString());
			db1.setUID(prop.get("jdbc1.UID").toString());
			db1.setPWD(prop.get("jdbc1.PWD").toString());
			
			db2.setURL(prop.get("jdbc2.URL").toString());
			db2.setUID(prop.get("jdbc2.UID").toString());
			db2.setPWD(prop.get("jdbc2.PWD").toString());
			
			db3.setURL(prop.get("jdbc3.URL").toString());
			db3.setUID(prop.get("jdbc3.UID").toString());
			db3.setPWD(prop.get("jdbc3.PWD").toString());
			
			schema1 = prop.get("jdbc1.SCHEMA").toString();
			schema2 = prop.get("jdbc2.SCHEMA").toString();
			schema3 = prop.get("jdbc3.SCHEMA").toString();
		} catch (Exception e) {
			e.printStackTrace();
			if(db1.getErrorlog() != null && !"".equals(db1.getErrorlog())){
				System.out.println(db1.getErrorlog());
			}
			if(db2.getErrorlog() != null && !"".equals(db2.getErrorlog())){
				System.out.println(db2.getErrorlog());
			}
		}  
	}
    
    /**
     * 获取要比对的表对象信息.
     * <br>
     * author: ADMIN
     * @param db
     * @param schema
     * @return
     * @throws SQLException 
     */
	public static void getTabInfo(MySqlDBHelper db, String schema)
			throws SQLException {
		ArrayList<Hashtable<String, String>> list = db.getPrepareList(
				Context.GET_TAB_INFO, new String[] { schema });
		
		db3.execute(db1.equals(db) ? Context.DEL_TAB_INFO1
				: Context.DEL_TAB_INFO2);
		
		db3.createBatch(db1.equals(db) ? Context.SAVE_TAB_INFO1
				: Context.SAVE_TAB_INFO2);
		
		for (int i = 0; i < list.size(); i++) {
			db3.addBatch(new String[] { 
					list.get(i).get("TABLE_NAME"),
					list.get(i).get("TABLE_COMMENT").replace("\r\n","  ") });
		}
		db3.doBatch();
		db3.closeBatch();
	}
    
    /**
     * 获取要比对的表中列对象信息.
     * <br>
     * author: ADMIN
     * @param db
     * @param schema
     * @return
     * @throws SQLException 
     */
	public static void getColInfo(MySqlDBHelper db,String schema) throws SQLException{
		ArrayList<Hashtable<String, String>> list = db.getPrepareList(
				Context.GET_COL_INFO, new String[] { schema });
		
		db3.execute(db1.equals(db) ? Context.DEL_COL_INFO1
				: Context.DEL_COL_INFO2);
		
		db3.createBatch(db1.equals(db) ? Context.SAVE_COL_INFO1
				: Context.SAVE_COL_INFO2);
		
		for (int i = 0; i < list.size(); i++) {
			db3.addBatch(new String[] { 
					list.get(i).get("TABLE_NAME"),
					list.get(i).get("COLUMN_NAME"),
					list.get(i).get("COLUMN_TYPE"),
					list.get(i).get("IS_NULLABLE"),
					list.get(i).get("COLUMN_KEY"),
					list.get(i).get("EXTRA"),
					list.get(i).get("COLUMN_COMMENT").replace("\r\n","  "),
					list.get(i).get("COLUMN_DEFAULT")});
		}
		db3.doBatch();
		db3.closeBatch();
    }
    
    /**
     * 获取要比对的表中索引对象信息.
     * <br>
     * author: ADMIN
     * @param db
     * @param schema
     * @return
     * @throws SQLException 
     */
	public static void getIndexInfo(MySqlDBHelper db,String schema) throws SQLException{
    	
		ArrayList<Hashtable<String, String>> list = db.getPrepareList(
				Context.GET_INDEX_INFO, new String[] { schema });
		
		db3.execute(db1.equals(db) ? Context.DEL_INDEX_INFO1
				: Context.DEL_INDEX_INFO2);
		
		db3.createBatch(db1.equals(db) ? Context.SAVE_INDEX_INFO1
				: Context.SAVE_INDEX_INFO2);
		
		for (int i = 0; i < list.size(); i++) {
			db3.addBatch(new String[] { 
					list.get(i).get("TABLE_NAME"),
					list.get(i).get("INDEX_NAME"),
					list.get(i).get("COLUMNS")});
		}
		db3.doBatch();
		db3.closeBatch();
    	
    }
    
    /**
     * 获取要比对的函数存储过程对象信息.
     * <br>
     * author: ADMIN
     * @param db
     * @param schema
     * @return
     * @throws SQLException 
     */
	public static void getRoutineInfo(MySqlDBHelper db,String schema) throws SQLException{
		ArrayList<Hashtable<String, String>> list = db.getPrepareList(
				Context.GET_ROUTINE_INFO, new String[] { schema });
		
		db3.execute(db1.equals(db) ? Context.DEL_ROUTINE_INFO1
				: Context.DEL_ROUTINE_INFO2);
		
		db3.createBatch(db1.equals(db) ? Context.SAVE_ROUTINE_INFO1
				: Context.SAVE_ROUTINE_INFO2);
		
		for (int i = 0; i < list.size(); i++) {
			db3.addBatch(new String[] { 
					list.get(i).get("ROUTINE_NAME"),
					list.get(i).get("ROUTINE_TYPE") });
		}
		db3.doBatch();
		db3.closeBatch();
    }
    
    /**
     * 表差异信息.
     * <br>
     * author: ADMIN
     * @return {TABLE_NAME1,TABLE_NAME2,TABLE_COMMENT}
     */
    public static ArrayList<Hashtable<String, String>> getDiffTab(){
    	
        ArrayList<Hashtable<String, String>> list = new ArrayList<Hashtable<String,String>>();

        ArrayList<Hashtable<String, String>> list1 = db3.getList(Context.GET_DIFF_TAB1);
        
        ArrayList<Hashtable<String, String>> list2 = db3.getList(Context.GET_DIFF_TAB2);

        ArrayList<Hashtable<String, String>> list3 = db3.getList(Context.GET_DIFF_TAB3);
    	
        list.addAll(list1);
        list.addAll(list2);
        list.addAll(list3);
    	
    	return list;
    }
    
    /**
     * 表中列差异信息.
     * <br>
     * author: ADMIN
     * @return {TABLE_NAME,COLUMN_NAME1,COLUMN_NAME2,COLUMN_TYPE,IS_NULLABLE,COLUMN_KEY,EXTRA,COLUMN_COMMENT,COLUMN_DEFAULT}
     */
    public static ArrayList<Hashtable<String, String>> getDiffCol(){     
        
        ArrayList<Hashtable<String, String>> list = new ArrayList<Hashtable<String,String>>();
        
        ArrayList<Hashtable<String, String>> list1 = db3.getList(Context.GET_DIFF_COL1);

        ArrayList<Hashtable<String, String>> list2 = db3.getList(Context.GET_DIFF_COL2);

        ArrayList<Hashtable<String, String>> list3 = db3.getList(Context.GET_DIFF_COL3);
        
        list.addAll(list1);
        list.addAll(list2);
        list.addAll(list3);
        
        return list;
    }
    
    
    /**
     * 表中索引差异信息.
     * <br>
     * author: ADMIN
     * @return {TABLE_NAME,INDEX_NAME1,INDEX_NAME2,COLUMNS}
     */
    public static ArrayList<Hashtable<String, String>> getDiffIndex(){     
        
        ArrayList<Hashtable<String, String>> list = new ArrayList<Hashtable<String,String>>();

        ArrayList<Hashtable<String, String>> list1 = db3.getList(Context.GET_DIFF_INDEX1);
        
        ArrayList<Hashtable<String, String>> list2 = db3.getList(Context.GET_DIFF_INDEX2);

        ArrayList<Hashtable<String, String>> list3 = db3.getList(Context.GET_DIFF_INDEX3);
        
        list.addAll(list1);
        list.addAll(list2);
        list.addAll(list3);
        
        return list;
    }
    
    /**
     * 函数存储过程差异信息.
     * <br>
     * author: ADMIN
     * @return {ROUTINE_NAME1,ROUTINE_NAME2,ROUTINE_TYPE}
     */
    public static ArrayList<Hashtable<String, String>> getDiffRoutine(){
        
        ArrayList<Hashtable<String, String>> list = new ArrayList<Hashtable<String,String>>();

        ArrayList<Hashtable<String, String>> list1 = db3.getList(Context.GET_DIFF_ROUTINE1);
        
        ArrayList<Hashtable<String, String>> list2 = db3.getList(Context.GET_DIFF_ROUTINE2);
        
        list.addAll(list1);
        list.addAll(list2);
        return list;
    }
	
    /**
     * 开始获取数据库对象查询信息
     * @throws SQLException
     */
    public static void prepareAllDiffInfo() throws SQLException{
        DifService.init();
        DifService.getTabInfo(db1, schema1);
        DifService.getTabInfo(db2, schema2);
        
        DifService.getColInfo(db1, schema1);
        DifService.getColInfo(db2, schema2);
        
        DifService.getIndexInfo(db1, schema1);
        DifService.getIndexInfo(db2, schema2);
        
        DifService.getRoutineInfo(db1, schema1);
        DifService.getRoutineInfo(db2, schema2);
    }
    
    /**
     * 查询出对象差异信息
     */
    public static void getAllDiffInfoTest(){
        ArrayList<Hashtable<String, String>>list1 = DifService.getDiffTab();
        System.out.println("=========================表比对结果========================");
        for(Hashtable<String, String> tab : list1){
            System.out.println(tab);
        }
        
        System.out.println("=========================列比对结果========================");
        ArrayList<Hashtable<String, String>>list2 =DifService.getDiffCol();
        for(Hashtable<String, String> tab : list2){
            System.out.println(tab);
        }
        
        System.out.println("=========================索引比对结果========================");
        ArrayList<Hashtable<String, String>>list3 =DifService.getDiffIndex();
        for(Hashtable<String, String> tab : list3){
            System.out.println(tab);
        }
        
        System.out.println("=========================函数存储过程比对结果========================");
        ArrayList<Hashtable<String, String>>list4 =DifService.getDiffRoutine();
        for(Hashtable<String, String> tab : list4){
            System.out.println(tab);
        }
    }
    
	public static void main(String[] args) {
		try{
		    log.debug("DifService.prepareAllDiffInfo() 开始执行...");
		    DifService.prepareAllDiffInfo();
		    
		    log.debug("DifService.getAllDiffInfo() 开始执行...");
		    DifService.getAllDiffInfoTest();
			
		}catch(Exception e){
		    log.debug("DifService main 方法异常:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db1.closeConnection();
			db2.closeConnection();
			db3.closeConnection();
		}
	}
	
	
	public static Properties getProp() {
		return prop;
	}

	public static void setProp(Properties prop) {
		DifService.prop = prop;
	}

	public static MySqlDBHelper getDb1() {
		return db1;
	}

	public static void setDb1(MySqlDBHelper db1) {
		DifService.db1 = db1;
	}

	public static MySqlDBHelper getDb2() {
		return db2;
	}

	public static void setDb2(MySqlDBHelper db2) {
		DifService.db2 = db2;
	}

	public static String getSchema1() {
		return schema1;
	}

	public static void setSchema1(String schema1) {
		DifService.schema1 = schema1;
	}

	public static String getSchema2() {
		return schema2;
	}

	public static void setSchema2(String schema2) {
		DifService.schema2 = schema2;
	}

	public static MySqlDBHelper getDb3() {
		return db3;
	}

	public static void setDb3(MySqlDBHelper db3) {
		DifService.db3 = db3;
	}

	public static String getSchema3() {
		return schema3;
	}

	public static void setSchema3(String schema3) {
		DifService.schema3 = schema3;
	}

}
