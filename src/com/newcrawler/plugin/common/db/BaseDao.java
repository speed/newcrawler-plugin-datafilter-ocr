package com.newcrawler.plugin.common.db;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public final class BaseDao {
	private final static Log logger = LogFactory.getLog(BaseDao.class);
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	
	private static Connection getConnection(Map<String, String> properties){
		try {
			Connection conn = ConnectionPool.getConnection(properties);
			return conn;
		} catch (SQLException e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw new RuntimeException("数据库连接错误！", e);
		}
	}
	
	public static List<Object> query(Map<String, String> properties, Class<?> c, String sql, List<Object> parameters)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, SQLException, InstantiationException{
		Connection conn=getConnection(properties);
		List<Object> list = null;
		try{
			list = query(conn,c, sql, parameters);
		}finally{
			closeConnect(conn);
		}
		return list;
	}
	/**
	 * 封装数据库的查操作
	 * 
	 * @param c
	 *            反射类的对象
	 * @param sql
	 *            操作的查询SQL语句
	 * @param parameters
	 *            参数集，调用时无则写null
	 * @return list 集合
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	public static List<Object> query(Connection conn, Class<?> c, String sql, List<Object> parameters)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, SQLException, InstantiationException{
		
		List<Object> list = new ArrayList<Object>();
		Object o = null;
		PreparedStatement ps = conn.prepareStatement(sql);
		setParameter(ps, parameters);
		ResultSet rs = ps.executeQuery();
		try{
			// 得到列信息ResultSetMetaDate对象
			ResultSetMetaData rsmd = rs.getMetaData();
			// 创建一个String的数组,用来保存所有的列名
			// rsmd.getColumnCount()为当前结果集中的列的总数,所以定义为长度
			String[] cName = new String[rsmd.getColumnCount()];
			for (int i = 0; i < cName.length; i++) {
				cName[i] = rsmd.getColumnName(i + 1);
			}
			while (rs.next()) {
				// 如果结果集得到了数据，则实例一个对象
				o = c.newInstance();
				for (int i = 0; i < cName.length; i++) {
					Object value=rs.getObject(i + 1);
					if(value==null){
						continue;
					}
					if(value instanceof Date){
						value=sdf.format((Date)value);
					}else{
						value=String.valueOf(value);
					}
					
					String mName="set" + cName[i].substring(0, 1).toUpperCase()+ cName[i].substring(1);
					try {
						Method method = c.getDeclaredMethod(mName, value.getClass());
						method.invoke(o, value);
					} catch (SecurityException e) {
						logger.error(ExceptionUtils.getFullStackTrace(e));
					} catch (NoSuchMethodException e) {
						logger.debug(e.toString());
					} 
				}
				// 添加到list集合中
				list.add(o);
			}
		}finally{
			closeRs(rs);
			closePs(ps);
		}
		return list;
	}
	
	public static int count(Map<String, String> properties, String sql, List<Object> parameters){
		Connection conn=getConnection(properties);
		int count = 0;
		try{
			count = count(conn, sql, parameters);
		}finally{
			closeConnect(conn);
		}
		return count;
	}
	
	/**
	 * 统计总数
	 * @param sql
	 * @param parameters
	 * @return
	 */
	public static int count(Connection conn, String sql, List<Object> parameters) {
		int count = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			setParameter(ps, parameters);
			rs = ps.executeQuery();
			if (rs.next()){
				count = rs.getInt(1);
		    }
		} catch (SQLException e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		} finally {
			closeRs(rs);
			closePs(ps);
		}
		return count;
	}
	
	public static int saveOrUpdate(Map<String, String> properties, String sql, List<Object> parameters) {
		Connection conn=getConnection(properties);
		int result = 0;
		try{
			result = saveOrUpdate(conn, sql, parameters);
		}finally{
			closeConnect(conn);
		}
		return result;
	}
	/**
	 * 封装数据库的增，删，改操作
	 * 
	 * @param sql
	 *            操作的SQL语句
	 * @param parameters
	 *            参数集
	 * @return 影响行数
	 */
	public static int saveOrUpdate(Connection conn, String sql, List<Object> parameters) {
		int result = 0;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			setParameter(ps, parameters);
			result = ps.executeUpdate();
		} catch (SQLException e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		} finally {
			closePs(ps);
		}
		return result;
	}

	/**
	 * 设置参数
	 * 
	 * @param parameters 参数集
	 * @throws SQLException 抛出SQL异常
	 */
	private static void setParameter(PreparedStatement ps, List<Object> parameters)
			throws SQLException {
		if (parameters != null && parameters.size() > 0) {
			for (int i = 0; i < parameters.size(); i++) {
				ps.setObject(i + 1, parameters.get(i));
			}
		}
	}

	/**
	 * 关闭程序中的Connectin 连接
	 * 
	 * @param con
	 *            Connection对象
	 */
	public static void closeConnect(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}

	/**
	 * 关闭程序中PreparedStatement对象
	 * 
	 * @param ps
	 *            PreparedStatement对象
	 */
	private static void closePs(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}

	/**
	 * 关闭程序中ResultSet 对象
	 * 
	 * @param rs
	 *            ResultSet对象
	 */
	private static void closeRs(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}
}
