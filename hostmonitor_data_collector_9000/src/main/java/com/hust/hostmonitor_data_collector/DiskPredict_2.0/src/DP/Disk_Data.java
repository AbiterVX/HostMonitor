package DP;

/**数据预处理之后的数据类型，包括 硬件信息、打点数据
 * @author 62786
 * @date: 2020年12月7日 下午7:18:23
 */
public class Disk_Data {
	public String imei;	// imei
	public String model;	// 厂商
	public double[] data;	// 打点信息
	
	public Disk_Data(String imei, String model, double[] data) {
		this.imei = imei;
		this.model = model;
		this.data = data;
	}
}
