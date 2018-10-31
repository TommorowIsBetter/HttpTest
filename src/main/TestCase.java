import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
	private static String URL1;
	private static String URL2;
	static {
		try{
			Properties props = new Properties();
			props.load(TestCase.class.getResourceAsStream("createfile.properties"));
			URL1 = props.getProperty("url1");
			URL2 = props.getProperty("url2");
		}
		catch(Exception e) {
			e.printStackTrace();
		} 
	}
	
	private TestSessionListener listener;
	public static final String DEF_CHATSET = "UTF-8";
    public static final int DEF_CONN_TIMEOUT = 3000;
    public static final int DEF_READ_TIMEOUT = 3000;
    public static String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";
	
    private static final String nextLine = "\r\n"; 
    private static final String twoHyphens = "--"; 
    private static final String boundary = "wk_file_2519775"; 
	
	
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
			int statusCode  = get(URL1); 
			/**
			 * 第二步上传操作，返回值应该是201才是正确的,这里为了统一把返回值减1即为200
			 */
			 statusCode = uploadFile(URL2);
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
		TestSessionListenerImpl impl = new TestSessionListenerImpl("createfile");
		TestCase main = new TestCase(impl);
		main.run();
	}
  /**
   * 
   * @param urlPath 要访问的ulr连接。
   * @param method  方法是HTTP方法中其中之一，例如GET,POST,PUT等操作。
   * @return HttpURLConnection  返回一个连接
   * @throws IOException
   * @Description 这个方法的作用，是为uploadFile()方法进行调用。
   */
    private static HttpURLConnection createConnection(String urlPath, String method) 
    		throws IOException 
    { 
    	URL url = new URL(urlPath); 
    	HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(); 
    	httpURLConnection.setRequestMethod(method); 
    	httpURLConnection.setRequestProperty("Charsert", "UTF-8"); 
    	return httpURLConnection; 
    }

	/**
	 * 
	 * @param file 要上传的文件
	 * @param url  要上传的连接
	 * @throws IOException 
	 * @Description 这个方法的作用是输入参数，实现文件的上传。此方法应该在第二步，因为WebHDFS的要求第二步可以
	 * 带文件PUT操作。
	 */
    private static int uploadFile(String url) throws IOException
    { 
    	HttpURLConnection connection = null; 
    	OutputStream outputStream = null; 
    	FileInputStream inputStream = null; 
    	try 
    	{   //获取HTTPURLConnection连接 
    		connection = createConnection(url, "PUT"); 
    		//运行写入默认为false，置为true 
    		connection.setDoOutput(true); 
    		//禁用缓存 
    		connection.setUseCaches(false); 
    		//设置接收编码 
    		connection.setRequestProperty("Accept-Charset", "utf-8"); 
    		//开启长连接可以持续传输 
    		connection.setRequestProperty("Connection", "keep-alive"); 
    		//设置请求参数格式以及boundary分割线 
    		connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary); 
    		//设置接收返回值的格式 
    		connection.setRequestProperty("Accept", "application/json"); 
    		//开启连接 
    		connection.connect(); 
    		//获取http写入流 
    		outputStream = new DataOutputStream(connection.getOutputStream()); 
    		//分隔符头部 
    		String header = twoHyphens + boundary + nextLine; 
    		//分隔符参数设置 
    		header += "Content-Disposition: form-data;name=\"file\";" + "filename=\""  + "\"" + nextLine + nextLine; 
    		//写入输出流 
    		outputStream.write(header.getBytes());
    		//读取文件并写入
    		byte[] bytes = new byte[1024]; 
    		for(int i=0;i<=1000;i++){
    			outputStream.write(header.getBytes());
    		}
    		//文件写入完成后加回车 
    		outputStream.write(nextLine.getBytes()); 
    		//写入结束分隔符 
    		String footer = nextLine + twoHyphens + boundary + twoHyphens + nextLine; 
    		outputStream.write(footer.getBytes()); 
    		outputStream.flush(); 
    		//文件上传完成 
    		InputStream response = connection.getInputStream(); 
    		InputStreamReader reader = new InputStreamReader(response); 
    		while (reader.read() != -1)
    		{ 
    			System.out.println(new String(bytes, "UTF-8")); 
    		} 
    		if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED)
    		 { 
    			System.out.println(connection.getResponseMessage()); 
    		}else 
    		    { 
    			System.err.println("上传失败"+connection.getResponseCode()); 
    			} 
    		} catch (IOException e) 
    		{ 
    			e.printStackTrace(); 
    		}finally { 
    			try { 
    				if (outputStream != null)
    			       { 
    					outputStream.close(); 
    				   } 
    				if (inputStream != null)
    				   {  
    					inputStream.close(); 
    				   }
    				if (connection != null)
    				   { 
    					connection.disconnect(); 
    				   } 
    				} catch (IOException e) 
    				{ 
    					e.printStackTrace(); 
    				} 
    		} 
    	return connection.getResponseCode()-1;
    	}
    
    
    /**
     * 
     * @param urlT 第一步操作，实现URL的连接操作，此时不需要带文件，正常的返回值应该是307才正确。
     * @return int 
     * @throws Exception
     * @Description 用于WebHDFS文件上传的第一步操作。
     */
    public static int get(String urlT) throws Exception {
        HttpURLConnection conn = null;
        URL url = new URL(urlT);
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("User-agent", userAgent);
        conn.setUseCaches(false);
        conn.setConnectTimeout(DEF_CONN_TIMEOUT);
        conn.setReadTimeout(DEF_READ_TIMEOUT);
        conn.setInstanceFollowRedirects(false);
        conn.connect();
        int statusCode = conn.getResponseCode();
        return statusCode;
        
    }
}
