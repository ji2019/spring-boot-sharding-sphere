package com.standard;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import java.util.ArrayList;
import java.util.Collection;

import com.google.common.collect.Range;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;
/**
 * 
 * https://blog.csdn.net/weixin_44211703/article/details/107495008
 * 自定义数据库分表类
 */
public class ModuloShardingTableAlgorithm implements PreciseShardingAlgorithm<String>, RangeShardingAlgorithm<Long> {
    private int ModeID=1000;
    
	/**
	 * Collection<String> collection   配置文件中设置的表取值范围
	 * RangeShardingValue<Long> rangeShardingValue 查询条件中输入的查询范围
	 * 返回符合查询条件的表名称
	 * 
	 */
    @Override
    public Collection<String> doSharding( Collection<String> collection  ,  RangeShardingValue<Long> rangeShardingValue) {
    	//实现RangeShardingAlgorithm接口，在函数中定义分表逻辑，返回符合逻辑的表名称
        Range<Long> valueRange = rangeShardingValue.getValueRange();//获得输入的查询条件范围
        String slowerEndpoint = String.valueOf(valueRange.hasLowerBound()?valueRange.lowerEndpoint():"");//查询条件下限
        String supperEndpoint = String.valueOf(valueRange.hasUpperBound()?valueRange.upperEndpoint():"");//查询条件上限
        //处理只有下限或上限的范围
        long  lowerEndpoint=0;
        long  lupperEndpoint=0;
        if(!slowerEndpoint.isEmpty()&&!supperEndpoint.isEmpty()) {
            lowerEndpoint= Math.abs(Long.parseLong(slowerEndpoint));
            lupperEndpoint= Math.abs(Long.parseLong(supperEndpoint));
        } else if(slowerEndpoint.isEmpty()&&!supperEndpoint.isEmpty()) {
            lupperEndpoint= Math.abs(Long.parseLong(supperEndpoint));
            lowerEndpoint=lupperEndpoint-ModeID>0?lupperEndpoint-ModeID:0;
        }  
        else if(!slowerEndpoint.isEmpty()&&supperEndpoint.isEmpty()) {
            lowerEndpoint= Math.abs(Long.parseLong(slowerEndpoint));
            lupperEndpoint=lowerEndpoint+ModeID;
        }  
         
         Collection<String> collect = new ArrayList<>();
         //逐个读取查询范围slowerEndpoint~lupperEndpoint的值，进行32取余计算，获得对应的表名称
            for (long i = lowerEndpoint; i <= lupperEndpoint; i++) {
                for (String each : collection) {
                    if (each.endsWith("_"+i % Integer.valueOf( collection.size()) + "")) {
                        if(!collect.contains(each))
                        {
                            collect.add(each);
                        }
                    }
                }
            }
            return collect;
    }

 
/**
 * 
 * Collection<String> collection   配置文件中设置的表取值范围
 * PreciseShardingValue<String> preciseShardingValue)
 * 返回符合查询条件的表名称
 * 
 */
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<String> preciseShardingValue) {//实现PreciseShardingAlgorithm<String>，在函数中定义分表逻辑，返回符合逻辑的表名称
        // TODO Auto-generated method stub

            for (String each : collection) {
             {                    
                 String hashCode = String.valueOf(preciseShardingValue.getValue());//配置文件中，分表字段对应的值，也是查询条件中输入的查询条件
                    long segment = Math.abs(Long.parseLong(hashCode)) % 32;
                    if (each.endsWith("_"+segment + "")) {//  
                        return each;
                    }
                }
 
            }
            throw new RuntimeException(preciseShardingValue+"没有匹配到表");
    }

}