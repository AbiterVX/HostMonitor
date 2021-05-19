package DP;

import java.io.IOException;
import ai.onnxruntime.OrtException;

/**
 * @author 62786
 * @date: 2020年12月7日 上午11:12:12
 */
public class Disk_Predict {
	public Disk_Predict() {
		super();
	}

	public Disk_Predict(String path_input, String path_output) {
		// 数据没有标签列时 TEST必须 = false
		Model_tools.TEST = false;
		// 读取数据 执行模型

		Model_tools.set_PATH_INPUT(path_input);
		Model_tools.set_PATH_OUTPUT(path_output);

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
		// 数据没有标签列时 TEST必须 = false
		Model_tools.TEST = false;
		// 读取数据 执行模型

		Model_tools.set_PATH_INPUT(new String("./file/input/testInput.csv"));
		Model_tools.set_PATH_OUTPUT(new String("./file/output/testOutput.csv"));

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

}