/*
 * @Descripttion: 
 * @Version: xxx
 * @Author: WanJu
 * @Date: 2021-06-25 15:33:51
 * @LastEditors: WanJu
 * @LastEditTime: 2021-07-02 12:17:01
 */
package com.data_escape.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExeCmd {
    private final static Logger logger = LoggerFactory.getLogger(ExeCmd.class);

    public static void executeCmd(String command[], String path, StringBuilder infoList, StringBuilder errorList) {
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            if (path == null)
                process = runtime.exec(command);
            else
                process = runtime.exec(command, null, new File(path));

            InputStream info = process.getInputStream();
            InputStream err = process.getErrorStream();
            new Thread(new InputStreamThread(info, infoList)).start();
            new Thread(new InputStreamThread(err, errorList)).start();
            // 等待所有子线程执行完毕
            process.waitFor();
        } catch (IOException e) {
            logger.error("[executeCmd][命令执行错误][cmd :{}{}]error info: {}", path, Arrays.toString(command), e.toString());
        } catch (InterruptedException e) {
            logger.error("[executeCmd][命令中断][cmd :{}{}]error info: {}", path, Arrays.toString(command), e.toString());
            e.printStackTrace();
        } finally {
            try {
                process.getOutputStream().close();
            } catch (Exception e) {
                logger.error("[executeCmd]error info: {}", e.toString());
            }
        }
    }

    static class InputStreamThread implements Runnable {
        private BufferedReader bufferedReader = null;
        private InputStream inputStream = null;
        private StringBuilder info = null;

        public InputStreamThread(InputStream inputStream, StringBuilder info) {
            this.inputStream = inputStream;
            this.info = info;
            this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        }

        @Override
        public void run() {
            String line = null;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    if (info != null)
                        info.append(line);
                    logger.info("[executeCmd]info: {}", line);
                }
            } catch (Exception e) {
                logger.error("[executeCmd]error: {}", e.toString());
            } finally {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    logger.error("[executeCmd]error: {}", e.toString());
                }
            }
        }
        
    }
}
