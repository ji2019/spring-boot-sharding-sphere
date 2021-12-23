package com.standard;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.collect.Range;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;
import org.springframework.util.StringUtils;
/**
 * https://blog.csdn.net/weixin_44211703/article/details/107495008
 * 自定义数据库分库类
 */
public class ModuloShardingDatabaseAlgorithm implements PreciseShardingAlgorithm<String>, RangeShardingAlgorithm<Long> { 
	
	//继承PreciseShardingAlgorithm接口，RangeShardingAlgorithm接口，处理范围查询
  

 private int ModeID=1000;//默认取值范围。如果只设置了>20000没有设置 最大值，此时默认设置一个最大值：最小值+1000。反之设置了最大值，没有设置最小值，就是最大值-1000
 
	/**
	 * 
	 * Collection<String> databaseNamescollection ：配置文件中数据库的数节点范围
	 * RangeShardingValue<Long> rangeShardingValue ：查询语句中查询范围
	 * 返回符合查询条件的数据库节点名称
	 * 
	 */
    @Override
    public Collection<String> doSharding(Collection<String> databaseNamescollection, RangeShardingValue<Long> rangeShardingValue) {//实现RangeShardingAlgorithm接口，在接口中实现逻辑，返回符合逻辑的数据库名称
           Collection<String> collect = new ArrayList<>();//返回数据库节点名称list
            Range<Long> valueRange = rangeShardingValue.getValueRange();//获取查询条件中范围值
            String slowerEndpoint = String.valueOf(valueRange.hasLowerBound()?valueRange.lowerEndpoint():"");//查询下限值
            String supperEndpoint =  String.valueOf(valueRange.hasUpperBound()?valueRange.upperEndpoint():"");//查询上限值
         //判断上限，下限值是否存在，如果不存在赋给默认值。用于处理查询条件中只有 >或<一个条件，不是一个范围查询的情况
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

            //逐个取slowerEndpoint ~supperEndpoint 之间的值，根据分库规则对该值先对32取余，将结果再对4取余，获得的数字就是数据库节点ID，然后返回这个数据库节点ID
            for (Long i = lowerEndpoint; i <=  lupperEndpoint; i++) {//传递的查询范围值，逐个值进行计算
                for (String each : databaseNamescollection) {//配置文件中表配置中设置的数据库节点范围去个读取，{0..3}逐个读取
                    if (each.endsWith(Long.valueOf((i %Long.valueOf(32)))% Long.valueOf(databaseNamescollection.size()) + "")) {//从当前范围取一个值，对32取模，结果再对4取模。databaseNamescollection是配置文件中节点数量，节点配置为（0...3），所以这里会是4
                        if(!collect.contains(each))
                        {//将当前取值对应的数据库节点存入返回list中
                            collect.add(each);
                        }
                    }
                }
            }

            return collect;//返回当前范围对应的节点列表
    }

	/**
	 * 
	 * Collection<String> collection ：配置文件中数据库的数节点范围
	 * PreciseShardingValue<String> preciseShardingValue ：查询语句中查询值
	 * 返回符合查询条件的数据库名称
	 * 
	 */
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<String> preciseShardingValue) {//实现PreciseShardingAlgorithm接口，处理=查询，在函数中处理逻辑，
        String databaseName = "";
            for (String each : collection) {//逐个读取数据库节点，然后根据传递的节点值根据分库分表逻辑（对32取余后再对4取余），获得当前查询需要访问的数据库节点名称并返回
                Long l32 = new Long((long)32);
                Long ldb= new Long((long)collection.size());
                String hashCode = String.valueOf(preciseShardingValue.getValue());
                long segment = Math.abs(Long.parseLong(hashCode)) % 32%4;
                if (each.endsWith(segment + "")) {
                    databaseName = each;
                    break;
                }
            }
        if (StringUtils.hasText(databaseName)) {
            return databaseName;
        }
        throw new UnsupportedOperationException();
    }

}