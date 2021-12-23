package com.oujiong.config;
 
//import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
//import com.dangdang.ddframe.rdb.sharding.api.strategy.table.SingleKeyTableShardingAlgorithm;
//import com.example.ziqiiii.shardingjdbcdemo.database.DataSourceConfig;
import com.google.common.collect.Range;

import org.apache.shardingsphere.api.sharding.ShardingValue;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
 
import java.sql.Connection;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import javax.sql.DataSource;
 
/**
 * create by ziqi on 2019/8/19
 */
@Component
public class TableShardingAlgorithm implements PreciseShardingAlgorithm, RangeShardingAlgorithm<Long> {
	
	
    private final String tablePrefix = "goods_";
 
    @Autowired
    private DataSource dataSource;
    
//  SELECT
//		TABLE_SCHEMA,
//		TABLE_NAME 
//	FROM
//		information_schema.`TABLES`
    
 
//    @Override
//    public String doEqualSharding( Collection<String> tableNames,  ShardingValue<Long> shardingValue) {
// 
//        //1.
//        //分表：goods_0,goods_1:
////       String endStr = shardingValue.getValue() % 2 + "";
////       String tb = tablePrefix + endStr;
////       return tb;
// 
//        
//        //下面的代码是动态创建表，并进行分表
//        //创建表功能OK，但是分表报错，不需要动态创建分表的话，可以注释掉下面部分
//        //2.
//        //动态创建表，假设分片规则为id%4,已经创建了0，1表，那么它会自动创建2，3表
//        List<String> databsesTable = dataSource.getTnames();
// 
//        String endStr = shardingValue.getValue() % 4 + "";
//        String tb = tablePrefix + endStr;
// 
//        if(tableNames.contains(tb)){
//            System.out.println(tb);
//        }else{
// 
//            String creatsql = String.format("CREATE TABLE `%s` (" +
//                    "  `goods_id` bigint(20) NOT NULL," +
//                    "  `goods_name` varchar(100) COLLATE utf8_bin NOT NULL," +
//                    "  `goods_type` bigint(20) DEFAULT NULL," +
//                    "  PRIMARY KEY (`goods_id`)" +
//                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin",tb);
// 
// 
//            Connection conn = dataSource.getConnection();
//            //执行创建表
//            System.out.println("//创建表");
//            try{
//                Statement stmt = conn.createStatement();
//                if(0 == stmt.executeUpdate(creatsql)) {
//                    System.out.println("成功创建表！");
//                } else {
//                    System.out.println("创建表失败！");
//                }
//                //
//                stmt.close();
//                conn.close();
//                System.out.println("//关闭资源");
// 
//                databsesTable.add(tb);
//                tableNames.add(tb);
// 
//            }catch (Exception e){
//                e.printStackTrace();
//            }
// 
// 
//        }
// 
//        return tb;
// 
// 
//    }
// 
//    @Override
//    public Collection<String> doInSharding(final Collection<String> tableNames, final ShardingValue<Long> shardingValue) {
//        Collection<String> result = new LinkedHashSet<>(tableNames.size());
//        for (Long value : shardingValue.getValues()) {
//            for (String tableName : tableNames) {
//                if (tableName.endsWith(value % 2 + "")) {
//                    result.add(tableName);
//                }
//            }
//        }
//        return result;
//    }
// 
//    @Override
//    public Collection<String> doBetweenSharding(final Collection<String> tableNames,
//                                                final ShardingValue<Long> shardingValue) {
//        Collection<String> result = new LinkedHashSet<>(tableNames.size());
//        Range<Long> range = shardingValue.getValueRange();
//        for (Long i = range.lowerEndpoint(); i <= range.upperEndpoint(); i++) {
//            for (String each : tableNames) {
//                if (each.endsWith(i % 2 + "")) {
//                    result.add(each);
//                }
//            }
//        }
//        return result;
//    }

	@Override
	public Collection<String> doSharding(Collection<String> availableTargetNames,
			RangeShardingValue<Long> shardingValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String doSharding(Collection availableTargetNames, PreciseShardingValue shardingValue) {
		// TODO Auto-generated method stub
		return null;
	}
}