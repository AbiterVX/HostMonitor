package com.hust.hostmonitor_data_collector.utils.DiskPredict;
/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-05-20 09:49:56
 * @LastEditors: WanJu
 * @LastEditTime: 2021-05-24 20:40:03
 */

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class JavaExePython {
    //进度前缀
    private static final String progressPrefix = "Progress:";
    //结果前缀
    private static final String resultDataPrefix = "PythonResultData:";


    public static void execPython(String filePath, JSONObject param, DiskPredictProgress progress){
        // filePath :待执行的python文件
        // param :以JSON格式组织参数
        try {
            Process proc;
            Runtime runtime = Runtime.getRuntime();
            List<String> cmd = new ArrayList<String>();
            cmd.add("python3");
            cmd.add("-u");  // 实时显示Python输出
            cmd.add(filePath);  // 待执行的Python文件 这是sys.argv[0]
            cmd.add(param.toJSONString());  // 需要执行的python参数 这是sys.argv[1]
            proc = runtime.exec(cmd.toArray(new String[0]));
            
            Thread thread = writeBackMsg(proc.getInputStream(), proc.getErrorStream(), progress);
            thread.join();
            
            proc.waitFor();
        }catch (InterruptedException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    
    private static Thread writeBackMsg(final InputStream in, final InputStream err, DiskPredictProgress progress){
        Thread thread = new Thread(new Runnable(){

            @Override
            public void run() {
                Reader inReader = new InputStreamReader(in);
                BufferedReader buffRead = new BufferedReader(inReader);

                Reader errReader = new InputStreamReader(err);
                BufferedReader buffErr = new BufferedReader(errReader);

                String line = null, errline = null;
                try {
                    while ((line = buffRead.readLine()) != null || (errline = buffErr.readLine()) != null){
                        if (line != null){
                            // 写回网页
                            System.out.println("[Python]:" + line);

                            //进度条
                            if(progress!=null){
                                if(line.startsWith(progressPrefix)){
                                    String progressNum = line.substring(progressPrefix.length());
                                    String[] progressData = progressNum.split("/");
                                    progress.setCurrentProgress(Integer.parseInt(progressData[0]),Integer.parseInt(progressData[1]));
                                }
                                else if(line.startsWith(resultDataPrefix)){
                                    String resultDataRaw = line.substring(resultDataPrefix.length());
                                    progress.setResultData(resultDataRaw);
                                    System.out.println(progress.getResultData());
                                }
                            }
                        }

                        if (errline != null){
                            // 写回网页
                            System.out.println("[Python]:" + errline);
                        }
                        line = errline = null;
                    }
                    buffErr.close();
                    buffRead.close();
                }
                catch (IOException e)
                {   
                    System.out.println("Error when read");
                    e.printStackTrace();
                    try
                    {
                        buffErr.close();
                        buffRead.close();
                    }
                    catch (IOException e1)
                    {
                        System.out.println("Error when close");
                        e1.printStackTrace();
                    }
                }
                
            }
            
        });
        thread.start();
        return thread;
    }
}