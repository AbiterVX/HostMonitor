package com.hust.hostmonitor_data_collector.DiskPredict;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import ai.onnxruntime.OrtException;
import com.csvreader.CsvReader;
import org.python.util.PythonInterpreter;

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

	public static void main(String[] args) {
		//Disk_Predict onnx_Model = new Disk_Predict("testInput.csv", "testOutput.csv");








		/*
		try {
			String commend =("cmd.exe /k winsat disk" );//该部分为你的cmd命令,此处为调用javac将Java文件编译出class文件

			Process pro = Runtime.getRuntime().exec(commend); //添加要进行的命令
			BufferedReader br = new BufferedReader(new InputStreamReader(pro.getInputStream())); //虽然cmd命令可以直接输出，但是通过IO流技术可以保证对数据进行一个缓冲。
			String msg = null;
			while ((msg = br.readLine()) != null) {
				System.out.println(msg);
			}
		} catch (IOException exception) {
			exception.printStackTrace();
			System.out.println("编译出错");
		}*/


		/*try {
			String csvFilePath = System.getProperty("user.dir") + "/DiskPredictData/input/testInput.csv";
			CsvReader reader = new CsvReader(csvFilePath, ',', Charset.forName("UTF-8"));
			reader.readHeaders();
			while (reader.readRecord()) {
				//System.out.println(reader.getRawRecord());
				System.out.println(reader.getValues()[0]);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/

		/*PythonInterpreter interpreter = new PythonInterpreter();
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