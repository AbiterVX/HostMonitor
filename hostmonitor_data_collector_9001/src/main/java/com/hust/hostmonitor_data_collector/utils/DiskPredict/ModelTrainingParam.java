package com.hust.hostmonitor_data_collector.utils.DiskPredict;

import com.alibaba.fastjson.JSONObject;

/**
 * 模型训练对外接口-参数
 */
public class ModelTrainingParam {
    //预处理参数
    int replace;
    //生成模型训练数据参数
    float scale;
    float verifySize;
    //模型训练参数
    JSONObject trainParams;
    public ModelTrainingParam(int _replace, float _scale,float _verifySize, JSONObject _trainParams){
        replace = _replace;
        scale = _scale;
        verifySize = _verifySize;
        trainParams = _trainParams;
    }
}
