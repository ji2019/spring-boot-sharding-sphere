//package com.oujiong.config;
//
//import java.beans.beancontext.BeanContext;
//import java.lang.reflect.Field;
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.shardingsphere.api.sharding.ShardingValue;
//import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
//
//import com.google.common.collect.Range;
//
//import lombok.SneakyThrows;
//
///**
// * @author gufusheng
// * @time 2021/7/19 19:46
// */
//public class ShardingAlgorithm implements ComplexKeysShardingAlgorithm {
//
//    @SneakyThrows
//    @Override
//    public Collection<String> doSharding(Collection<String> collection, Collection<ShardingValue> collection1) {
//        SchoolTermService schoolTermService = BeanContext.getBean(SchoolTermService.class);
//        List<String> result = new ArrayList<>();
//        for (ShardingValue shardingValue : collection1) {
//            Class clazz = shardingValue.getClass();
//
//            Field[] fields = clazz.getDeclaredFields();
//            Map<String, Object> map = new HashMap<>();
//            for (Field field : fields) {
//                //打开私有访问
//                field.setAccessible(true);
//                String name = field.getName();
//                Object value = field.get(shardingValue);
//                map.put(name, value);
//            }
//
//            if (StringUtils.equals((String) map.get("columnName"), "active_time")) {
//                Timestamp time = null;
//                if (map.containsKey("valueRange")) { // active_time可能为范围查询，此条件判断针对该情况
//                    Range range = (Range) map.get("valueRange");
//                    time = (Timestamp) range.lowerEndpoint();
//                } else {
//                    List list = (LinkedList) map.get("values");
//                    time = (Timestamp) list.get(0);
//                }
//                List<SchoolTerm> list = schoolTermService.getList();
//                StringBuilder tableName = new StringBuilder();
//                tableName.append(map.get("logicTableName"));
//                for (SchoolTerm term : list) {
//                    if (DateUtil.isIn(time, term.getTermStartDate(), term.getTermEndDate())) {
//                        String name;
//                        if (term.getIsLast().equals(1)) {
//                            // 当前学期时间，则拼接当前表
//                            name = "";
//                        } else {
//                            name = term.getTerm().substring(2, 4) + term.getTerm().substring(7, 9) + term.getTerm().substring(9);
//                        }
//                        tableName.append(name);
//                    }
//                }
//                result.clear();
//                result.add(tableName.toString());
//                break;
//            } else {
//                // 不带时间字段时，查询默认表，即最新学年历史轨迹表
//                StringBuffer tableName = new StringBuffer();
//                String name = "";
//                tableName.append(map.get("logicTableName"))
//                        .append(name);
//                result.add(tableName.toString());
//            }
//        }
//        System.out.println("进入分表设计");
//        result.forEach(System.out::println);
//        return result;
//    }
//}
