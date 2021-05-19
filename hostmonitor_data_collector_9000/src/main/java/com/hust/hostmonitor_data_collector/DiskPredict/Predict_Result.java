package com.hust.hostmonitor_data_collector.DiskPredict;
import ai.onnxruntime.OrtSession.Result;

/** 预测结果的数据类型 包括 硬件信息、预测结果
 * @author 62786
 * @date: 2020年12月7日 下午7:23:07
 */
public class Predict_Result {
	public String imei;	// imei
	public String model;
	public Result result;
	
	public Predict_Result(String imei, String model, Result result) {
		this.imei = imei;
		this.model = model;
		this.result = result;
	}
}
