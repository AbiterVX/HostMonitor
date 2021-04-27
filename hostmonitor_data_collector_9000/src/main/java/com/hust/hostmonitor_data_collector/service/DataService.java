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
     * 格式：[{"os":"Ubuntu...",
     *        "MemorySize":2048,
     *        "CpuType":"xx type",
     *        "DiskTotalSize":1024000,},
     *       {},...]
     */
    String getHostHardwareInfo();

    /**
     * 功能：获取Host信息-实时
     * 格式：[{"CpuUsage":10,
     *        "MemoryUsage":11,
     *        "DiskOccupancyUsage":12,
     *        "MemoryUsage":11,
     *        "Disk":{
     *            "vda":{
     *                "Util":44
     *                "Iops":5.1
     *                "Read":89.2
     *                "Write":22.5
     *            },
     *            ...
     *        },
     *        "NetSend":200,
     *        "NetReceive":300,
     *        "TcpEstablished":16,
     *        "Temperature":{
     *            "1": 66,
     *            ...
     *        },
     *        "Power":178,
     *       },
     *       ...]
     */
    String getHostInfoRealTime();

    /**
     * 功能：获取Host信息-近期
     * 参数：
     *      index：配置文件中对应ip的索引，从0开始。
     *      hour：小时数
     * 格式：[{"TimeStamp":1619319687,
     *        "CpuUsage":10,
     *        "MemoryUsage":11,
     *        "DiskOccupancyUsage":12,
     *        "MemoryUsage":11,
     *        "Disk":{
     *            "vda":{
     *                "Util":44
     *                "Iops":5.1
     *                "Read":89.2
     *                "Write":22.5
     *            },
     *            ...
     *        },
     *        "NetSend":200,
     *        "NetReceive":300,
     *        "TcpEstablished":16,
     *        "Temperature":{
     *            "1": 66,
     *            ...
     *        },
     *        "Power":178,
     *       },
     *       ...]
     */
    String getHostInfoRecent(int index,int hour);

    /**
     * 功能：获取Host信息-某字段
     * 参数：
     *      index：配置文件中对应ip的索引，从0开始。
     *      hour：小时数
     *      field：查询字段
     * 格式：[{"TimeStamp":1619319687,
     *        "CpuUsage":10,
     *        "某字段": xxx,
     *       },
     *       ...]
     */
    String getHostInfoField(int index,int hour,HostInfoFieldType field);


    /**
     * 功能：获取Host 进程信息-最近
     * 参数：
     *      index：配置文件中对应ip的索引，从0开始。
     *
     * 格式：[{"uid":1,
     *        "pid":12,
     *        "readKbps": 452.7,
     *        "writeKbps": 142.3,
     *        "command": “java”,
     *       },
     *       ...]
     */
    String getHostProcessInfoRealTime(int index);


    /**
     * 功能：获取Host IO测试-信息-最近
     * 参数：
     * 格式：[{"IOSpeed":169,
     *       "UploadSpeed":5.65,
     *       "DownloadSpeed":98.56},
     *       ...]
     */
    String getHostIOTestInfoRealTime();


}
