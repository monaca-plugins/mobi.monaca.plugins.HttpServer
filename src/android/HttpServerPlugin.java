package mobi.monaca.framework.plugin;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.http.conn.util.InetAddressUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpServerPlugin extends CordovaPlugin{

	private static final String TAG = HttpServerPlugin.class.getSimpleName();
	private static MonacaLocalServer localServer;

	@Override
	public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
//		MyLog.v(TAG, "HttpServerPlugin exec action:" + action + ", args:" + args);
		
		if(action.equalsIgnoreCase("getRootDirectoryAbsolutePath")){
			if(localServer != null){
				callbackContext.success(localServer.getServerRoot());
			}else{
				callbackContext.error("Error server is not started yet. Plesae start the server before lcalling this");
			}
			return true;
		}

		if(action.equalsIgnoreCase("getAddress")){
			JSONObject addressJSON = createAddressJSON();
			callbackContext.success(addressJSON);
			return true;
		}
		
		if(action.equalsIgnoreCase("getStatus")){
			if(localServer == null){
				JSONObject statusJSON = new JSONObject();
				statusJSON.put("status", "stopped");
				callbackContext.success(statusJSON);
			}else{
				JSONObject statusJSON = createAddressJSON();
				statusJSON.put("status", "started");
				statusJSON.put("rootDirectoryAbsolutePath", localServer.getServerRoot());
				callbackContext.success(statusJSON);
			}
			return true;
		}

		if(action.equalsIgnoreCase("start")){
			if(localServer != null){
				localServer.stop();
			}
			if (args.length() < 2) {
				callbackContext.error("either documentRoot or params is not supplied");
			} else {
				Runnable serverRunner = new Runnable(){
					@Override
					public void run() {
						try{
							String rootDir = args.getString(0);
							JSONObject params = args.getJSONObject(1);
							int port = params.getInt("port");
							localServer = new MonacaLocalServer(cordova.getActivity(), rootDir, port);
							localServer.start();
							JSONObject result = new JSONObject();
							result.put("networks", getIPAddresses());
							result.put("port", port);
							callbackContext.success(result);
						}catch (JSONException e) {
							callbackContext.error(e.getMessage());
							e.printStackTrace();
						} catch (Exception e) {
							callbackContext.error("Cannot start server. error: " + e.getMessage());
							e.printStackTrace();
						}
					}};
				Runnable fail = new Runnable(){
					@Override
					public void run() {
						callbackContext.error("Cannot start server.");
					}};

					serverRunner.run();
			}
			return true;
		}else if(action.equalsIgnoreCase("stop")){
			if(localServer != null){
				localServer.stop();
				localServer = null;
				callbackContext.success();
			}
			return true;
		}else{
			return false;
		}
	}
	
	private JSONObject createAddressJSON() throws JSONException {
		JSONObject result = new JSONObject();
		result.put("ip", getIPAddress(true));
		result.put("port", localServer.getPort());
		return result;
	}
	
	@Override
	public void onDestroy() {
//		MyLog.i(TAG, "Monaca HttpServer plugin onDestroy");
		if(localServer != null){
//			MyLog.i(TAG, "closing local server");
			localServer.stop();
		}
		super.onDestroy();
	}
	
	private String getIPAddress(boolean useIPv4) {
		try {
			List<NetworkInterface> interfaces = Collections
					.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf
						.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress().toUpperCase();
						boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
						if (useIPv4) {
							if (isIPv4)
								return sAddr;
						} else {
							if (!isIPv4) {
								int delim = sAddr.indexOf('%'); // drop ip6 port
																// suffix
								return delim < 0 ? sAddr : sAddr.substring(0,
										delim);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		} // for now eat exceptions
		return "";
	}

	private JSONObject getIPAddresses() throws SocketException, JSONException {
		JSONObject networkJson= new JSONObject();
		List<NetworkInterface> interfaces = Collections.list(NetworkInterface
				.getNetworkInterfaces());
		for (NetworkInterface intf : interfaces) {
			List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
			for (InetAddress addr : addrs) {
				if (!addr.isLoopbackAddress()) {
					String ipAddress = addr.getHostAddress().toUpperCase();
					boolean isIPv4 = InetAddressUtils.isIPv4Address(ipAddress);
					if (isIPv4) {						
						String interfacename = intf.getName();
						networkJson.put(interfacename, ipAddress);
					}
				}
			}
		}
		return networkJson;
	}	
}