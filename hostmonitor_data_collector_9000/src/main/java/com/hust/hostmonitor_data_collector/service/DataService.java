package com.hust.hostmonitor_data_collector.service;


/*
 * 数据Service
 *  用于直接与HostMonitorBatchExecution交互，数据库存取等操作。
 *  DataSampleController会调用DataService的接口获取数据。
 *  接口返回格式为JSON
 */
public interface DataService {

    /**
     * 功能：获取所有Host的IP
     * 格式：["0.0.0.0","0.0.0.1" ... ]
     */
    String getHostIp();

    /**
     * 功能：获取所有Host的连接状态
     * 其他：每次采样时会更新session连接情况，0为无法连接，1为正常。
     * 格式：[1, 0, ... ]
     */
    String getHostState();

    /**
     * 功能：获取所有Host的硬件信息
     * 格式：[{},{},...]
     */
    String getHostHardwareInfo();

    /**
     * 功能：获取Host信息-实时
     * 格式：[{"ip":"0.0.0.0",...},{"ip":"0.0.0.1",...},...]
     */
    String getHostInfoRealTime();

    /**
     * 功能：获取Host信息-近期
     * 参数：
     *      index：配置文件中对应ip的索引，从0开始。
     *      hour：小时数
     * 格式：[{"ip":"0.0.0.0",...},{"ip":"0.0.0.1",...},...]
     */
    String getHostInfoRecent(int index,int hour);

    /**
     * 功能：获取Host信息-某字段
     * 参数：
     *      index：配置文件中对应ip的索引，从0开始。
     *      hour：小时数
     *      field：查询字段
     * 格式：[{"ip":"0.0.0.0",...},{"ip":"0.0.0.1",...},...]
     */
    String getHostInfoField(int index,int hour,HostInfoFieldType field);

}
