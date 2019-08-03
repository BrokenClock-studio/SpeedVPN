/*
 * Adapted from OpenVPN for Android
 * Copyright (c) 2012-2013, Arne Schwabe
 * Copyright (c) 2013, Kevin Cernekee
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * In addition, as a special exception, the copyright holders give
 * permission to link the code of portions of this program with the
 * OpenSSL library.
 */

package app.speedvpn;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.VpnService;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.acra.ACRA;
import org.acra.ACRAConfiguration;
import org.acra.ErrorReporter;

import app.speedvpn.R;
import app.speedvpn.core.OpenConnectManagementThread;
import app.speedvpn.core.OpenVpnService;
import app.speedvpn.core.VPNConnector;
import app.speedvpn.fragments.FeedbackFragment;

public class MainActivity extends Activity {

	public static final String TAG = "OpenConnect";

	private ActionBar mBar;

	private ArrayList<TabContainer> mTabList = new ArrayList<TabContainer>();

	private TabContainer mConnectionTab;
	private int mLastTab;
	private boolean mTabsActive;

	private int mConnectionState = OpenConnectManagementThread.STATE_DISCONNECTED;
	private VPNConnector mConn;


	private ImageView CnctStateImage;
	private ImageButton ConnectButton;
	private ImageButton DisConnectButton;
	private TextView Tv_Ip;
	//private TextView Tv_Location;
	public Spinner spinner;
	private String[] ServerNames;
	private String[] ServerUUIDs;
	private String SelectedUUID;
	private boolean RandomNeeded = false;
	private PrefManager prefManager;

	String CrntIp = "";
	int SpinnerIndex = 0;
	Boolean CrntState = false;
	boolean justOpened;
	private ServerPull serverPull;
	private AlertDialog dialog;
	private int RateIndex;
	private boolean disconnectOnAdEnd = false;

	public static MainActivity mainActivity;


	private void SetToArray(String[] s, Set<String> st){

		//s = new  String[st.size()];
		int i=0;
		for (String ss : st) {
			s[i++] = ss;
		}
	}

	

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		mainActivity = this;

		

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width=dm.widthPixels;
		int height=dm.heightPixels;
		double wi=(double)width/(double)dm.xdpi;
		double hi=(double)height/(double)dm.ydpi;
		double x = Math.pow(wi,2);
		double y = Math.pow(hi,2);
		double screenInches = Math.sqrt(x+y);
		if(screenInches < 5){
			findViewById(R.id.logo).setVisibility(View.GONE);
		}
		prefManager = new PrefManager(getBaseContext(), PrefManager.PRF_APP_DATA,PrefManager.MODE_READ);
		RateIndex = prefManager.ReadInt(PrefManager.KEY_RATE_INDEX);

		CnctStateImage = (ImageView)findViewById(R.id.imgCnctState);
		ConnectButton = (ImageButton) findViewById(R.id.btn_connect);
		Tv_Ip = (TextView)findViewById(R.id.tvIp);

		spinner = (Spinner)findViewById(R.id.spinner);
		ServerNames =  DataManager.Server_NameS;
		ServerUUIDs = DataManager.Server_UUIDS;
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
				R.layout.spiner_item,ServerNames);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		justOpened = true;
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				if(i==0){
					RandomNeeded = true;
				}else {
					RandomNeeded = false;
					SelectedUUID = ServerUUIDs[i + 3];
				}

				if(justOpened){
					justOpened = false;
				}else{
					ConnectButton.setImageResource(R.drawable.connect_button);
					CrntState = false;
				}


				SpinnerIndex = i;
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});

		Intent myIntent = getIntent();
		CrntIp = myIntent.getStringExtra("CrntIp");
		CrntState = myIntent.getBooleanExtra("CrntState",false);
		SpinnerIndex = myIntent.getIntExtra("spinnerIndex",0);
		spinner.setSelection(SpinnerIndex);
		if(CrntState){

			//Tv_Location.setText(CrntLocation);

			CnctStateImage.setImageResource(R.drawable.connection);
			ConnectButton.setImageResource(R.drawable.disconnect_button);
		}else {
			CnctStateImage.setImageResource(R.drawable.disconnection);
			ConnectButton.setImageResource(R.drawable.connect_button);
		}
		Tv_Ip.setText("IP: " + CrntIp);


		ConnectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// VpnProfile profile = new VpnProfile();// =(VpnProfile)arrayAdapter.getListAdapter().getItem(position);

				if(CrntState){

					if(RateIndex <3){
						RateIndex++;
						prefManager = new PrefManager(getBaseContext(), PrefManager.PRF_APP_DATA,PrefManager.MODE_WRITE);
						prefManager.SaveIntData(PrefManager.KEY_RATE_INDEX,RateIndex);
						Disconnect();
					}else if(RateIndex < 6){

						ShowRateDialog();

					}
					else if(RateIndex == 420){
						Random rand = new Random();
						int n = rand.nextInt(100);
						if(n>70){
							Disconnect();
						}else {
							new AlertDialog.Builder(MainActivity.this)
									.setCancelable(false)
									.setTitle("Please Support US!")
									.setMessage("We hope you have loved our vpn solution.\n\nNow it's a good time to support us by watching a video. That will help us " +
											"to improve more and add new servers!\n\nWatch and continue...")
									.setPositiveButton("WATCH", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialogInterface, int i) {

											//show ad
										}
									})
									.show();
						}




					}else if(RateIndex >= 6){

						Random rand = new Random();
						int n = rand.nextInt(100);
						if(n>60 && n<80){
							ShowRateDialog();
						}else {
							n = rand.nextInt(100);
							if(n>85){
								Disconnect();
							}else {
								//show ad
							}
						}
					}





				}else{
					//dialog = ProgressDialog.show(getApplicationContext(), "Connecting",
							//"Connecting. Please wait...", true);
					Tv_Ip.setText("Connecting...Please Wait...");
					ConnectButton.setImageAlpha(50);
					ConnectButton.setEnabled(false);

					startVPN();

				}

			}
		});


		mTabsActive = false;
		if (savedInstanceState != null) {
			mLastTab = savedInstanceState.getInt("active_tab");
		}

		mBar = getActionBar();
		mBar.hide();
		//mBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		//mTabList.add(new TabContainer(0, R.string.vpn_list_title, new VPNProfileList()));
		//mTabList.add(new TabContainer(1, R.string.log, new LogFragment()));
		//mTabList.add(new TabContainer(2, R.string.faq, new FaqFragment()));

		//mConnectionTab = mTabList.get(0);

		FeedbackFragment.recordUse(this, false);










	}


	public void ShowRateDialog(){
		dialog = new AlertDialog.Builder(MainActivity.this)
				.setTitle("RATE SPEED VPN")
				.setMessage("Are you enjoying our vpn service?\n\nWe are really working hard to make you " +
						"love our app. Please let us know if you loved our vpn app or not....\n\nThank You.")
				.setPositiveButton("LOVED/GOOD/NOT BAD", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						RateIndex = 420;
						prefManager = new PrefManager(getBaseContext(), PrefManager.PRF_APP_DATA,PrefManager.MODE_WRITE);
						prefManager.SaveIntData(PrefManager.KEY_RATE_INDEX,RateIndex);

						final String appPackageName = getPackageName();
						try {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
						} catch (android.content.ActivityNotFoundException anfe) {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
						}
						Disconnect();
					}
				})
				.setNegativeButton("WORST/BAD", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						new AlertDialog.Builder(MainActivity.this)
								.setCancelable(false)
								.setTitle("Thanks for your feedback!")
								.setPositiveButton("OK", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialogInterface, int i) {

										RateIndex = 420;
										prefManager = new PrefManager(getBaseContext(), PrefManager.PRF_APP_DATA,PrefManager.MODE_WRITE);
										prefManager.SaveIntData(PrefManager.KEY_RATE_INDEX,RateIndex);
										Disconnect();
									}
								})
								.show();
					}
				})
				.setNeutralButton("LATER", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						RateIndex++;
						prefManager = new PrefManager(getBaseContext(), PrefManager.PRF_APP_DATA,PrefManager.MODE_WRITE);
						prefManager.SaveIntData(PrefManager.KEY_RATE_INDEX,RateIndex);
						Disconnect();
					}
				})
				.setCancelable(false)
				.show();
	}

	public void Disconnect(){

					mConn.service.stopVPN();
					mConn = new VPNConnector(getBaseContext(), false) {
						@Override
						public void onUpdate(OpenVpnService service) {
							UpdateUI(service);
						}
					};
	}


	public void UpdateUI(OpenVpnService service){
		int state = service.getConnectionState();
		if (state == OpenConnectManagementThread.STATE_CONNECTED) {

		}else {
			CnctStateImage.setImageResource(R.drawable.disconnection);
			ConnectButton.setImageResource(R.drawable.connect_button);
			prefManager = new PrefManager(getBaseContext(), PrefManager.PRF_APP_DATA,PrefManager.MODE_WRITE);
			prefManager.SaveBoolData(PrefManager.KEY_CONNECTION_STATE,false);
			prefManager.SaveIntData(PrefManager.KEY_CONNECTION_INDEX,SpinnerIndex);
			CrntState = false;
		}

	}

	public void onError(){
		CrntState = false;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				CnctStateImage.setImageResource(R.drawable.disconnection);
				Tv_Ip.setText("Error!" );
				ConnectButton.setImageResource(R.drawable.connect_button);
				ConnectButton.setEnabled(true);
				ConnectButton.setImageAlpha(255);
			}
		});

	}



	public void onConnect(final boolean state){
		CrntState = state;


		runOnUiThread(new Runnable() {
			@Override
			public void run() {




				if(state){

					CnctStateImage.setImageResource(R.drawable.connection);
					Tv_Ip.setText("IP: Loading..." );
					prefManager = new PrefManager(getBaseContext(), PrefManager.PRF_APP_DATA,PrefManager.MODE_WRITE);
					prefManager.SaveBoolData(PrefManager.KEY_CONNECTION_STATE,state);
					prefManager.SaveIntData(PrefManager.KEY_CONNECTION_INDEX,SpinnerIndex);

				}else {
					CnctStateImage.setImageResource(R.drawable.disconnection);
					ConnectButton.setImageResource(R.drawable.connect_button);
				}

//				new Timer().schedule(new TimerTask() {
//					@Override
//					public void run() {
//						// this code will be executed after 2 seconds
//						CallForIp();
//					}
//				}, 1000);

				CallForIp();

			}
		});
	}

	public void CallForIp(){
		try {
			if(isConnected()){
				serverPull = new ServerPull(ServerPull.S_MAIN_ACTIVITY);
				serverPull.execute(ServerPull.URL_GET_IP , ServerPull.TASK_GET_IP_MAIN);
			}else {

				ConnectButton.setImageResource(R.drawable.disconnect_button);
				ConnectButton.setEnabled(true);
				ConnectButton.setImageAlpha(255);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void oNGetIP(String s){
		CrntIp = s;
		Tv_Ip.setText("IP: " + CrntIp );
		ConnectButton.setImageResource(R.drawable.disconnect_button);
		ConnectButton.setEnabled(true);
		ConnectButton.setImageAlpha(255);
	}


	public boolean isConnected() throws InterruptedException, IOException
	{
		String command = "ping -c 1 google.com";
		return (Runtime.getRuntime().exec (command).waitFor() == 0);
	}

	private void reportBadRom(Exception e) {
		ACRAConfiguration cfg = ACRA.getConfig();
		cfg.setResDialogText(R.string.bad_rom_text);
		cfg.setResDialogCommentPrompt(R.string.bad_rom_comment_prompt);
		ACRA.setConfig(cfg);

		ErrorReporter er = ACRA.getErrorReporter();
		er.putCustomData("cause", "reportBadRom");
		er.handleException(e);
	}


	public void  s( String s){

		Toast.makeText( getApplicationContext() , s,
				Toast.LENGTH_SHORT).show();;

	}


	private void startVPN() {
		Intent prepIntent;
		try {
			prepIntent = VpnService.prepare(this);
		} catch (Exception e) {
			reportBadRom(e);
			//finish();
			return;
		}

		if (prepIntent != null) {
			try {
				startActivityForResult(prepIntent, 0);
			} catch (Exception e) {
				reportBadRom(e);
				//finish();
				return;
			}
		} else {
			onActivityResult(0, RESULT_OK, null);
		}
	}

	/* Called by Android OS after user clicks "OK" on VpnService.prepare() dialog */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		setResult(resultCode);


		//UUID mUUID = null;


//			FeedbackFragment.recordProfileAdd(this);
			//mUUID = UUID.fromString(SelectedUUID);

//		String name = "13.234.131.232";
//
//		name = name.replaceAll("\\s", "");
//		if (!name.equals("")) {
//			FeedbackFragment.recordProfileAdd(getBaseContext());
//			mUUID =  ProfileManager.create(name).getUUID();
//		}


		//SelectedUUID = ProfileManager.
		if (resultCode == RESULT_OK) {

			if(RandomNeeded){
				Random rand = new Random();
				int n = rand.nextInt(4);
				SelectedUUID = ServerUUIDs[n];
			}

			Intent intent = new Intent(getBaseContext(), OpenVpnService.class);
			intent.putExtra(OpenVpnService.EXTRA_UUID, SelectedUUID);
			startService(intent);

		}else {
			Tv_Ip.setText("Canceled!");
			ConnectButton.setImageAlpha(255);
			ConnectButton.setEnabled(true);

		}
		// finish();
	}


	@Override
	protected void onSaveInstanceState(Bundle b) {
		super.onSaveInstanceState(b);
		b.putInt("active_tab", mLastTab);
	}

	private void updateUI(OpenVpnService service) {
		int newState = service.getConnectionState();

		service.startActiveDialog(this);

		if (mConnectionState != newState) {
			if (newState == OpenConnectManagementThread.STATE_DISCONNECTED) {
				//mConnectionTab.replace(R.string.vpn_list_title, new VPNProfileList());
			} else if (mConnectionState == OpenConnectManagementThread.STATE_DISCONNECTED) {
				//mConnectionTab.replace(R.string.status, new StatusFragment());
			}
			mConnectionState = newState;
		}

		if (!mTabsActive) {
			// NOTE: addTab may cause mLastTab to change, so cache the value here
			int lastTab = mLastTab;
			for (TabContainer tc : mTabList) {
				mBar.addTab(tc.tab);
				if (tc.idx == lastTab) {
					mBar.selectTab(tc.tab);
				}
			}
			mTabsActive = true;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		mConn = new VPNConnector(this, true) {
			@Override
			public void onUpdate(OpenVpnService service) {
				updateUI(service);
			}
		};
	}



	@Override
	protected void onPause() {
		mConn.stopActiveDialog();
		mConn.unbind();
		super.onPause();
	}

	protected class TabContainer implements ActionBar.TabListener {
		private Fragment mFragment;
		private boolean mActive;
		public Tab tab;
		public int idx;

		public void replace(int titleResId, Fragment frag) {
			if (mActive) {
				getFragmentManager().beginTransaction().remove(mFragment).commit();
			}

			mFragment = frag;
			tab.setText(titleResId);

			if (idx == mLastTab) {
				getFragmentManager().beginTransaction()
					.setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
					.replace(android.R.id.content, mFragment)
					.commit();
				mActive = true;
			} else {
				mActive = false;
			}
		}

		public TabContainer(int idx, int titleResId, Fragment frag) {
			this.idx = idx;
			this.mFragment = frag;
			tab = getActionBar().newTab()
					.setText(titleResId)
					.setTabListener(this);
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			if (mTabsActive) {
				if (idx < mLastTab) {
					ft.setCustomAnimations(R.animator.fragment_slide_right_enter,
							R.animator.fragment_slide_right_exit);
				} else if (idx > mLastTab) {
					ft.setCustomAnimations(R.animator.fragment_slide_left_enter,
							R.animator.fragment_slide_left_exit);
				}
			}

			mLastTab = idx;
			ft.replace(android.R.id.content, mFragment);
			mActive = true;
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (mActive) {
				ft.remove(mFragment);
				mActive = false;
			}
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	}




}
