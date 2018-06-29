package com.topaiebiz.member.point.utils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.math.BigDecimal.ROUND_DOWN;

/**
 * Created by ward on 2018-02-26.
 */
public class BatchCreateTable {


    // JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://rm-bp11c669x6gey5f7oo.mysql.rds.aliyuncs.com:3306/motherbuy_log?characterEncoding=utf8";

    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "qianmi";
    static final String PASS = "jQ6mmyhDwLrmRXSv";

    public static void main(String[] args) {
        Integer autoBox = new Integer(1);

        Long mod = 973145653837017089L;
        System.out.println(mod % 128);


        BigDecimal payPrice = new BigDecimal(68.00);
        BigDecimal tempRate = new BigDecimal(1.119);
        BigDecimal goodsAmount = payPrice.divide(tempRate, 4, ROUND_DOWN);
        BigDecimal taxAmount = payPrice.subtract(goodsAmount);
        System.out.println(goodsAmount.toString() + "|" + taxAmount.toString());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String tempStr = formatter.format(new Date());

        System.out.println(tempStr);

        Long a = 20000L;
        Long b = new Long(20000);
        System.out.println(a.equals(b));
        System.out.println(a == b);
        System.out.println("-------------------------");

        System.out.println(new BigDecimal("100.990").stripTrailingZeros().toPlainString());
        Connection conn = null;
        Statement stmt = null;
        try {
            // 注册 JDBC 驱动
            Class.forName("com.mysql.jdbc.Driver");

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // 执行查询
            System.out.println(" 实例化Statement对...");
            stmt = conn.createStatement();
            String pointLog, balanceSql;
            //sql = "SELECT id, memberId FROM t_mem_member_point_log_1";


            boolean rs;
            for (Integer n = 0; n < 1; n++) {

                pointLog = "CREATE TABLE IF NOT EXISTS `t_mem_member_point_log_" + n + "` (\n" +
                        "  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,\n" +
                        "  `memberId` bigint(20) DEFAULT NULL COMMENT '会员ID\',\n" +
                        "  `userName` varchar(50) DEFAULT NULL COMMENT '用户名',\n" +
                        "  `telephone` varchar(11) DEFAULT NULL COMMENT '会员手机号',\n" +
                        "  `beforePoint` bigint(20) DEFAULT NULL COMMENT '变化前积分',\n" +
                        "  `pointChange` bigint(20) DEFAULT NULL COMMENT '积分变化额度 -表示减少+表示增加',\n" +
                        "  `afterPoint` bigint(20) DEFAULT NULL COMMENT '变化后积分',\n" +
                        "  `operateType` varchar(32) DEFAULT NULL COMMENT '积分变化的操作code',\n" +
                        "  `operateDesc` varchar(256) DEFAULT NULL COMMENT '操作说明',\n" +
                        "  `operateSn` varchar(128) DEFAULT NULL COMMENT '交易单号等唯一标示用于解决幂等性',\n" +
                        "  `meno` varchar(5000) DEFAULT NULL COMMENT '备注',\n" +
                        "  `creatorId` bigint(20) DEFAULT NULL COMMENT '创建人编号。取值为创建人的全局唯一主键标识符',\n" +
                        "  `createdTime` datetime DEFAULT NULL COMMENT '创建时间。取值为系统的当前时间',\n" +
                        "  `deletedFlag` tinyint(4) DEFAULT NULL COMMENT '逻辑删除标识。仅且仅有0和1两个值，1表示已经被逻辑删除，0表示正常可用。',\n" +
                        "  `version` bigint(20) DEFAULT NULL COMMENT '信息版本号。乐观锁机制的辅助字段，用于控制信息的一致性。',\n" +
                        "  PRIMARY KEY (`id`)\n" +
                        ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='会员积分明细记录';";

                balanceSql = "CREATE TABLE IF NOT EXISTS `t_mem_member_balance_log_" + n + "` (\n" +
                        "  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,\n" +
                        "  `memberId` bigint(20) DEFAULT NULL COMMENT '会员ID',\n" +
                        "  `userName` varchar(50) DEFAULT NULL COMMENT '用户名',\n" +
                        "  `telephone` varchar(11) DEFAULT NULL COMMENT '会员手机号',\n" +
                        "  `beforeBalance` decimal(10,2) DEFAULT NULL COMMENT '变化前余额',\n" +
                        "  `balanceChange` decimal(10,2) DEFAULT NULL COMMENT '余额变化额度 -表示减少+表示增加',\n" +
                        "  `afterBalance` decimal(10,2) DEFAULT NULL COMMENT '变化后余额',\n" +
                        "  `operateType` varchar(32) DEFAULT NULL COMMENT '余额变化的操作code',\n" +
                        "  `operateDesc` varchar(256) DEFAULT NULL COMMENT '操作说明',\n" +
                        "  `operateSn` varchar(128) DEFAULT NULL COMMENT '交易单号',\n" +
                        "  `meno` varchar(5000) DEFAULT NULL COMMENT '备注',\n" +
                        "  `creatorId` bigint(20) DEFAULT NULL COMMENT '创建人编号。取值为创建人的全局唯一主键标识符',\n" +
                        "  `createdTime` datetime DEFAULT NULL COMMENT '创建时间。取值为系统的当前时间',\n" +
                        "  `deletedFlag` tinyint(4) DEFAULT NULL COMMENT '逻辑删除标识。仅且仅有0和1两个值，1表示已经被逻辑删除，0表示正常可用。',\n" +
                        "  `version` bigint(20) DEFAULT NULL COMMENT '信息版本号。乐观锁机制的辅助字段，用于控制信息的一致性。',\n" +
                        "  PRIMARY KEY (`id`)\n" +
                        ") ENGINE=InnoDB AUTO_INCREMENT=956488901332320418 DEFAULT CHARSET=utf8;";

                rs = stmt.execute(pointLog);
                stmt.execute(balanceSql);
            }

            // 展开结果集数据库
           /* while (rs.next()) {
                // 通过字段检索
                long id = rs.getLong("id");
                String memberId = rs.getString("memberId");

                // 输出数据
                System.out.print("id: " + id);
                System.out.print(", memberId: " + memberId);
                System.out.print("\n");
            }*/
            // 完成后关闭
            //rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // 处理 JDBC 错误
            se.printStackTrace();
        } catch (Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }// 什么都不做
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
    }
}
