package com.zkhc.mysqlDif.util;

import com.mysql.jdbc.PreparedStatement;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * MYSQL数据库访问工具类. 
 * @author ADMIN
 */
public class MySqlDBHelper {
	public final Logger log = Logger.getLogger(this.getClass());
	private String URL = "jdbc:mysql://localhost:3306/test"; // 访问远程MySql的地址
	private String CLASSFORNAME = "com.mysql.jdbc.Driver"; // 驱动类
	private String UID = "root"; // 数据库账号
	private String PWD = "123456"; // 数据库密码
	private Connection conn;
	private PreparedStatement pStmt;
	private Statement stmt;
	private ResultSet rs;
	private String errorlog = "";

	/**
	 * 获取远程MYSQL数据库的链接.
	 * <br>
	 * author: ADMIN
	 * @return
	 */
	public synchronized Connection getConnection() {
		try {
			Class.forName(CLASSFORNAME);
			if (conn == null || conn.isClosed()) {
				conn = DriverManager.getConnection(URL, UID, PWD);
				log.debug("连接成功！"+URL);
			}
		} catch (ClassNotFoundException ex) {
			errorlog = "驱动类没有找到!";
			log.error(URL + "驱动类没有找到:" + ex);
		} catch (SQLException ex) {
			errorlog = "连接异常：SQLException";
			log.error(URL + "连接异常" + ex);
		}catch(Exception ex){
			errorlog = "连接异常："+ex.getMessage();
			log.error(URL + "连接异常" + ex);
		}
		return conn;
	}
	
	   /**
     * MYSQL多条记录查询.
     * <br>
     * author: ADMIN
     * @return List对象
     */
    public ArrayList< Hashtable<String,String>> getList(String sqlString) {
        ArrayList<Hashtable<String,String>> pkv = new ArrayList< Hashtable<String,String>>();
        try {
            conn = this.getConnection();
            stmt = conn.createStatement();
            
            rs = stmt.executeQuery(sqlString);
            ResultSetMetaData rsmd = rs.getMetaData();
            int num = rsmd.getColumnCount();
            while (rs.next()) {
                Hashtable<String,String> table = new Hashtable<String,String>();
                for (int i = 1; i <= num; i++) {
                    String key = rsmd.getColumnLabel(i);
                    String value = rs.getString(i);
                    if (value == null)
                        value = "";
                    table.put(key, value);
                }
                pkv.add(table);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorlog = "多条记录查询异常" + e.getMessage();
            log.error(URL + "多条记录查询异常" + e);
        }finally{
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                rs = null;
            }
            
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                stmt = null;
            }
        }
        
        return pkv;
    }

	/**
	 * MYSQL多条记录查询.
	 * <br>
	 * author: ADMIN
	 * @return List对象
	 */
	public ArrayList<Hashtable<String, String>> getPrepareList(String sqlString,String[]params) {
	    ArrayList<Hashtable<String,String>> pkv = new ArrayList< Hashtable<String,String>>();
		try {
			conn = this.getConnection();
			pStmt = (PreparedStatement) conn.prepareStatement(sqlString);
			
			for(int i=0; i<params.length; i++){
				pStmt.setString(i+1, params[i]);
			}
			
			rs = pStmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int num = rsmd.getColumnCount();
			while (rs.next()) {
			    Hashtable<String,String> table = new Hashtable<String,String>();
				for (int i = 1; i <= num; i++) {
					String key = rsmd.getColumnLabel(i);
					String value = rs.getString(i);
					if (value == null)
						value = "";
					table.put(key, value);
				}
				pkv.add(table);
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorlog = "多条记录查询异常" + e.getMessage();
			log.error(URL + "多条记录查询异常" + e);
		}finally{
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				rs = null;
			}
			
			if (pStmt != null) {
				try {
					pStmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				pStmt = null;
			}
		}
		
		return pkv;
	}

	/**
	 * 创建PreparedStatement.
	 * <br>
	 * author: ADMIN
	 * @param sql
	 */
	public void createBatch(String sql)
	{
		try
		{
			conn = this.getConnection();
			conn.setAutoCommit(false);
			pStmt = (PreparedStatement) conn.prepareStatement(sql);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			errorlog = "创建预编译批处理连接异常！";
			log.error("创建预编译批处理连接异常！");
		}
	}
	
	/**
	 * 加入批处理.
	 * author: ADMIN
	 * @param params
	 * @throws SQLException
	 */
	public void addBatch(String[] params) throws SQLException{
		for(int i=0; i<params.length; i++){
			pStmt.setString(i+1, params[i]);
		}
		pStmt.addBatch();
	}
	
	/**
	 * 执行批处理.
	 * author: ADMIN
	 * @throws SQLException
	 */
	public void doBatch() throws SQLException{
		if(pStmt!=null){
			pStmt.executeBatch();
			conn.commit();
		}
	}
	
	/**
	 * 关闭批处理.
	 * author: ADMIN
	 * @throws SQLException
	 */
	public void closeBatch(){
		if (pStmt != null) {
			try {
				pStmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			pStmt = null;
		}
	}
	
	public void closeConn(Statement stmt, Connection conn) {
		try {
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			log.error("关闭数据库连接异常：" + e.toString());
		}
	}
	
	   public void closeConn(Statement stmt) {
	        try {
	            if (stmt != null) {
	                stmt.close();
	                stmt = null;
	            }
	        } catch (SQLException e) {
	            log.error("关闭数据库连接异常：" + e.toString());
	        }
	    }
	
	/**
	 * 关闭数据库连接.
	 * <br>
	 * author: ADMIN
	 */
	public void closeConnection( ) {
		try {
			if (conn != null) {
				conn.setAutoCommit(true);
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			errorlog = "关闭数据库连接异常(closePrepareConn)："+e.getMessage();
			log.error(URL+" 关闭数据库连接异常(closePrepareConn)：" + e.toString());
		}
	}
	
	/**
	 * 更新数据
	 * 
	 * @param sqlstr
	 * @return int 1, 成功；0，不成功；
	 */
	public int execute(String sqlstr) {
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			stmt.execute(sqlstr);
			return 1;
		} catch (Exception e) {
			log.error(sqlstr + e.getMessage());
		} finally {
			this.closeConn(stmt);
		}
		return 0;
	}
	
	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public String getUID() {
		return UID;
	}

	public void setUID(String uID) {
		UID = uID;
	}

	public String getPWD() {
		return PWD;
	}

	public void setPWD(String pWD) {
		PWD = pWD;
	}

	public String getErrorlog() {
		return errorlog;
	}

	public void setErrorlog(String errorlog) {
		this.errorlog = errorlog;
	}
	
}
