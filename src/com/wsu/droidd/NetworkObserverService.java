package com.wsu.droidd;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class NetworkObserverService extends Service {

	private static final String TAG = "NetworkObserverService";

	private static final String PACKAGE_DIR = "/data/data/com.wsu.droidd/";
	private DatabaseHandler dh;
	private String localIp = "";

	Handler toastHandler = new Handler();
	Runnable toastIp = new Runnable() {
		public void run() {
			Toast.makeText(NetworkObserverService.this.getApplicationContext(), "Yo",
					Toast.LENGTH_LONG).show();
		}
	};

	public NetworkObserverService() {
		dh = new DatabaseHandler(this.getBaseContext());
	}

	@Override
	public void onCreate() {
		super.onCreate();
		dh = new DatabaseHandler(this.getApplicationContext());

		// Log.d(TAG, "Start");
		// Set up all our stuff!

		tcpdumpSetup();
		// Run the command to monitor traffic.
		new Thread(new Runnable() {

			@Override
			public void run() {
				// Log.d(TAG, "Running Tcpdump");
				runCommand(PACKAGE_DIR + "tcpdump", true);
			}
		}).start();
		// Log.d(TAG, "After the run.");
		// Service is now running. This will grab data. We want to store this.

	}

	private void tcpdumpSetup() {
		// Make sure Tcpdump exists.
		File file = new File(PACKAGE_DIR + "tcpdump");
		Log.d(TAG, "Setup start");
		if (!file.exists()) {
			// Log.d(TAG, "File doesn't exist, makeing.");
			// Export it from Assets.
			if (!copyFileFromAssets("tcpdump")) {
				Log.e(TAG, "File not found: " + "tcpdump");
			}
		}
		// Log.d(TAG, "chmoding.");
		runCommand("chmod 777 " + PACKAGE_DIR + "tcpdump", false);
	}

	/*
	 * Run a command in super user mode. Requires root. From:
	 * http://stackoverflow
	 * .com/questions/6896618/read-command-output-inside-su-process
	 */
	public String runCommand(String cmd, boolean isOutput) {
		try {
			Process p = Runtime.getRuntime().exec(new String[] { "su", "-c", "system/bin/sh" });

			DataOutputStream stdin = new DataOutputStream(p.getOutputStream());
			// from here all commands are executed with su permissions
			stdin.writeBytes(cmd + "\n"); // \n executes the command
			if (isOutput) {
				// Only the tcpdump command should get in here.
				BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line = "";
				while ((line = r.readLine()) != null) // Process output line
				{
					// This loop is ran when tcpdump receives a packet.
					int i = 0;
					while (localIp.equals("")) {
						if (i++ == 10) {
							toastHandler.post(toastIp);
							// If we can't find the current IP of the devie,
							// shut down service.
							this.stopSelf();
						}
						getLocalIpAddress();
					}
					// --------------------------------------------------
					// In here is just processing of the tcpdump output.
					// Output of one:
					// 21:31:32.455845 IP 130.108.206.230.34357 >
					// 74.125.133.108.993: Flags [P.], ack 3601985445, win 5126,
					// options [nop,nop,TS[|tcp]>
					String name = getCurrent(); // Gets app in foreground as
												// that's the likely source.
												// Obviously this should change
												// and become more specific
												// based on ports if it's not an
												// official port (1-500 or w/e)
												// using lsof
					// Matcher match =
					// Pattern.compile("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b.\\d{1,5}\\b").matcher(line);
					line = line.substring(14);
					String main = line.substring(line.indexOf("IP") + 3, line.indexOf(":"));
					String one = main.split(">")[0].replaceAll(" ", "");
					String two = main.split(">")[1].replaceAll(" ", "");
					/*
					 * String tcpdump[] = line.split(" "); // Spot 2 and 4 have
					 * // ip/port. One is // local. String one = "", two = "";
					 * String oneS[] = tcpdump[2].split("\\."); String twoS[] =
					 * tcpdump[4].split("\\."); for (i = 0; i < oneS.length - 1;
					 * i++) { one += oneS[i].replaceAll("[^0-9]", ""); two +=
					 * twoS[i].replaceAll("[^0-9]", ""); if (i != oneS.length -
					 * 2) { one += "."; two += "."; } } one += ":" +
					 * oneS[oneS.length - 1]; two += ":" + twoS[twoS.length -
					 * 1];
					 */
					String port = (one.contains(localIp)) ? two : one;
					String ip = port.replaceAll("\\.[0-9]{1,5}$", "");
					port = port.replaceAll(ip + "\\.", "");
					// --------------------------------------------------
					// If this is a proper ip v4 address, then add it. ip6 is
					// not yet implemented.
					ArrayList<Proc> temp = dh.getProc(name);
					boolean isNewIp = true;
					for (Proc t : temp) {
						if (t.getIp().equals(ip)) {
							// If the IP exists. continue. We don't want to
							// add anything.
							isNewIp = false;
							break;
						}
					}
					if (isNewIp) {
						// If it's a new IP and we want to add it to the
						// list, do this stuff.
						Proc tempProc = new Proc(ip, port, name);
						dh.addProc(tempProc);
						Log.d(TAG, "Adding: " + tempProc.getName() + " " + tempProc.getIp());
					}
				}
				Log.e(TAG, "Huge error");
				return "ERROR"; // Should never be reached.
			} else {
				return "";
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "";

		}
	}

	/*
	 * From:
	 * http://stackoverflow.com/questions/13406180/how-to-get-local-ip-and-
	 * display-it-in-textview
	 * http://stackoverflow.com/questions/9850080/android-4-0-3-getting-local-ip
	 */
	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
					.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
						.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
						return (localIp = inetAddress.getHostAddress().toString());
					}
				}
			}
		} catch (SocketException ex) {
			Log.e(TAG, ex.toString());
		}
		return localIp;
	}

	/*
	 * Returns the application's package name in foreground.
	 */
	private String getCurrent() {
		ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		// get the info from the currently running task
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		ComponentName componentInfo = taskInfo.get(0).topActivity;
		return componentInfo.getPackageName();
	}

	/*
	 * 
	 */
	private boolean copyFileFromAssets(String filename) {
		AssetManager assetManager = getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
			// Log.e(TAG, "Failed to get asset file list.", e);
		}
		for (String fn : files) {
			if (fn.equals(filename)) {
				InputStream in = null;
				OutputStream out = null;
				try {
					in = assetManager.open(fn);
					out = new FileOutputStream(PACKAGE_DIR + fn);
					copyFile(in, out);
					in.close();
					in = null;
					out.flush();
					out.close();
					out = null;

				} catch (IOException e) {
					// Log.e(TAG, "Failed to copy asset file tcpdump: " + e);
				}
				return true;
			}
		}
		return false;
	}

	/*
	 * Copy the asset file to the directory.
	 */
	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// We're dieing! Set up alarm manager to restart us in a bit when things
		// cool down ;).
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
