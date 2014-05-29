package mobi.monaca.framework.plugin;

import java.io.File;
import java.io.IOException;

import mobi.monaca.framework.MonacaPageActivity;
import android.app.Activity;
import fi.iki.elonen.SimpleWebServer;

public class MonacaLocalServer {
	private static final String TAG = MonacaLocalServer.class.getSimpleName();
	private MonacaPageActivity activity;
	private String mAppAssetPath;
	private String fullPath;
	private SimpleWebServer webServer;
	private int port;

	public MonacaLocalServer(Activity activity, String rootDir, int port) {
		
		this.activity = (MonacaPageActivity) activity;

		mAppAssetPath = this.activity.getAppWWWPath();

		fullPath = mAppAssetPath + "/" + removeLeadingSlash(rootDir);
		File fullPathFile = new File(fullPath);
		this.port = port;
		webServer = new SimpleWebServer(null, port, fullPathFile, true);
	}
	
	public void start() throws IOException{
		webServer.start();
	}
	
	public void stop(){
		webServer.stop();
	}

	public String getServerRoot(){
		return fullPath;
	}
	
	public int getPort() {
		return port;
	}

	private String removeLeadingSlash(String string) {
		if(string.startsWith("/")){
			return string.replaceFirst("/", "");
		}
		return string;
	}
}