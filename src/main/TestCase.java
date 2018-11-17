import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import com.cloudtest.perfcloud.test.performance.ResultStatus;
import com.cloudtest.testdriver.TestSessionListener;
import com.cloudtest.testdriver.TestSessionListenerImpl;
 
/**
 * 测试脚本的类名称必须叫 TestCase，且包名为空
 *
 */
public class TestCase implements Runnable{
	private static String URL;
	private static String DST;
	private static String NAME;
	static {
		try{
			Properties props = new Properties();
			props.load(TestCase.class.getResourceAsStream("downloadfile.properties"));
			URL = props.getProperty("url");
			DST = props.getProperty("dst");
			NAME= props.getProperty("name");
		}
		catch(Exception e) {
			e.printStackTrace();
		} 
	}
	
	private TestSessionListener listener;
		
	/**
	 * 此构造函数方法必须存在 
	 */
	public TestCase(TestSessionListener listener) {
		this.listener = listener;
	} 
	
	/**
	 * 此run方法必须存在 
	 */
	public void run() {
		// 通知测试结束
		ResultStatus status = ResultStatus.PASSED;
		String error = ""; 
		String log = "";
		long sentBytes = 0;
		long recvBytes = 0;
				
		// 通知测试开始
		listener.onTestBegin();
				
	    try {
			String token="v32Eo2Tw+qWI/eiKW3D8ye7l19mf1NngRLushO6CumLMHIO1aryun0/Y3N3YQCv/TqzaO/TFHw4=";
		    int statusCode = downLoadFromUrl(URL,NAME,DST,token);
			if(statusCode!=200) {
				status = ResultStatus.FAILED;
			} 
	    }
	    catch(Exception e) {
	    	e.printStackTrace();
	    } 
	    
	    listener.onTestEnd(status, error, log, sentBytes, recvBytes);		
	}
	
	public static void main(String[] args) throws Exception {
		TestSessionListenerImpl impl = new TestSessionListenerImpl("downloadfile");
		TestCase main = new TestCase(impl);
		main.run();
	}
	/**
     * 从网络Url中下载文件
     * @param urlStr 要下载的URL的连接
     * @param fileName 下载之后重新定义的文件名字
     * @param savePath 下载之后的保存路径
     * @param toekn 与服务器进行连接的令牌
     * @throws IOException
     */
    public static int  downLoadFromUrl(String urlStr,String fileName,String savePath,String toekn) throws IOException{
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为3秒
        //conn.setConnectTimeout(10000000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        conn.setRequestProperty("lfwywxqyh_token",toekn);
        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);
        //文件保存位置
        File saveDir = new File(savePath);
        if(!saveDir.exists()){
            saveDir.mkdir();
        }
        File file = new File(saveDir+File.separator+fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if(fos!=null){
            fos.close();
        }
        if(inputStream!=null){
            inputStream.close();
        }
        System.out.println("返回码:"+conn.getResponseCode());
        System.out.println("info:"+url+" download success");
        return conn.getResponseCode();

    }
    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     * @description downLoadFromUrl()方法会调用这个方法，帮助实现下载操作。
     */
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
}
