package com.oujiong.config.temp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Range;

import lombok.extern.slf4j.Slf4j;

@Slf4j
 public class LogShardingAlgorithm implements PreciseShardingAlgorithm, RangeShardingAlgorithm<Integer> {
	
    @Autowired
    private DataSource dataSource;
    
    
     /**
      * 缓存存在的表
      */
     private List<String> tables;

     private final String systemLogHead = "system_log_";

     private Boolean isLoad = false;

     @Override
     public String doSharding(Collection availableTargetNames, PreciseShardingValue shardingValue) {
         if (!isLoad) {
//             tables = DBUtil.getAllSystemLogTable();
             isLoad = true;
         }
         String target = shardingValue.getValue().toString();
         String year = target.substring(target.lastIndexOf("_") + 1, target.lastIndexOf("_") + 5);
         if (!tables.contains(systemLogHead + year)) {
             createLogTable(year);
             tables.add(year);
         }
         return shardingValue.getLogicTableName() + "_" + year;
     }

     private void createLogTable(String year) {
		
	}

	@Override
     public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Integer> shardingValue) {
         if (!isLoad) {
//             tables = DBUtil.getAllSystemLogTable();
             isLoad = true;
         }
         Collection<String> availables = new ArrayList<>();
         Range valueRange = shardingValue.getValueRange();
         for (String target : tables) {
             Integer shardValue = Integer.parseInt(target.substring(target.lastIndexOf("_") + 1, target.lastIndexOf("_") + 5));
             if (valueRange.hasLowerBound()) {
                 String lowerStr = valueRange.lowerEndpoint().toString();
                 Integer start = Integer.parseInt(lowerStr.substring(0, 4));
                 if (start - shardValue > 0) {
                     continue;
                 }
             }
             if (valueRange.hasUpperBound()) {
                 String upperStr = valueRange.upperEndpoint().toString();
                 Integer end = Integer.parseInt(upperStr.substring(0, 4));
                 if (end - shardValue < 0) {
                     continue;
                 }
             }
             availables.add(target);
         }
         return availables;
     }
 }