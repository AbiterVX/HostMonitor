/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-30 09:43:36
 * @LastEditors: WanJu
 * @LastEditTime: 2021-07-02 19:17:00
 */
package com.data_escape;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.data_escape.DataEscapeUtils.SrcNode.ClientManager;
import com.data_escape.DiskUtils.DiskChecker;
import com.data_escape.DiskUtils.DiskZipper;
import com.data_escape.DiskUtils.beans.ImageFileBean;
import com.data_escape.DiskUtils.beans.PhysicalDiskBean;
import com.data_escape.DiskUtils.cmds.DISM;

/**
 * Hello world!
 *
 */
public class ClientApp 
{
    // 可以把ClientManager的对象设为静态对象，方便调用
    public static ClientManager clientManager = null; 
    public static void main( String[] args )
    {
        List<PhysicalDiskBean> physicalDiskList = new ArrayList<>();
        ImageFileBean imageFileBean = null;
        

        String clientMenu = "\n#################################\n" +
                            "\n         0: 源硬盘检查" +
                            "\n         1: 源硬盘压缩" +
                            "\n         2: 传输请求" +
                            "\n        -1: 退出" +
                            "\n\n###############################\n";
        
        Scanner in = new Scanner(System.in);
        int cmd = 0;
        while (cmd != -1) {
            System.out.println(clientMenu);
            cmd = in.nextInt();
            switch (cmd) {
                case 0:
                    // 源硬盘检查
                    // web端发请求给节点，查询所有硬盘信息
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
                    // ********************************************************
                    break;
                    
                case 1:
                    // 源硬盘压缩
                    // 首先需要选择要压缩的硬盘，以及压缩文件暂存的位置，这里只提供盘符供选择
                    // 以下逻辑需要 web 端进行实现
                    // 选择需要压缩的硬盘（故障硬盘）
                    System.out.println("====> 选择需要备份迁移的故障硬盘（SN）:");
                    in.nextLine();
                    String srcSN = in.nextLine();
                    PhysicalDiskBean pDiskBean = null;
                    for (PhysicalDiskBean diskBean : physicalDiskList) {
                        if (diskBean.getSn().equals(srcSN)) {
                            pDiskBean = diskBean;
                            break;
                        }
                    }
                    if (pDiskBean == null) {
                        System.out.println("########### 没有匹配的 SN 码 ###########");
                        break;
                    }
                    // 这里还需要检查容量是否足够，压缩的比例约为0.9，由 web端 完成
                    System.err.println("====> 选择备份文件的暂存位置（盘符、根目录）:");
                    String srcCapation = in.nextLine();
                    
                    // 向节点发出请求，开始备份:
                    // 备份完成后返回服务器
                    String imageFileJson = DiskZipper.diskZip(JSON.toJSONString(pDiskBean), srcCapation);

                    // web端接收到返回信息，展示结果
                    if (imageFileJson == null) {
                        System.out.println("########### 硬盘压缩失败 ###########");
                        break;
                    } else {
                        // 展示结果
                        imageFileBean = JSON.parseObject(imageFileJson, ImageFileBean.class);
                        System.out.println("\n########### 压缩后的硬盘镜像文件如下 ###########\n" + DiskChecker.prettyJson(imageFileBean));
                        System.out.println("\n########### 压缩后的硬盘镜像文件内容如下 ###########\n" + DISM.getInfoFromImage(imageFileBean.getPath()));
                    }

                    break;

                case 2:
                    // 传输请求
                    // 这里是源节点 srcNode，需要将硬盘镜像文件传到目的节点 dstNode
                    // ********************************************************
                    // 这里需要 web端 进行验证
                    // 验证所选的目标节点的存储路径是否足够的空间存储硬盘镜像文件，简单的大小比较
                    // ********************************************************
                    // web端将 dstNode 的ip，port传给源节点，同时也传出 imageFileJson
                    in.nextLine();
                    System.out.println("====> 请输入目标节点的 IP:");
                    String serverIP = in.nextLine();
                    System.out.println("====> 请输入目标节点的 端口:");
                    int serverPort = in.nextInt();
                    System.out.println("====> 请输入文件片大小 默认20 单位:M:");
                    int fileShardSize = 20;
                    fileShardSize = in.nextInt();
                    // 连接开始，此时必须先启动服务器
                    clientManager = new ClientManager(serverIP, serverPort, JSON.toJSONString(imageFileBean), fileShardSize);
                    clientManager.clientStart();
                    // 当文件传输完毕后，源节点的业务流程就已经全部结束了
                    break;

                default:
                    break;
            }
        }
        in.close();
    }

}
