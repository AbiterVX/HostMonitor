/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-30 09:49:49
 * @LastEditors: WanJu
 * @LastEditTime: 2021-07-02 19:17:57
 */
package com.data_escape;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.data_escape.DataEscapeUtils.DstNode.ServerManager;
import com.data_escape.DiskUtils.DiskChecker;
import com.data_escape.DiskUtils.DiskPartition;
import com.data_escape.DiskUtils.DiskRecover;
import com.data_escape.DiskUtils.beans.LogicalDiskBean;
import com.data_escape.DiskUtils.beans.PhysicalDiskBean;

public class ServerAPP {
    // 可以把ServerManager的对象设为静态对象，方便调用
    public static ServerManager serverManager = null; 
    
    public static void main(String args[]) {
        List<PhysicalDiskBean> physicalDiskList = new ArrayList<>();
        PhysicalDiskBean srcDiskBean = null;
        PhysicalDiskBean dstDiskBean = null;
        
        String serverMenu = "\n###############################\n" +
                            "\n         0: 目标硬盘检查" +
                            "\n         1: 选择源硬盘与目的硬盘" +
                            "\n         2: 目标硬盘分区" +
                            "\n         3: 传输应答" +
                            "\n         4: 硬盘恢复" +
                            "\n        -1: 退出" +
                            "\n\n###############################";
        
        Scanner in = new Scanner(System.in);
        int cmd = 0;
        while (cmd != -1) {
            System.out.println(serverMenu);
            cmd = in.nextInt();
            switch (cmd) {
                case 0:
                    // 目标硬盘检查
                    // web端发请求给节点，查询目标节点的所有硬盘信息
                    // web端接收到返回信息 physicalDiskListJson
                    String physicalDiskListJson = DiskChecker.diskCheck();
                    // web端展示信息
                    if (physicalDiskListJson == null) {
                        System.out.println("########### 硬盘信息查询失败 ###########");
                    } else {
                        // physicalDiskList 可以直接传给网页端
                        JSONArray jsonArray = JSON.parseArray(physicalDiskListJson);
                        // 以下处理不必要
                        physicalDiskList.clear();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            physicalDiskList.add(jsonObject.toJavaObject(PhysicalDiskBean.class));
                        }
                        System.out.println("\n########### 硬盘信息如下 ###########\n" + DiskChecker.prettyJson(physicalDiskList));
                    }
                    
                    // ********************************************************
                    // 这里需要 web端 进行验证，此时已经获取到节点的全部硬盘信息
                    // 验证源节点的硬盘能否迁移到目的节点的硬盘，简单的容量大小比较
                    // 进行下一步的时候必须是通过验证的源节点硬盘和目的节点硬盘
                    // case 1中给出一个粗略的案例
                    // ********************************************************
                    break;
                    
                case 1:
                    System.out.println("====> 请选择源硬盘（故障盘）（SN）:");
                    in.nextLine();
                    String srcSN = in.nextLine();
                    System.out.println("====> 请选择目的硬盘（需要转移到的盘）（SN）:");
                    String dstSN = in.nextLine();

                    for (PhysicalDiskBean diskBean : physicalDiskList) {
                        if (diskBean.getSn().equals(srcSN)) {
                            srcDiskBean = diskBean;
                        }
                        if (diskBean.getSn().equals(dstSN)) {
                            dstDiskBean = diskBean;
                        }
                    }

                    // 检查这两个硬盘的大小
                    if (dstDiskBean.getTotal() <= srcDiskBean.getUsed()) {
                        // 如果目的硬盘的整体容量小于源硬盘里的数据总量，则不能在二者间进行数据迁移
                        System.out.println("########### 目的硬盘容量过小，请重新选择 ###########");
                    } else {
                        System.out.println("########### 容量检查无误，可以转移 ###########");
                    }

                    break;

                case 2:
                    in.nextLine();
                    // 在 web段要求用户输入分区信息，类似其它分区软件，注意盘符不能重复
                    // 这时有源硬盘的信息，可以让用户根据原硬盘信息进行设置，注意分区大小的问题
                    // 应为上一步已经选择了目的硬盘，所以直接对它进行分区即可
                    System.out.println("########### 要分区的硬盘如下，会格式化该硬盘，须谨慎操作 ###########");
                    System.out.println(DiskChecker.prettyJson(dstDiskBean));
                    System.out.println("====> 请设置分区格式（GPT / MBR）:");
                    String partitonType = in.nextLine();
                    dstDiskBean.setType(partitonType);
                    // 接下来就是设置分区
                    List<LogicalDiskBean> lDiskList = srcDiskBean.getLogicalDrives();
                    List<LogicalDiskBean> dstLDiskList = new ArrayList<>();
                    for (LogicalDiskBean lBean : lDiskList) {
                        LogicalDiskBean lDiskBean = new LogicalDiskBean();
                        System.out.println("########### 源硬盘分区信息如下 ###########");
                        System.out.println(DiskChecker.prettyJson(lBean));
                        System.out.println("====> 选择分区类型（Primary: 0 / Logical: 1）:");
                        int isPrimary = in.nextInt();
                        System.out.println("====> Bootable: 0 / UnBootable: 1）:");
                        int isBoot = in.nextInt();
                        in.nextLine();
                        System.out.println("====> 请设置盘符（不能选择重复盘符）:");
                        String cpation = in.nextLine();
                        System.out.println("====> 请设置分区名称:");
                        String name = in.nextLine();
                        System.out.println("====> 请设置文件系统（NTFS / FAT32）:");
                        String fileSystem = in.nextLine();
                        System.out.println("====> 请设置大小，（单位：Byte）:");
                        long capacity = in.nextLong();
                        in.nextLine();
                        lDiskBean.setPrimaryPartition(isPrimary == 0);
                        lDiskBean.setBootPartition(isBoot == 0);
                        // lDiskBean.setBootable(isBootVolume == 0);
                        lDiskBean.setCaption(cpation);
                        lDiskBean.setName(name);
                        lDiskBean.setFileSystem(fileSystem);
                        lDiskBean.setTotal(capacity);
                        dstLDiskList.add(lDiskBean);
                    }
                    dstDiskBean.setLogicalDrives(dstLDiskList);

                    // 在目的硬盘上进行分区，此步骤会格式化目的硬盘导致数据丢失，须谨慎操作
                    DiskPartition.diskPartition(JSON.toJSONString(dstDiskBean));
                    // 分区完之后可以刷新一遍目标节点的硬盘信息，顺便检查分区是否成功
                    break;

                case 3:
                    in.nextLine();
                    // 这一步在 web端完成，先于源节点的传输请求启动
                    System.out.println("====> 请输入 IP:");
                    String serverIP = in.nextLine();
                    System.out.println("====> 请输入 端口:");
                    int serverPort = in.nextInt();
                    // 缓冲区的大小必须比源节点的文件片大小大,可以通过算法设置不必用户输入
                    System.out.println("====> 请输入缓冲区大小 默认20 单位:M:");
                    int fileShardSize = in.nextInt();
                    System.out.println("====> 请输文件保存位置（盘符 / 根目录）");
                    in.nextLine();
                    String fileSavePath = in.nextLine();
                    ServerManager serverManager = new ServerManager(serverIP, serverPort, fileShardSize, fileSavePath);
                    serverManager.serverStart();
                    // 启动服务之后再启动客户端
                    // 文件传输是全自动的操作
                    break;

                case 4:
                    System.out.println("########### 硬盘镜像文件将解压到 ###########");
                    System.out.println(dstDiskBean.toString());

                    // 这里存在一个映射关系，因为源硬盘分区与目的硬盘分区的盘符不一样
                    // 不过在分区的时候这个映射关系已经确定了
                    System.out.println("########### 分区映射关系如下所示 ###########");
                    List<LogicalDiskBean> srcLDiskBeans = srcDiskBean.getLogicalDrives();
                    List<LogicalDiskBean> dstLDiskBeans = dstDiskBean.getLogicalDrives();
                    for (int i = 0; i < srcLDiskBeans.size(); i++) {
                        System.out.println(srcLDiskBeans.get(i).toString() + "\n<===== 对应 =====>\n" + dstLDiskBeans.get(i).toString());
                    }
                    DiskRecover.diskRecover(JSON.toJSONString(srcDiskBean), JSON.toJSONString(dstDiskBean),
                    "E:\\dataescape_recv_tmp\\fa6641458842b7ec9cb0ddc621cd6b2e.wim");
                    break;
                default:
                    break;
            }
        }
        in.close();
    }
}
