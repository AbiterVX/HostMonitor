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
    int[] max_depth;
    int[] max_features;
    int[] n_estimators;
    public ModelTrainingParam(int _replace,
                              float _scale,float _verifySize,
                              int[] _max_depth,int[] _max_features,int[] _n_estimators){
        replace = _replace;
        scale = _scale;
        verifySize = _verifySize;

        max_depth = _max_depth;
        max_features = _max_features;
        n_estimators = _n_estimators;

        trainParams = new JSONObject();
        trainParams.put("max_depth", max_depth);
        trainParams.put("max_features", max_features);
        trainParams.put("n_estimators", n_estimators);
    }

}
