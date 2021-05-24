package com.hust.hostmonitor_data_collector.DiskPredict;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import ai.onnxruntime.OrtException;
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import org.json.JSONArray;

/**
 * @author 62786
 * @date: 2020年12月7日 上午11:12:12
 */
public class Disk_Predict {
	public Disk_Predict() {
		super();
	}

	public Disk_Predict(String inputFileName, String outputFileName) {
		// 数据没有标签列时 TEST必须 = false
		Model_tools.TEST = false;
		// 读取数据 执行模型
		String path = System.getProperty("user.dir") + "/DiskPredictData";
		Model_tools.set_PATH_INPUT(path + "/input/" +inputFileName);
		Model_tools.set_PATH_OUTPUT(path + "/output/" +outputFileName);

		Disk_Predict onnx_Model = new Disk_Predict(Model_tools.PATH_MODEL, Model_tools.PATH_CONFIG,
				Model_tools.PATH_INPUT);
		try {
			// 打印结果
			onnx_Model.print_result();
			onnx_Model.evaluate();
			// 写回结果
			onnx_Model.write_back();
		} catch (OrtException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Disk_Predict(String path_model, String path_conf, String path_data) {
		try {
			// 先读取模型
			Model_tools.load_Model(path_model);
			// 再读取配置文件
			Model_tools.load_Config(path_conf);
			// 读取数据 并 开始预测
			Model_tools.loadData_run(path_data, null);
		} catch (OrtException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 打印结果
	 * 
	 * @throws OrtException
	 * @throws IOException
	 * @author: 62786
	 * @date: 2020年12月7日 上午11:15:16
	 */
	public void print_result() throws OrtException, IOException {
		Model_tools.print_result();
	}

	/**
	 * 写回结果
	 * 
	 * @throws OrtException
	 * @throws IOException
	 * @author: 62786
	 * @date: 2020年12月7日 上午11:15:19
	 */
	public void write_back() throws IOException, OrtException {
		Model_tools.write_back(Model_tools.PATH_INPUT, Model_tools.PATH_OUTPUT);
	}

	/**
	 * 模型评估
	 * 
	 * @throws OrtException
	 * @throws IOException
	 * @author: 62786
	 * @date: 2020年12月7日 上午11:15:21
	 */
	public void evaluate() throws OrtException {
		Model_tools.evaluate();
	}


	//获取磁盘预测结果
	public static void getDiskPredictResult(String readFileName){
		try {
			String projectPath = System.getProperty("user.dir");
			CsvReader reader = new CsvReader(projectPath + readFileName, ',', StandardCharsets.UTF_8);
			reader.readHeaders();
			while (reader.readRecord()) {
				String[] currentRow = reader.getValues();
				//字段
				String hostName = currentRow[2];
				String diskName = currentRow[3];
				float probability = Float.parseFloat(currentRow[136]);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//对采样的磁盘预测数据整合
	public static void diskSampleDataIntegration(String writeFileName, List<String[]> contentData){
		String projectPath = System.getProperty("user.dir");
		try {
			//写表头
			String titles = "";
			CsvReader titleReader = new CsvReader(projectPath+ "/ConfigData/Client/title.csv", ',', StandardCharsets.UTF_8);
			if (titleReader.readRecord()) {
				titles = titleReader.getRawRecord();
			}
			titleReader.close();
			//写CSV文件
			FileWriter writer;
			writer = new FileWriter(projectPath+writeFileName);
			writer.write(titles+"\n");

			for (String[] currentContent:contentData){
				for(int j=0;j<currentContent.length;j++){
					writer.write(currentContent[j]+"\n");
				}
			}

			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		//Disk_Predict onnx_Model = new Disk_Predict("testInput.csv", "testOutput.csv");

		//Disk_Predict.readCSV("/DiskPredictData/input/input.csv");

		/*
		List<String[]> contentData = new ArrayList<>();
		contentData.add(new String[]{"615001001,2020/10/5 8:41,WRIGHT,$$+Pdygyg$lJj9j1vv2MvDUC4IJWEX1hJXyU5ULV5n8=,WDC PC SN720 SDAPNTW-512G-1127,NVM Express,10126000,1,512056,,,0,318,100,10,0,\"1,227,352\",\"1,507,992\",\"19,519,866\",\"26,869,868\",47,328,75,10,0,0,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,20201005"});
		contentData.add(new String[]{"615001001,2020/10/5 8:39,BOHRL,$$$VeUFTzQo2neKVbB5$PKQNyIJF+TPeqTzB3r9eWpM=,SAMSUNG MZVLB512HBJQ-00000,NVM Express,EXF7201Q,1,512056,,,0,317,100,10,0,\"3,054,732\",\"2,928,834\",\"71,261,572\",\"102,382,682\",58,274,77,4,0,845,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,20201005",
				"615001001,2020/10/5 14:44,KELVINC,$$+NCs7EBxP080wdnLpCQpN6XJSC4sz+H4SAU++izxE=,WDC PC SN730 SDBPNTY-512G-1027,NVM Express,11110000,1,512056,PCIe 3.0 x4,PCIe 3.0 x4,0,311,100,10,0,\"4,212,856\",\"3,242,361\",\"66,390,250\",\"41,243,761\",100,268,86,14,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,20201005"});
		Disk_Predict.diskSampleDataIntegration("/DiskPredictData/input/input.csv",contentData);
		*/

		/*
		java 调用 python
		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.exec("a=[5,2,3,9,4,0]; ");
		interpreter.exec("print(sorted(a));");  //此处python语句是3.x版本的语法
		interpreter.exec("print sorted(a);");   //此处是python语句是2.*/



		//https://blog.csdn.net/qq_26591517/article/details/80441540
		//interpreter.execfile("D:\\add.py");


		/*
		// 数据没有标签列时 TEST必须 = false
		Model_tools.TEST = false;
		// 读取数据 执行模型
		Model_tools.set_PATH_INPUT(new String(path + "/input/testInput.csv"));
		Model_tools.set_PATH_OUTPUT(new String(path + "/output/testOutput.csv"));
		Disk_Predict onnx_Model = new Disk_Predict(Model_tools.PATH_MODEL, Model_tools.PATH_CONFIG,
				Model_tools.PATH_INPUT);*/
		/*try {
			// 打印结果
			onnx_Model.print_result();
			onnx_Model.evaluate();
			// 写回结果
			onnx_Model.write_back();
		} catch (OrtException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

}
