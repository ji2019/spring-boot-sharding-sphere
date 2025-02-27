package com.oujiong.config;

import java.beans.beancontext.BeanContext;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

/**
 * 
 * https://blog.csdn.net/he__xu/article/details/119055957
 * @author gufusheng
 * 
 */
@Component
public class DBUtils {

	private static final Logger logger = LoggerFactory.getLogger(DBUtils.class);


	private static Connection connection;
	private static String[] shardingTableNames;
	private static String active;

	static {
		try {
			Class.forName(DbConfig.driverClass);
			connection = DriverManager.getConnection(DbConfig.url, DbConfig.user, DbConfig.password);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		shardingTableNames = getYmlConfig("term.sharding.tables").split(",");
	}

	
		
	

	/**
	 * @param ymlName 配置文件路径
	 * @param key     查找的配置键
	 * @return
	 */
	public static String getYmlConfig(String ymlName, String key) {
		String[] split = key.split("\\.");
		Yaml yaml = new Yaml();
		InputStream inputStream;
		Resource resource;
		try {
			resource = new ClassPathResource(ymlName);
			inputStream = resource.getInputStream();
			Map<String, Object> load = yaml.loadAs(inputStream, Map.class);
			for (int i = 0; i < split.length; i++) {
				if (i == split.length - 1) {
					return (String) load.get(split[i]);
				} else {
					load = (Map<String, Object>) load.get(split[i]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 配置键查找profile内的配置值
	 * 
	 * @param key
	 * @return
	 */
	public static String getYmlConfig(String key) {
		String[] split = key.split("\\.");
		Yaml yaml = new Yaml();
		InputStream inputStream;
		Resource resource;
		try {
			resource = new ClassPathResource("application-" + active + ".yml");
			inputStream = resource.getInputStream();
			Map<String, Object> load = yaml.loadAs(inputStream, Map.class);
			for (int i = 0; i < split.length; i++) {
				if (i == split.length - 1) {
					return (String) load.get(split[i]);
				} else {
					load = (Map<String, Object>) load.get(split[i]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 执行sql
	 * 
	 * @param sql sql语句
	 * @throws Exception
	 */
	public void executeSql(String sql) throws Exception {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.execute();
			logger.info("sql执行成功..");
		} catch (SQLException e) {
			throw new Exception("sql执行失败.." + '\n' + "SQL: " + sql);
		}
	}

	/**
	 * 获取数据库中所有数据表
	 * 
	 * @return
	 */
	public List<String> tableNames() {
		StringBuffer sbTables = new StringBuffer();
		List<String> tables = new ArrayList<String>();
		try {
			DatabaseMetaData dbMetaData = connection.getMetaData();
			ResultSet rs = dbMetaData.getTables(null, null, null, new String[] { "TABLE" });
			while (rs.next()) {// ///TABLE_TYPE/REMARKS
				// sbTables.append("表名：" + rs.getString("TABLE_NAME") + "<br/>");
				// sbTables.append("表类型：" + rs.getString("TABLE_TYPE") + "<br/>");
				// sbTables.append("表所属数据库：" + rs.getString("TABLE_CAT") + "<br/>");
				// sbTables.append("表所属用户名：" + rs.getString("TABLE_SCHEM")+ "<br/>");
				// sbTables.append("表备注：" + rs.getString("REMARKS") + "<br/>");
				if ("share0".equalsIgnoreCase(rs.getString("TABLE_CAT"))) {
					tables.add(rs.getString("TABLE_NAME"));
				}
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		return tables;
	}

//	public void createHisTrackTable(String tableName, SchoolTerm schoolTerm) throws Exception {
//		String newName = splitTableName(tableName, schoolTerm);
//		Statement statement = null;
//		PreparedStatement preparedStatement = null;
//		String rename = "rename table " + tableName + " to " + newName + ";";
//		String create = "create table " + tableName + " like " + newName + ";";
//		try {
//			statement = connection.createStatement();
//			preparedStatement = connection.prepareStatement(rename);
//
//			assert statement != null;
//
//			preparedStatement.execute();
//
//			preparedStatement = connection.prepareStatement(create);
//			preparedStatement.execute();
//
//			logger.info(tableName + ": 数据移植成功..");
//		} catch (SQLException e) {
//			throw new Exception(tableName + "数据移植失败..");
//		}
//	}

//	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
//	public void termListener() {
//		ThirdService thirdService = BeanContext.getBean(ThirdService.class);
//		SchoolTermService schoolTermService = BeanContext.getBean(SchoolTermService.class);
//		SchoolTermMapper schoolTermMapper = BeanContext.getBean(SchoolTermMapper.class);
//		// 获取智慧校园当前学期
//		SchoolTerm currentTerm = thirdService.getCurrentTerm();
//		// 获取系统最新学期
//		SchoolTerm lastSchoolTerm = schoolTermService.lastTerm();
//		if (lastSchoolTerm != null) {
//			if (!StringUtils.equals(lastSchoolTerm.getTerm(), currentTerm.getTerm())) {
//				// 系统当前学期和智慧校园当前学期不一致
//				try {
//					for (String tableName : shardingTableNames) {
//						createHisTrackTable(tableName, lastSchoolTerm);
//					}
//					// 切换系统学期
//					lastSchoolTerm.setIsLast(0);
//					schoolTermMapper.updateById(lastSchoolTerm);
//					currentTerm.setIsLast(1);
//					schoolTermMapper.insert(currentTerm);
//					logger.info("学期切换成功..");
//				} catch (Exception e) {
//					logger.info("学期切换失败..");
//					logger.error(e.getMessage());
//					logger.error(e.getCause().toString());
//					// 检测数据移植是否成功，失败则回滚
//					checkTransplant(lastSchoolTerm);
//				}
//			}
//		} else {
//			// 系统初始化
//			currentTerm.setIsLast(1);
//			schoolTermMapper.insert(currentTerm);
//			logger.info("学期初始化成功..");
//		}
//	}

//	/**
//	 * 检测数据移植是否成功,检测失败手动回滚
//	 */
//	public void checkTransplant(SchoolTerm schoolTerm) {
//		// 当前数据库存在的表
//		List<String> tableNames = tableNames();
//		// 按数据表移植成功后应存在的表
//		List<String> subTableNames = new ArrayList<>();
//		for (String shardingTableName : shardingTableNames) {
//			subTableNames.add(shardingTableName);
//			subTableNames.add(splitTableName(shardingTableName, schoolTerm));
//		}
//		// 进行数据移植的表
//		List<String> tableNameList = Arrays.asList(shardingTableNames);
//		for (String subTableName : subTableNames) {
//			if (!tableNames.contains(subTableName)) {
//				// 数据表移植检测失败
//				if (tableNameList.contains(subTableName)
//						&& tableNames.contains(splitTableName(subTableName, schoolTerm))) {
//					// 重命名成功，但创建表失败
//					String sql = "rename table " + subTableName + " to "
//							+ subTableName.substring(0, subTableName.length() - 6);
//					try {
//						executeSql(sql);
//					} catch (Exception e) {
//						logger.error("回滚重命名失败，请管理员处理!!");
//						logger.error(e.getMessage());
//						logger.error(e.getCause().toString());
//					}
//				}
//			}
//		}
//		logger.info("检测数据移植成功");
//	}

	// 进行表名拼接
//	public String splitTableName(String tableName, SchoolTerm schoolTerm) {
//		return tableName + schoolTerm.getTerm().substring(2, 4) + schoolTerm.getTerm().substring(7, 9)
//				+ schoolTerm.getTerm().substring(9);
//	}
}
