package DP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.OrtSession.SessionOptions;
import ai.onnxruntime.OrtSession.SessionOptions.OptLevel;

/**
 * @author 62786 模型读取/调用工具类
 */
public class Model_tools {
	// 一些配置参数
	// 路径
	public final static String PATH_ROOT = "./file/V_2.0/";
	public final static String PATH_MODEL = "models/";
	public final static String PATH_CONFIG = "conf/";
	public static String PATH_INPUT = "./file/input/input_20201005_10000_1.csv";
//	public final static String PATH_INPUT = "./file/input/input_20201005_10000_1.csv";
//	public final static String PATH_INPUT = "./file/input/input_20201001.csv";
//	public final static String PATH_INPUT = "./file/input/input_20201005xx.csv";

	public static String PATH_OUTPUT = "./file/output/result.csv";
	// 固有名称
	public final static String FEATURE = "feature";
	public final static String NORM = "norm";
	public final static String ARG_1 = "arg1";
	public final static String ARG_2 = "arg2";
	public final static String FIRM_WARE = "fw";
	public final static String TRANSFER_MODE = "tm";
	public final static String PREDICT_WINDOW = "pw";
	// 程序配置
	public final static Character SEP = ',';
	public final static String[] CONFIG_FILES = { "firmware.conf", "norm.conf", "RandomForestClassifier.conf",
			"transmodel.conf" };
	public final static String[] MODELS = { "SAMSUNG", "LITEON", "WDC", "TOSHIBA" };
	public final static String[] MODEL_NAME = { "RandomForestClassifier.onnx", "RandomForestClassifier.onnx",
			"RandomForestClassifier.onnx", "RandomForestClassifier.onnx" };
	public final static String DEFAULT_MODEL = "SAMSUNG";
	public final static String DEFAULT_MODEL_NAME = "OTHERS";

//  九、十月份数据
	public static Integer INDEX_LABEL = 0;
	public static Integer INDEX_INTERVAL = 1;
	public static Integer INDEX_IMEI = 3;
	public static Integer INDEX_MODEL = 4;
	public static Integer INDEX_FIRMWARE = 6;
	public static Integer INDEX_DISKSIZE = 8;
	public static Integer INDEX_TRANSFER_MAX = 9;
	public static Integer INDEX_TRANSFER_CUR = 10;
	public static Integer NUM_SAMRT = 48;
	public static Integer NUM_WE = 14;
	public static Integer NUM_BSOD = 23;

	// 全局变量
	// 根据不同的厂商读取配置信息
	private static Map<String, Map<String, String>> firmware_tableMap = new HashMap<>();
	private static Map<String, Map<String, String>> transfermode_tableMap = new HashMap<>();
	private static Map<String, List<Integer>> featuresMap = new HashMap<>();
	private static Map<String, List<Double>> arg1Map = new HashMap<>();
	private static Map<String, List<Double>> arg2Map = new HashMap<>();
	private static Map<String, Integer> normMap = new HashMap<>();

	// 模型
	private static Map<String, OrtSession> Models = new HashMap<>();
	// 配置文件
	private static Map<String, Map<String, List<String>>> Configs = new HashMap<>();
	// 真实标签
	private static List<Integer> True_Label = new ArrayList<>();
	// 预测结果
	private static List<Predict_Result> Result = new ArrayList<>();

	// 缺失的 Firmware
	public static Integer Lost_Firmware = 0;
	public static Integer Show_Lost_Firmware = 6;
	public static Map<String, Integer[]> Default_Firmware = new HashMap<>();
	// 缺失的 Transfer_mode
	public static Integer Lost_Transfer_mode = 0;
	//
	public static Integer All_rows = 0;
	// 每个厂商的告警信息
	public static Map<String, Integer[]> Model_counts = new HashMap<>(); // 0表示总量，1表示错误盘，2表示正常盘
	// 初步特征筛选（剔除当天的值，保留累计值）
	private static List<Integer> feature_filter = new ArrayList<Integer>();

	// 是否是测试
	public static boolean TEST = false;
	// 将大文件分片的大小
	public final static Integer MAX_BYTE_LENGTH = 65536 * 100;
	// 线程的数量
	public final static Integer MAX_THREAD_NUM = 1;
	
	static public void set_PATH_INPUT(String path_input) {
		PATH_INPUT = path_input;
	}
	
	static public void set_PATH_OUTPUT(String path_output) {
		PATH_OUTPUT = path_output;
	}

	/**
	 * 读取模型 保存结果至 Models
	 * 
	 * @param model_path 模型路径
	 * @throws OrtException
	 * @author: 62786
	 * @date: 2020年12月7日 上午11:16:49
	 */
	static public void load_Model(String model_path) throws OrtException {
		OrtEnvironment env = OrtEnvironment.getEnvironment();
		System.out.println(env.toString());
		OrtSession.SessionOptions opts = new SessionOptions();
		opts.setOptimizationLevel(OptLevel.BASIC_OPT);

		for (int i = 0; i < MODELS.length; i++) {
			String path = PATH_ROOT + MODELS[i] + "/" + model_path + MODEL_NAME[i];
			OrtSession session = env.createSession(path, opts);
			Models.put(MODELS[i], session);
		}
	}

	/**
	 * 调用get_Conf()读取配置文件, 保存结果至 Configs, 同时调用 set_Conf() 将配置信息读入全局变量
	 * 
	 * @param conf_path 配置文件路径
	 * @throws IOException
	 * @author: 62786
	 * @date: 2020年12月7日 上午11:17:07
	 */
	public static void load_Config(String conf_path) throws IOException {
		// 各个厂商的配置文件不同
		for (String model : MODELS) {
			System.out.printf("\nReading config form %s%n", model);
			Map<String, List<String>> configMap = new HashMap<>();
			// 读取指定的配置文件
			for (String path : CONFIG_FILES) {
				String final_path = PATH_ROOT + model + "/" + conf_path + path;
				// 从路径读取配置文件
				List<String> conf = get_Conf(final_path);
				System.out.printf("Reading %s%n", final_path);
				switch (path) {
				case "firmware.conf":
					configMap.put(FIRM_WARE, conf);
					break;
				case "transmodel.conf":
					configMap.put(TRANSFER_MODE, conf);
					break;
				case "norm.conf":
					if (conf.size() != 3) {
						System.out.printf("Norm ConfigFile %s read error! %d%n", path, conf.size());
						System.exit(0);
					}
					configMap.put(NORM, new ArrayList<String>());
					configMap.get(NORM).add(conf.get(0));
					configMap.put(ARG_1, new ArrayList<String>());
					configMap.get(ARG_1).add(conf.get(1));
					configMap.put(ARG_2, new ArrayList<String>());
					configMap.get(ARG_2).add(conf.get(2));
					break;
				case "RandomForestClassifier.conf":
					if (conf.size() != 2) {
						System.out.printf("Model ConfigFile %s close error!%n", path);
						System.exit(0);
					}
					configMap.put(PREDICT_WINDOW, new ArrayList<String>());
					configMap.get(PREDICT_WINDOW).add(conf.get(0));
					configMap.put(FEATURE, new ArrayList<String>());
					configMap.get(FEATURE).add(conf.get(1));
					break;

				default:
					break;
				}
			}
			Configs.put(model, configMap);
		}
		// 直接将配置信息保存在全局变量中
		set_Conf();
	}

	/**
	 * 从文件读取原始配置信息
	 * 
	 * @param path
	 * @return 返回 配置信息
	 * @throws IOException
	 */
	public static List<String> get_Conf(String path) throws IOException {
		FileInputStream conf_file = new FileInputStream(path);
		BufferedReader conf_reader = new BufferedReader(new InputStreamReader(conf_file));
		String line = null;
		List<String> conf = new ArrayList<>();
		while ((line = conf_reader.readLine()) != null) {
			conf.add(line.trim());
		}
		try {
			conf_reader.close();
			conf_file.close();
		} catch (IOException e) {
			System.out.printf("ConfigFile %s close error!%n", path);
			return null;
		}
		return conf;
	}

	/**
	 * 将配置信息写入全局变量
	 * 
	 * @throws IOException
	 * @author: 62786
	 * @date: 2020年12月7日 下午7:10:44
	 */
	public static void set_Conf() throws IOException {
		// 筛选第一遍feature
		feature_filter = feature_filter();

		for (String model : MODELS) {
			firmware_tableMap.put(model, get_Firmwares(Configs.get(model).get(FIRM_WARE)));
			transfermode_tableMap.put(model, get_Transfermode(Configs.get(model).get(TRANSFER_MODE)));
			featuresMap.put(model, get_Features(Configs.get(model).get(FEATURE).get(0)));
			arg1Map.put(model, get_ARG(Configs.get(model).get(ARG_1).get(0), featuresMap.get(model)));
			arg2Map.put(model, get_ARG(Configs.get(model).get(ARG_2).get(0), featuresMap.get(model)));
			normMap.put(model, get_NORM(Configs.get(model).get(NORM).get(0)));
		}
	}

	/**
	 * 预测，并将结果保存至 Result
	 * 
	 * @param session    模型会话
	 * @param input_data 输入预处理后的数据
	 * @throws OrtException
	 * @author: 62786
	 * @date: 2020年12月7日 上午10:57:23
	 */
	public static List<Predict_Result> predict(Map<String, OrtSession> session, List<Disk_Data> input_data)
			throws OrtException {
		List<Predict_Result> cur_result = new ArrayList<>();
		OnnxTensor tensorFromArray = null;
		Map<String, OnnxTensor> input_map = null;
		OrtEnvironment env = OrtEnvironment.getEnvironment();
//		System.out.println(input_name);

		for (Disk_Data data : input_data) {
			String input_name = session.get(data.model).getInputNames().iterator().next();
			float[][] temp_data = new float[1][];
			temp_data[0] = double2float(data.data);
			tensorFromArray = OnnxTensor.createTensor(env, temp_data);
			input_map = Collections.singletonMap(input_name, tensorFromArray);

			cur_result.add(new Predict_Result(data.imei, data.model, session.get(data.model).run(input_map)));
		}
		return cur_result;
	}

	/**
	 * 对数据进行分批处理 并 运行预测模型
	 * 
	 * @param data_path 数据路径
	 * @param sep       分隔符 默认为','
	 * @param configMap 配置信息
	 * @throws IOException
	 * @author: 62786
	 * @throws OrtException
	 * @throws InterruptedException
	 * @date: 2020年12月7日 上午10:50:33
	 */
	public static void loadData_run(String data_path, String sep)
			throws IOException, OrtException, InterruptedException {
		// 计时
		long start = System.nanoTime();
		MappedBigFileReader reader = new MappedBigFileReader(data_path, MAX_BYTE_LENGTH);
		if (sep == null) {
			sep = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
//			sep = ",";
		}

		if (TEST) {
			INDEX_DISKSIZE -= 1;
			INDEX_TRANSFER_MAX -= 1;
			INDEX_TRANSFER_CUR -= 1;
		}
		// 使用多线程处理数据 To_runs
		To_run[] To_runs = new To_run[MAX_THREAD_NUM];
		// 开始分片读取
		int split_nums = 1;
		// 保存原始数据 每条数据是一个string
		List<String> datas = null;
		// 保存原始数据 每条数据是一个list<string>
		List<String> one_data = null;
		List<List<String>> input_datas = new ArrayList<>();
		// 分批读取数据
		System.out.printf("\nReading data form %s%n", data_path);
		boolean flag = true;
		while (reader.read() != -1) {
			datas = reader.readArray();
//			System.out.println(datas.toString());
			for (String temp : datas) {
				if (flag) {
					flag = false;
					continue;
				}
				temp = temp.replaceAll("\"", "");
				// 将 csv 数据转换为 list<string>
				one_data = new ArrayList<>(Arrays.asList(temp.split(sep)));

				// 如果是测试，就保存真实标签
				if (TEST) {
					True_Label.add(Integer.valueOf(one_data.get(INDEX_LABEL)));
					one_data = one_data.subList(INDEX_INTERVAL + 1, one_data.size());
					// 去掉 INDEX_TRANSFER_MAX
					one_data.remove(one_data.get(INDEX_TRANSFER_MAX));
				}

				input_datas.add(one_data);
			}
			All_rows += datas.size();
			// **************************** 使用多线程分批对数据进行处理 ***************************
			// 分配数据
			int pre_num = input_datas.size() / To_runs.length;
			for (int i = 0; i < To_runs.length; i++) {
				To_runs[i] = new To_run();
				if (i == To_runs.length - 1) {
					// 最后一个线程
					To_runs[i].set_data(input_datas.subList(i * pre_num, input_datas.size()));
				} else {
					To_runs[i].set_data(input_datas.subList(i * pre_num, (i + 1) * pre_num));
				}
				// 开始执行线程
				To_runs[i].start();
			}
			for (int i = 0; i < To_runs.length; i++) {
				// 等待线程执行完毕
				To_runs[i].join();
			}
			for (int i = 0; i < To_runs.length; i++) {
				// 获取线程的执行结果
				Result.addAll(To_runs[i].get_result());
			}
			input_datas.clear();
			System.out.printf("\rProcessed:%.2f%s  %.2f/%.2f（MB）  row_num-%d  -- Split NO.%d",
					1.0 * reader.getReadLength() / reader.getFileLength() * 100, "%", reader.getReadLength(),
					reader.getFileLength(), All_rows, split_nums);
			split_nums++;
//    		if (split_nums > 5) break;
		}
		;

		System.out.printf("%n%n数据预处理完成！ 缺失的Firmware总数：%d%n", Lost_Firmware);
		if (Lost_Firmware > 0) {
			print_lostfirmware();
		}
		long end = System.nanoTime();
		reader.close();
		System.out.printf("%n耗时: %.2f 分钟%n", ((1.0 * end - start) / 600 / 100000000));
	}

	/**
	 * 使用多线程对数据进行处理、预测，并返回结果
	 * 
	 * @author 62786
	 * @date: 2020年12月7日 下午7:15:00
	 */
	static class To_run extends Thread {
		private List<List<String>> input_datas;
		private List<Predict_Result> results = null;

		public To_run() {
		}

		public void set_data(List<List<String>> input_datas) {
			this.input_datas = input_datas;
		}

		public List<Predict_Result> get_result() {
			return results;
		}

		public void run() {
			try {
				// 对初始数据进行预处理
				List<Disk_Data> datas_to_run = prepross(input_datas);
				// 预测，并将结果保存至 Result
				results = predict(Models, datas_to_run);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (OrtException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 打印结果
	 * 
	 * @param results
	 * @throws OrtException
	 * @throws IOException
	 */
	public static void print_result() throws OrtException {
//		统计各厂商 故障盘：正常盘
		int error_num = 0;
		int safe_num = 0;

		Integer[] default_count = { 0, 0, 0 };
		for (String key : MODELS) {
			Model_counts.put(key, default_count);
		}
		Model_counts.put(DEFAULT_MODEL_NAME, default_count);

		for (Predict_Result result : Result) {
			// label表示预测结果0 1
			long[] label = (long[]) result.result.get(0).getValue();
			// preb表示预测概率
//			List<Map<String, Float>> preb = (List<Map<String, Float>>) result.get(1).getValue();
			// 读取记录的信息 更新
			Integer[] temp = Model_counts.get(result.model);
			if (label[0] == 1) {
				error_num++;
				Model_counts.put(result.model, new Integer[] { temp[0] + 1, temp[1] + 1, temp[2] });
			} else {
				safe_num++;
				Model_counts.put(result.model, new Integer[] { temp[0] + 1, temp[1], temp[2] + 1 });
			}
		}

		System.out.printf("%n统计完成，故障盘:正常盘 = %d:%d%n", error_num, safe_num);
		print_modelcounts();
	}

	/**
	 * 输出各个 firmware 的缺失数
	 * 
	 * @throws OrtException
	 * @author: 62786
	 * @date: 2020年12月8日 下午3:58:32
	 */
	public static void print_lostfirmware() {
		// 降序排列
		List<Map.Entry<String, Integer[]>> list = new ArrayList<Map.Entry<String, Integer[]>>(
				Default_Firmware.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer[]>>() {

			@Override
			public int compare(Entry<String, Integer[]> o1, Entry<String, Integer[]> o2) {

				return o2.getValue()[1].compareTo(o1.getValue()[1]);
			}
		});
		System.out.printf("%n缺失的 firmware 分布（显示前%d个）：%n", Show_Lost_Firmware);
		System.out.printf("%-5s%15s%15s", "--Firmware--", "--Label--", "--Num--");
		Map.Entry<String, Integer[]> data = null;
		for (int i = 0; i < list.size(); i++) {
			if (i > Show_Lost_Firmware)
				break;
			data = list.get(i);
			System.out.println();
			System.out.printf("%-10s", data.getKey());
			System.out.printf("%13d", data.getValue()[0]);
			System.out.printf("%18d", data.getValue()[1]);
		}
		System.out.println();
	}

	/**
	 * 输出不同厂商的告警率
	 * 
	 * @param model_counts
	 * @throws OrtException
	 * @author: wj
	 * @date: 2020年12月7日 上午9:17:03
	 */
	public static void print_modelcounts() {
		System.out.println("各厂商数据分布：");
		System.out.printf("%-5s%15s%15s%15s%15s", "--Model--", "--Total--", "--Error--", "--Normal--", "--Raito--");
		for (String key : Model_counts.keySet()) {
			Integer[] data = Model_counts.get(key);
			System.out.println();
			System.out.printf("%-10s", key);
			System.out.printf("%11d", data[0]);
			System.out.printf("%15d", data[1]);
			System.out.printf("%15d", data[2]);
			System.out.printf("%15.3f%s", 100.0 * data[1] / data[0], "%");
		}
		System.out.println("\n");
	}

	/**
	 * 如果是测试则输出模型的评估指标
	 * 
	 * @param results
	 * @throws OrtException
	 */
	public static void evaluate() throws OrtException {
		if (!TEST) {
			System.out.println("测试时才能显示评估结果！\n try: Model_tools.TEST = true; at the start of your main(){...}");
			return;
		}
		List<Integer> labels = new ArrayList<Integer>();
		for (Predict_Result result : Result) {
			// label 为模型的预测结果
			long[] label = (long[]) result.result.get(0).getValue();
			labels.add(Integer.valueOf((int) label[0]));
		}
		if (labels.size() != True_Label.size()) {
			System.out.printf("评估数据量错误！ %d!=%d%n", labels.size(), True_Label.size());
			return;
		}

		int TP = 0, FN = 0, FP = 0, TN = 0;
		for (int i = 0; i < True_Label.size(); i++) {
			if (True_Label.get(i) == 1 && labels.get(i) == 1) {
				TP++;
			} else if (True_Label.get(i) == 1 && labels.get(i) == 0) {
				FN++;
			} else if (True_Label.get(i) == 0 && labels.get(i) == 1) {
				FP++;
			} else {
				TN++;
			}
		}
		double Accuracy = 1.0 * (TP + TN) / (TP + FN + FP + TN);
		double Recall = 1.0 * TP / (TP + FN);
		double Specificity = 1.0 * TN / (TN + FP);
		double Error_Rate = 1.0 * (FP + FN) / (TP + FN + FP + TN);
		double FAR = 1.0 * FP / (FP + TN);

		System.out.printf("%30s", "Predict");
		System.out.println();
		System.out.printf("%22s", "1");
		System.out.printf("%10s", "0");
		System.out.println();
		System.out.printf("%-8s", "Real");
		System.out.printf("%-10s", "1: " + (TP + FN));
		System.out.printf("%-10s", "TP: " + TP);
		System.out.printf("%-10s", "FN: " + FN);
		System.out.println();
		System.out.printf("%-8s", "    ");
		System.out.printf("%-10s", "0: " + (FP + TN));
		System.out.printf("%-10s", "FP: " + FP);
		System.out.printf("%-10s", "TN: " + TN);

		System.out.println();
		System.out.println();
		System.out.printf("%-20s", "Target");
		System.out.printf("%-10s", "Value");
		System.out.println();
		System.out.printf("%-20s", "Accuracy");
		System.out.printf("%-10s", Accuracy);
		System.out.println();
		System.out.printf("%-20s", "Recall");
		System.out.printf("%-10s", Recall);
		System.out.println();
		System.out.printf("%-20s", "Specificity");
		System.out.printf("%-10s", Specificity);
		System.out.println();
		System.out.printf("%-20s", "Failure Alarm Rate");
		System.out.printf("%-10s", FAR);
		System.out.println();
		System.out.printf("%-20s", "Error_Rate");
		System.out.printf("%-10s", Error_Rate);
	}

	/**
	 * 写回结果到新的CSV文件中
	 * 
	 * @param input_path
	 * @param output_path
	 * @param results
	 * @throws IOException
	 * @throws OrtException
	 */
	@SuppressWarnings("unchecked")
	public static void write_back(String input_path, String output_path) throws IOException, OrtException {
		if (Result.size() != (All_rows - 1)) {
			System.err.println("结果与数据长度不匹配！");
			return;
		}
		System.out.printf("%nWrite result back to: %s%n", output_path);

		// 创建输出文件
		File output_file = new File(output_path);
		output_file.createNewFile();
		BufferedWriter output_writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(output_file), "UTF-8"), 1024);

		// 读取原始数据文件
		boolean isheader = true;
		String lineSep = System.getProperty("line.separator");
		MappedBigFileReader reader = new MappedBigFileReader(input_path, MAX_BYTE_LENGTH);

		// 设置数据格式
		DecimalFormat decimalFormat = new DecimalFormat(".000");
		List<String> datas = new ArrayList<>();
		int row_num = 0;
		Predict_Result result = null;
		long[] label = null;
		List<Map<String, Float>> preb = null;
		Map<String, Float> preb_map = null;
		String output_data = null;
		while (reader.read() != -1) {
			datas = reader.readArray();
			for (String temp : datas) {
				// 跳过表头
				if (isheader) {
					temp = temp.substring(0, temp.length() - 1) + ",label,preb_0,preb_1" + lineSep;
					output_writer.write(temp);
					isheader = false;
					continue;
				}

				result = Result.get(row_num);
				label = (long[]) result.result.get(0).getValue();
				preb = (List<Map<String, Float>>) result.result.get(1).getValue();
				preb_map = preb.get(0);
				// 组装输出数据
				output_data = "," + String.valueOf(label[0]);

				for (Entry<String, Float> v : preb_map.entrySet()) {
					output_data += "," + String.valueOf(decimalFormat.format(v.getValue()));
				}
				output_data = temp.substring(0, temp.length() - 1) + output_data;
				output_writer.write(output_data);
				output_writer.newLine();
				row_num++;
			}
			output_writer.flush();
		}
		try {
			output_writer.close();
		} catch (IOException e) {
			System.out.printf("WriteBackFile %s close error!%n", output_file);
		}
	}

	/*------------------------------------- 预处理 -------------------------------------*/
	/**
	 * 对输入的初始数据进行逐条预处理
	 * 
	 * @param input_data
	 * @return
	 * @throws IOException
	 * @author: 62786
	 * @date: 2020年12月7日 下午7:16:32
	 */
	public static List<Disk_Data> prepross(List<List<String>> input_data) throws IOException {
		List<Disk_Data> output_datas = new ArrayList<>();
		// 开始逐条处理初始数据
		for (List<String> data : input_data) {
			String select_model = DEFAULT_MODEL;
			String imei = data.get(INDEX_IMEI);
			for (String key : MODELS) {
				if (data.get(INDEX_MODEL).toLowerCase().contains(key.toLowerCase())) {
					select_model = key;
				}
			}

//			System.out.println("\n预处理之前：" + data.toString());
			if (!TEST) {
				// 对Firmware进行标签编码
				data = prep_Firmware(data, firmware_tableMap.get(select_model));
//				System.out.println("Firmware进行标签编码之后：" + data.toString());
				// 对transfermodel进行标签编码
				data = prep_Transfermodel(data, transfermode_tableMap.get(select_model));
//				System.out.println("transfermodel进行标签编码之后：" + data.toString());
			}
			// 剔除DISKSIZE之前的标识数据 剔除最后一列的时间戳 根据特征选择结果筛选数据
			data = prep_Feature(data, featuresMap.get(select_model));
//			System.out.println("特征筛选之后：" + data.toString());
			// 归一化
			Disk_Data disk_Data = new Disk_Data(imei, select_model,
					prep_Norm(normMap.get(select_model), data, arg1Map.get(select_model), arg2Map.get(select_model)));
			output_datas.add(disk_Data);
//			System.out.println("归一化之后：" + Arrays.toString(disk_Data.data));
		}
		return output_datas;
	}

	/*------------------------------------- 标签编码 -------------------------------------*/
	/**
	 * Firmware标签编码
	 * 
	 * @param input_data
	 * @param firmware_table
	 * @return
	 */
	public static List<String> prep_Firmware(List<String> input_data, Map<String, String> firmware_table) {
		String firmware = input_data.get(INDEX_FIRMWARE);
//		System.out.println(firmware);
		String firmware_code = null;
		if (!firmware_table.containsKey(firmware)) {
			firmware_code = default_Firmware(firmware, firmware_table.size());
			Lost_Firmware++;
		} else {
			firmware_code = firmware_table.get(firmware);
		}
		// 加入到倒数第二列，即属性列的最后一列
		input_data.add(input_data.size() - 1, firmware_code);
		return input_data;
	}

	/**
	 * 如果没有该firmware 则生成默认的标签编码
	 * 
	 * @param fw
	 * @param size
	 * @return
	 * @author: 62786
	 * @date: 2020年12月8日 下午3:49:53
	 */
	public static String default_Firmware(String fw, int size) {
		Integer[] label_num = null;
		if (Default_Firmware.containsKey(fw)) {
			label_num = Default_Firmware.get(fw);
			label_num[1] += 1;
		} else {
			label_num = new Integer[] { size + Default_Firmware.size(), 1 };
		}
		Default_Firmware.put(fw, label_num);
		return String.valueOf(label_num[0]);
	}

	/**
	 * TransferModel标签编码
	 * 
	 * @param input_data
	 * @param firmware_table
	 * @return
	 */
	public static List<String> prep_Transfermodel(List<String> input_data, Map<String, String> transfermode_table) {
		String transfermode = input_data.get(INDEX_TRANSFER_CUR);
		String transfermode_code = null;
		if (!transfermode_table.containsKey(transfermode)) {
			transfermode_code = String.valueOf(transfermode_table.size());
			Lost_Transfer_mode++;
		} else {
			transfermode_code = transfermode_table.get(transfermode);
		}
		// 加入到原位置
		input_data.remove(input_data.get(INDEX_TRANSFER_CUR));
		input_data.add(INDEX_TRANSFER_CUR, transfermode_code);
		return input_data;
	}

	/*------------------------------------- 特征筛选 -------------------------------------*/
	/**
	 * 根据特征选择结果筛选数据
	 * 
	 * @param input_data
	 * @param features
	 * @return
	 */
	public static List<String> prep_Feature(List<String> input_data, List<Integer> features) {
		List<String> temp_data = input_data.subList(INDEX_DISKSIZE, input_data.size() - 1);
		if (temp_data.size() < features.get(features.size() - 1)) {
			System.out.println("特征筛选错误，数据长度过短！");
			System.exit(0);
		}
		// 第一遍筛选 过滤 当天的值 保留累计值
		if (!TEST) {
			List<String> temp_data2 = new ArrayList<String>();
			for (int index : feature_filter) {
				temp_data2.add(temp_data.get(index));
			}
			temp_data = temp_data2;
		}

		// 第二遍筛选 特征选择
		List<String> new_data = new ArrayList<String>();
		for (int index : features) {
			new_data.add(temp_data.get(index));
		}

		return new_data;
	}

	public static List<Integer> feature_filter() {
		List<Integer> features = new ArrayList<Integer>();
		// 先筛选一遍数据
		// 1：DiskSize - Current transfer mode
		features.add(INDEX_DISKSIZE - INDEX_DISKSIZE);
		features.add(INDEX_TRANSFER_CUR - INDEX_DISKSIZE);
		// 2：SMART_1 - SMART_48
		for (int i = 1; i <= NUM_SAMRT; i++) {
			features.add(INDEX_TRANSFER_CUR - INDEX_DISKSIZE + i);
		}
		// 3：WE_1 - WE_14
		for (int i = 1; i <= NUM_WE; i++) {
			features.add(INDEX_TRANSFER_CUR - INDEX_DISKSIZE + NUM_SAMRT + i * 2);
		}

		// 4：BSOD_1 - BSOD_23
		for (int i = 1; i <= NUM_BSOD; i++) {
			features.add(INDEX_TRANSFER_CUR - INDEX_DISKSIZE + NUM_SAMRT + NUM_WE * 2 + i * 2);
		}

		// 5：Firmware
		features.add(INDEX_TRANSFER_CUR - INDEX_DISKSIZE + NUM_SAMRT + NUM_WE * 2 + NUM_BSOD * 2 + 1);

		System.out.println("List<Integer> feature_filter()");
		for (int index : features) {
			System.out.printf(" %d", index);
		}
		System.out.println();
		return features;
	}

	/*------------------------------------- 归一化 -------------------------------------*/
	/**
	 * 数据归一化
	 * 
	 * @param norm
	 * @param input_data
	 * @param arg1
	 * @param arg2
	 * @return
	 */
	public static double[] prep_Norm(int norm, List<String> input_data, List<Double> arg1, List<Double> arg2) {
		if (norm == 1) {
			return norm_Zscore(input_data, arg1, arg2);
		} else if (norm == 2) {
			return norm_MinMax(input_data, arg1, arg2);
		} else {
			System.out.println("数据归一化错误！");
			System.exit(0);
			return null;
		}
	}

	/**
	 * 归一化方法1：均值方差法
	 * 
	 * @param input_data
	 * @param avg
	 * @param std
	 * @return
	 */
	public static double[] norm_Zscore(List<String> input_data, List<Double> avg, List<Double> std) {
		if (avg.size() != std.size() || avg.size() != input_data.size()) {
			System.out.printf("prep_Zscore 处理失败！%d-%d-%d%n", avg.size(), std.size(), input_data.size());
			System.exit(0);
		}
		double[] output_data = new double[input_data.size()];
		String num = null;
		for (int i = 0; i < input_data.size(); i++) {
			num = input_data.get(i);
			try {
				output_data[i] = std.get(i) == 0 ? Double.valueOf(num)
						: (Double.valueOf(num) - avg.get(i)) / std.get(i);
			} catch (Exception e) {
				output_data[i] = 0;
			}
		}
		return output_data;
	}

	/**
	 * 归一化方法2：最大最小值法
	 * 
	 * @param input_data
	 * @param min
	 * @param max
	 * @return
	 */
	public static double[] norm_MinMax(List<String> input_data, List<Double> min, List<Double> max) {
		if (min.size() != max.size() || min.size() != input_data.size()) {
			System.out.printf("prep_MinMax 处理失败！%d-%d-%d%n", min.size(), max.size(), input_data.size());
			System.exit(0);
		}
		double[] output_data = new double[input_data.size()];
		for (int i = 0; i < input_data.size(); i++) {
			output_data[i] = Double.valueOf(input_data.get(i)) == 0.0 ? 0.0
					: (Double.valueOf(input_data.get(i)) - min.get(i)) / (max.get(i) - min.get(i));
		}
		return output_data;
	}

	/*------------------------------------- 预处理需要用到的数据转换函数 -------------------------------------*/
	/**
	 * 获取firmware映射表
	 * 
	 * @param firmware
	 * @return
	 */
	public static Map<String, String> get_Firmwares(List<String> firmware) {
		Map<String, String> firmware_table = new HashMap<String, String>();
		for (String firmware_code : firmware) {
			String[] code = firmware_code.split(":");
			firmware_table.put(code[0], code[1]);
		}
		return firmware_table;
	}

	/**
	 * 获取transfermode映射表
	 * 
	 * @param firmware
	 * @return
	 */
	public static Map<String, String> get_Transfermode(List<String> transfermode) {
		Map<String, String> transfermode_table = new HashMap<String, String>();
		for (String firmware_code : transfermode) {
			String[] code = firmware_code.split(":");
			transfermode_table.put(code[0], code[1]);
		}
		return transfermode_table;
	}

	/**
	 * 获取特征筛选标签
	 * 
	 * @param feature
	 * @return
	 */
	public static List<Integer> get_Features(String feature) {
		return getIntListbyStr(feature);
	}

	/**
	 * 获取归一化参数
	 * 
	 * @param avg
	 * @return
	 */
	public static List<Double> get_ARG(String avg, List<Integer> features) {
		List<Double> temp_data = getDoubleListbyStr(avg);
		List<Double> new_data = new ArrayList<>();
		for (int index : features) {
			new_data.add(temp_data.get(index));
		}
		return new_data;
	}

	/**
	 * 获取归一化的方法标志位
	 * 
	 * @param norm
	 * @return
	 */
	public static Integer get_NORM(String norm) {
		return Integer.valueOf(norm);
	}

	/*------------------------------------- 数据转换需要用到的基本函数 -------------------------------------*/

	/**
	 * String 转 list[int]
	 * 
	 * @param list
	 * @return
	 */
	public static List<Integer> getIntListbyStr(String list) {
		JSONArray param;
		param = new JSONArray(list);
		List<Integer> result = new ArrayList<Integer>();
		// ����ģ���id����
		for (int i = 0; i < param.length(); i++) {
			result.add(Integer.valueOf(param.get(i).toString()));
		}
		return result;
	}

	/**
	 * String 转 list[double]
	 * 
	 * @param list
	 * @return
	 */
	public static List<Double> getDoubleListbyStr(String list) {
		JSONArray param;
		param = new JSONArray(list);
		List<Double> result = new ArrayList<Double>();
		for (int i = 0; i < param.length(); i++) {
			result.add(Double.parseDouble(param.get(i).toString()));
		}
		return result;
	}

	/**
	 * String 转 list[String]
	 * 
	 * @param list
	 * @return
	 */
	public static List<String> getStrListbyStr(String list) {
		JSONArray param;
		param = new JSONArray(list);
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < param.length(); i++) {
			result.add(param.get(i).toString());
		}
		return result;
	}

	/**
	 * String 转 list[float]
	 * 
	 * @param input_data
	 * @return
	 */
	public static float[] double2float(double[] input_data) {
		float[] output_data = new float[input_data.length];
		for (int i = 0; i < input_data.length; i++) {
			output_data[i] = (float) input_data[i];
		}
		return output_data;
	}

//	public static void copy_str2lsit(String[] strs, ArrayList<String> list) {
//		for (String s : strs) {
//			list.a
//		}
//	}
}
