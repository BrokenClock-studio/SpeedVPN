package app.speedvpn;

import android.app.Activity;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import org.acra.ACRA;
import org.acra.ACRAConfiguration;
import org.acra.ErrorReporter;

import java.util.UUID;

import app.openconnect.api.GrantPermissionsActivity;
import app.speedvpn.core.OpenVpnService;
import app.speedvpn.core.ProfileManager;
import app.speedvpn.fragments.FeedbackFragment;

public class HomeActivity extends Activity {


    private ImageButton ConnectButton;
    private ImageButton DisConnectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ConnectButton = (ImageButton) findViewById(R.id.btn_connect);

        ConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // VpnProfile profile = new VpnProfile();// =(VpnProfile)arrayAdapter.getListAdapter().getItem(position);
                startVPN();
            }
        });


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

        String name = "3.130.20.60";
        UUID mUUID = null;
        name = name.replaceAll("\\s", "");
        if (!name.equals("")) {
            FeedbackFragment.recordProfileAdd(getBaseContext());
            mUUID = ProfileManager.create(name).getUUID();
        }

        if (resultCode == RESULT_OK) {
            Intent intent = new Intent(getBaseContext(), OpenVpnService.class);
            intent.putExtra(OpenVpnService.EXTRA_UUID, mUUID.toString());
            startService(intent);

        }
       // finish();
    }
}
