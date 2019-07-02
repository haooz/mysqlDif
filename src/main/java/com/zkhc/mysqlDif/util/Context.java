package com.zkhc.mysqlDif.util;

public class Context {

	public static final String DEL_TAB_INFO1 = " TRUNCATE TABLE t_tabinfo_1 ";

	public static final String DEL_TAB_INFO2 = " TRUNCATE TABLE t_tabinfo_2  ";

	public static final String DEL_COL_INFO1 = " TRUNCATE TABLE t_colinfo_1 ";

	public static final String DEL_COL_INFO2 = " TRUNCATE TABLE t_colinfo_2  ";
	
	public static final String DEL_INDEX_INFO1 = " TRUNCATE TABLE t_indexinfo_1 ";

	public static final String DEL_INDEX_INFO2 = " TRUNCATE TABLE t_indexinfo_2  ";
	
	public static final String DEL_ROUTINE_INFO1 = " TRUNCATE TABLE t_routineinfo_1 ";

	public static final String DEL_ROUTINE_INFO2 = " TRUNCATE TABLE t_routineinfo_2  ";
	
	public static final String GET_TAB_INFO = " SELECT UPPER(TABLE_NAME)TABLE_NAME,TABLE_COMMENT "
			+ "   FROM information_schema.TABLES  "
			+ "  WHERE TABLE_SCHEMA = ? ";

	public static final String GET_COL_INFO = "  SELECT UPPER(TABLE_NAME)TABLE_NAME,UPPER(COLUMN_NAME)COLUMN_NAME, "
			+ "			COLUMN_TYPE, IS_NULLABLE, COLUMN_KEY, EXTRA,COLUMN_COMMENT, "
			+ "			IFNULL(COLUMN_DEFAULT,'null')COLUMN_DEFAULT "
			+ "   FROM information_schema.COLUMNS a "
			+ "  WHERE TABLE_SCHEMA = ? ";
	
	public static final String GET_INDEX_INFO = " SELECT UPPER(table_name)TABLE_NAME,upper(index_name)INDEX_NAME, "
			+ "				 GROUP_CONCAT(upper(column_name) ORDER BY SEQ_IN_INDEX) COLUMNS "
			+ "		  FROM INFORMATION_SCHEMA.STATISTICS "
			+ "		 WHERE table_schema = ? "
			+ "		 GROUP BY TABLE_NAME,INDEX_NAME ";
	
	public static final String GET_ROUTINE_INFO = "SELECT a.ROUTINE_NAME,a.ROUTINE_TYPE "
			+ "	  FROM information_schema.Routines a "
			+ "	where a.ROUTINE_SCHEMA = ? ";

	public static final String SAVE_TAB_INFO1 = " INSERT INTO t_tabinfo_1(TABLE_NAME, TABLE_COMMENT) VALUES (?, ?) ";

	public static final String SAVE_TAB_INFO2 = " INSERT INTO t_tabinfo_2(TABLE_NAME, TABLE_COMMENT) VALUES (?, ?) ";

	public static final String SAVE_COL_INFO1 = " INSERT INTO t_colinfo_1 "
			+ "	 (TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, "
			+ "	  COLUMN_KEY, EXTRA, COLUMN_COMMENT, COLUMN_DEFAULT) "
			+ "	 VALUES " + "	  (?,?,?,?,?,?,?,?) ";

	public static final String SAVE_COL_INFO2 = " INSERT INTO t_colinfo_2 "
			+ "	 (TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, "
			+ "	  COLUMN_KEY, EXTRA, COLUMN_COMMENT, COLUMN_DEFAULT) "
			+ "	 VALUES " + "	  (?,?,?,?,?,?,?,?) ";
	
	public static final String SAVE_INDEX_INFO1 = " INSERT INTO  t_indexinfo_1 (TABLE_NAME ,INDEX_NAME ,COLUMNS) VALUES (?,?,?) ";
	
	public static final String SAVE_INDEX_INFO2 = " INSERT INTO  t_indexinfo_2 (TABLE_NAME ,INDEX_NAME ,COLUMNS) VALUES (?,?,?) ";
	
	public static final String SAVE_ROUTINE_INFO1 = " INSERT INTO  t_routineinfo_1 (ROUTINE_NAME ,ROUTINE_TYPE ) VALUES (?,?) ";
	
	public static final String SAVE_ROUTINE_INFO2 = " INSERT INTO  t_routineinfo_2 (ROUTINE_NAME ,ROUTINE_TYPE) VALUES (?,?) ";
	
	public static final String GET_DIFF_TAB1 =
            " select a.TABLE_NAME AS TABLE_NAME1,'表不存在' TABLE_NAME2,'-' TABLE_COMMENT " +
            "   from t_tabinfo_1 a " +
            "  where a.TABLE_NAME NOT in " + 
            " ( select b.TABLE_NAME FROM t_tabinfo_2 b ) ";
	
	public static final String GET_DIFF_TAB2 =
            " select a.TABLE_NAME AS TABLE_NAME2,'表不存在' TABLE_NAME1,'-' TABLE_COMMENT " +
            "   from t_tabinfo_2 a  " +
            "  where a.TABLE_NAME NOT in " +
            " ( select b.TABLE_NAME FROM t_tabinfo_1 b ) ";
	
	public static final String GET_DIFF_TAB3 =
            " select a.TABLE_NAME AS TABLE_NAME1,b.TABLE_NAME AS TABLE_NAME2,'表注释有差异' TABLE_COMMENT " +
            " from t_tabinfo_1 a INNER JOIN t_tabinfo_2 b " +
            "  ON a.TABLE_NAME = b.TABLE_NAME " +
            " AND a.TABLE_COMMENT <> b.TABLE_COMMENT ";
	
	
	   public static final String GET_DIFF_COL1 =
            " select a.TABLE_NAME,a.COLUMN_NAME COLUMN_NAME1,'列不存在' COLUMN_NAME2 ," +
            "        a.COLUMN_TYPE,a.IS_NULLABLE,a.COLUMN_KEY,a.EXTRA,a.COLUMN_COMMENT," +
            "        a.COLUMN_DEFAULT " +
            "  from t_colinfo_1 a " +
            " where a.TABLE_NAME in " +
            "  ( select b.TABLE_NAME from  t_tabinfo_2 b )" +
            " AND CONCAT(a.TABLE_NAME,'&',a.COLUMN_NAME)" +
            " not in " +
            "  ( select CONCAT(c.TABLE_NAME,'&',c.COLUMN_NAME) " +
            "      from t_colinfo_2 c ) ";
	   
       public static final String GET_DIFF_COL2 =
           " select a.TABLE_NAME,a.COLUMN_NAME COLUMN_NAME2,'列不存在' COLUMN_NAME1 ," +
           "        a.COLUMN_TYPE,a.IS_NULLABLE,a.COLUMN_KEY,a.EXTRA,a.COLUMN_COMMENT," +
           "        a.COLUMN_DEFAULT " +
           "  from t_colinfo_2 a " +
           " where a.TABLE_NAME in " +
           "      ( select b.TABLE_NAME from  t_tabinfo_1 b )" +
           "   AND  CONCAT(a.TABLE_NAME,'&',a.COLUMN_NAME)" +
           " not in " +
           "      ( select CONCAT(c.TABLE_NAME,'&',c.COLUMN_NAME) " +
           "         from t_colinfo_1 c ) ";
       
       public static final String GET_DIFF_COL3 =
           "     select a.TABLE_NAME,a.COLUMN_NAME COLUMN_NAME1,b.COLUMN_NAME COLUMN_NAME2 , " +
           "           (CASE WHEN a.COLUMN_TYPE <> b.COLUMN_TYPE THEN '类型有差异' ELSE '-' END)COLUMN_TYPE, " +
           "           (CASE WHEN a.IS_NULLABLE <> b.IS_NULLABLE THEN 'ISNULL有差异' ELSE '-' END)IS_NULLABLE, " +
           "           (CASE WHEN a.COLUMN_KEY <> b.COLUMN_KEY THEN 'KEY属性差异' ELSE '-' END)COLUMN_KEY, " +
           "           (CASE WHEN a.EXTRA <> b.EXTRA THEN '自增长属性差异' ELSE '-' END)EXTRA, " +
           "           (CASE WHEN a.COLUMN_COMMENT <> b.COLUMN_COMMENT THEN '列注释差异' ELSE '-' END)COLUMN_COMMENT, " +
           "           (CASE WHEN a.COLUMN_DEFAULT <> b.COLUMN_DEFAULT THEN '列默认值差异' ELSE '-' END)COLUMN_DEFAULT " +
           "      from t_colinfo_1 a INNER JOIN t_colinfo_2 b " +
           "        ON a.TABLE_NAME = b.TABLE_NAME " +
           "       AND a.COLUMN_NAME = b.COLUMN_NAME " +
           "       AND ( a.COLUMN_TYPE <> b.COLUMN_TYPE OR " +
           "             a.IS_NULLABLE <> b.IS_NULLABLE OR " +
           "             a.COLUMN_KEY <> b.COLUMN_KEY OR " +
           "             a.EXTRA <> b.EXTRA OR " +
           "             a.COLUMN_COMMENT <> b.COLUMN_COMMENT OR " +
           "             a.COLUMN_DEFAULT <> b.COLUMN_DEFAULT ) ";
       
       public static final String GET_DIFF_INDEX1 =
           " select a.TABLE_NAME,a.INDEX_NAME INDEX_NAME1,'索引不存在' INDEX_NAME2 ,a.COLUMNS" +
           "  from t_indexinfo_1 a " +
           " where a.TABLE_NAME in " +
           "  ( select b.TABLE_NAME from  t_tabinfo_2 b )" +
           " AND CONCAT(a.TABLE_NAME,'&',a.INDEX_NAME)" +
           " not in " +
           "  ( select CONCAT(c.TABLE_NAME,'&',c.INDEX_NAME) " +
           "      from t_indexinfo_2 c ) ";
       
       public static final String GET_DIFF_INDEX2 =
           " select a.TABLE_NAME,a.INDEX_NAME INDEX_NAME2,'索引不存在' INDEX_NAME1 ,a.COLUMNS" +
           "  from t_indexinfo_2 a " +
           " where a.TABLE_NAME in " +
           "  ( select b.TABLE_NAME from  t_tabinfo_1 b )" +
           " AND CONCAT(a.TABLE_NAME,'&',a.INDEX_NAME)" +
           " not in " +
           "  ( select CONCAT(c.TABLE_NAME,'&',c.INDEX_NAME) " +
           "      from t_indexinfo_1 c ) ";
       
       public static final String GET_DIFF_INDEX3 =
           "  select a.TABLE_NAME,a.INDEX_NAME INDEX_NAME1,b.INDEX_NAME INDEX_NAME2 , " +
           "         (CASE WHEN a.COLUMNS <> b.COLUMNS THEN '索引定义差异' ELSE '-' END)COLUMNS " +
           "    from t_indexinfo_1 a INNER JOIN t_indexinfo_2 b " +
           "      ON a.TABLE_NAME = b.TABLE_NAME " +
           "     AND a.INDEX_NAME = b.INDEX_NAME " +
           "     AND a.COLUMNS <> b.COLUMNS ";
       
       public static final String GET_DIFF_ROUTINE1 =
           " select a.ROUTINE_NAME ROUTINE_NAME1,'函数/存储过程不存在' ROUTINE_NAME2,a.ROUTINE_TYPE " +
           "   from t_routineinfo_1 a " +
           "  where a.ROUTINE_NAME NOT in " + 
           " ( select b.ROUTINE_NAME FROM t_routineinfo_2 b ) ";
       
       public static final String GET_DIFF_ROUTINE2 =
           " select a.ROUTINE_NAME ROUTINE_NAME2,'函数/存储过程不存在' ROUTINE_NAME1,a.ROUTINE_TYPE " +
           "   from t_routineinfo_2 a  " +
           "  where a.ROUTINE_NAME NOT in " +
           " ( select b.ROUTINE_NAME FROM t_routineinfo_1 b ) ";
       

}
