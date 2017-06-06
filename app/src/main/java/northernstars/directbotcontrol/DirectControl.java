package northernstars.directbotcontrol;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.VerticalSeekBar;

import java.net.InetAddress;

public class DirectControl extends AppCompatActivity implements Runnable{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_direct_control);

        Toolbar vToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(vToolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu( Menu aMenu ) {
        getMenuInflater().inflate(R.menu.menu_direct_control, aMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem aItem) {
        int id = aItem.getItemId();

        if (id == R.id.action_close) {
            mIsRunning = false;
            finish();
            return true;
        }

        return super.onOptionsItemSelected(aItem);
    }

    ServerConnection mServerConnection;
    boolean mIsRunning = false;

    public void connectToNetwork(View aView){
        if( aView.getTag() == "1" ) {
            mIsRunning = false;
        } else {
            ConnectivityManager vConnectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo vNetworkInfo = vConnectionManager.getActiveNetworkInfo();
            if (vNetworkInfo != null && vNetworkInfo.isConnected()) {
                if(!mIsRunning) {
                    new Thread(this).start();
                }
            }
        }

    }

    private void setNetworkConnectionButton( final boolean aConnection ){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button vNetworkConnectButton = (Button) findViewById(R.id.bNetworkConnect);
                if( aConnection ) {
                    vNetworkConnectButton.setText(R.string.network_disconnect);
                    vNetworkConnectButton.setTag("1");
                } else {
                    vNetworkConnectButton.setText(R.string.network_connect);
                    vNetworkConnectButton.setTag("0");
                }
            }
        });
    }

    @Override
    public void run() {
        mIsRunning = true;
        setNetworkConnectionButton(true);

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        try {

            mServerConnection = new ServerConnection( InetAddress.getByName(((EditText) findViewById(R.id.eTNetworkIP)).getText().toString()), Integer.parseInt(((EditText) findViewById(R.id.eTNetworkPort)).getText().toString()) );
            mServerConnection.connectToServer( ((EditText) findViewById(R.id.eTNetworkName)).getText().toString(), Integer.parseInt(((EditText) findViewById(R.id.eTNetworkRCID)).getText().toString()), Integer.parseInt(((EditText) findViewById(R.id.eTNetworkVTID)).getText().toString()) );

        } catch ( Exception vException ){
            // I Know. But... I KNOW!
        }

        int vWheelVelocityLeft = 0, vWheelVelocityRight = 0, vOldWheelVelocityLeft = 0, vOldWheelVelocityRight = 0;

        while(mIsRunning){

            try {

                if( mServerConnection.isConnected() ){

                    vWheelVelocityLeft = (((VerticalSeekBar) findViewById(R.id.sBDirectControlLeft)).getProgress() - 100);
                    vWheelVelocityRight = (((VerticalSeekBar) findViewById(R.id.sBDirectControlRight)).getProgress() - 100);

                    if( vWheelVelocityLeft != vOldWheelVelocityLeft || vWheelVelocityRight != vOldWheelVelocityRight) {
                        mServerConnection.sendDatagramm("<command> <wheel_velocities> <right>" + vWheelVelocityRight + "</right> <left>" + vWheelVelocityLeft + "</left> </wheel_velocities> </command>");

                        vOldWheelVelocityLeft = vWheelVelocityLeft;
                        vOldWheelVelocityRight = vWheelVelocityRight;
                    }

                    Thread.sleep(25);

                } else {
                    mIsRunning = false;
                }

            } catch ( Exception vException ){
                // I Know. But... I KNOW!
            }
        }

        mServerConnection.closeConnection();
        setNetworkConnectionButton(false);
    }

    public static class NetworkFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater aInflater, ViewGroup aContainer, Bundle aSavedInstanceState) {
            return aInflater.inflate(R.layout.fragment_network_control, aContainer, false);
        }

    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager aFragmentManager) {
            super(aFragmentManager);
        }

        @Override
        public Fragment getItem(int aPosition) {
            switch (aPosition%2) {
                case 0:
                    return new DirectControlFragment();
                case 1:
                    return new NetworkFragment();
            }

            return new DirectControlFragment();

        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int aPosition) {
            switch (aPosition) {
                case 0:
                    return "Direct Control";
                case 1:
                    return "Network";
            }
            return null;
        }
    }
}
