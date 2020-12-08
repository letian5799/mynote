package com.supergo.utils;

import java.net.URLDecoder;

import org.apache.log4j.Logger;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

public class FastDFSClient {
	private static Logger logger = Logger.getLogger(FastDFSClient.class);

	private static TrackerClient trackerClient = null;
	private static TrackerServer trackerServer = null;
	private static StorageServer storageServer = null;
	private static StorageClient1 client = null;
	// fdfsclient的配置文件的路径
	private static String CONF_NAME = "/fdfs/fdfs_client.conf";

	static {
		try {
			// 配置文件必须指定全路径
			String confName = FastDFSClient.class.getResource(CONF_NAME).getPath();
			// 配置文件全路径中如果有中文，需要进行utf8转码
			confName = URLDecoder.decode(confName, "utf8");

			ClientGlobal.init(confName);
			trackerClient = new TrackerClient();
			trackerServer = trackerClient.getConnection();
			storageServer = null;
			client = new StorageClient1(trackerServer, storageServer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 上传文件方法
	 * <p>
	 * Title: uploadFile
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param fileName
	 *            文件全路径
	 * @param extName
	 *            文件扩展名，不包含（.）
	 * @param metas
	 *            文件扩展信息
	 * @return
	 * @throws Exception
	 */
	public static String uploadFile(String fileName, String extName, NameValuePair[] metas) throws Exception {
		String result = client.upload_file1(fileName, extName, metas);
		return result;
	}

	public static String uploadFile(String fileName) throws Exception {
		return uploadFile(fileName, null, null);
	}

	public static String uploadFile(String fileName, String extName) throws Exception {
		return uploadFile(fileName, extName, null);
	}

	/**
	 * 上传文件方法
	 * <p>
	 * Title: uploadFile
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param fileContent
	 *            文件的内容，字节数组
	 * @param extName
	 *            文件扩展名
	 * @param metas
	 *            文件扩展信息
	 * @return
	 * @throws Exception
	 */
	public static String uploadFile(byte[] fileContent, String extName, NameValuePair[] metas) throws Exception {

		String result = client.upload_file1(fileContent, extName, metas);
		return result;
	}

	public static String uploadFile(byte[] fileContent) throws Exception {
		return uploadFile(fileContent, null, null);
	}

	public static String uploadFile(byte[] fileContent, String extName) throws Exception {
		return uploadFile(fileContent, extName, null);
	}

	public static String uploadFile2(String filePath) throws Exception {
		String fileId = "";
		String fileExtName = "";
		if (filePath.contains(".")) {
			fileExtName = filePath.substring(filePath.lastIndexOf(".") + 1);
		} else {
			logger.warn("Fail to upload file, because the format of filename is illegal.");
			return fileId;
		}
		// 建立连接
		/* ....... */
		// 上传文件
		try {
			fileId = client.upload_file1(filePath, fileExtName, null);
		} catch (Exception e) {
			logger.warn("Upload file \"" + filePath + "\"fails");
		} finally {
			trackerServer.close();
		}
		return fileId;
	}

	public static String uploadSlaveFile(String masterFileId, String prefixName, String slaveFilePath)
			throws Exception {
		String slaveFileId = "";
		String slaveFileExtName = "";
		if (slaveFilePath.contains(".")) {
			slaveFileExtName = slaveFilePath.substring(slaveFilePath.lastIndexOf(".") + 1);
		} else {
			logger.warn("Fail to upload file, because the format of filename is illegal.");
			return slaveFileId;
		}
		// 建立连接
		/* ....... */
		// 上传文件
		try {
			slaveFileId = client.upload_file1(masterFileId, prefixName, slaveFilePath, slaveFileExtName, null);
		} catch (Exception e) {
			logger.warn("Upload file \"" + slaveFilePath + "\"fails");
		} finally {
			trackerServer.close();
		}
		return slaveFileId;
	}

	public static int download(String fileId, String localFile) throws Exception {
		int result = 0;
		// 建立连接
		TrackerClient tracker = new TrackerClient();
		TrackerServer trackerServer = tracker.getConnection();
		StorageServer storageServer = null;
		StorageClient1 client = new StorageClient1(trackerServer, storageServer);
		// 上传文件
		try {
			result = client.download_file1(fileId, localFile);
		} catch (Exception e) {
			logger.warn("Download file \"" + localFile + "\"fails");
		} finally {
			trackerServer.close();
		}
		return result;
	}

	public static void main(String[] args) {
		try {
			String masterFileId = uploadFile("E:/1.jpg");
			System.out.println("主文件:" + masterFileId);
			download(masterFileId, "E:/master.jpg");
			String slaveFileId = uploadSlaveFile(masterFileId, "_120x120", "E:/1.jpg");
			System.out.println("从文件:" + slaveFileId);
			download(slaveFileId, "E:/slave.jpg");
		} catch (Exception e) {
			logger.error("upload file to FastDFS failed.", e);
		}
	}
}
