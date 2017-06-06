package northernstars.directbotcontrol;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.JoystickMovedListener;
import android.widget.JoystickView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VerticalSeekBar;

import java.util.Locale;

public class DirectControlFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater aInflater, ViewGroup aContainer,
                             Bundle aSavedInstanceState) {
        return aInflater.inflate(R.layout.fragment_direct_control, aContainer, false);
    }

    @Override
    public void onViewCreated( final View aView, Bundle aSavedInstanceState ){

        JoystickView joystick = (JoystickView) aView.findViewById(R.id.sBDirectControlJoystick);
        joystick.setMovementRange(71);

        joystick.setOnJostickMovedListener(new JoystickMovedListener() {
            @Override
            public void OnMoved(int aPan, int aTilt) {

                if (aPan > 0) {
                    ((VerticalSeekBar) aView.findViewById(R.id.sBDirectControlLeft)).setProgress((int) ((double) -aTilt / 71 * 100) + 100);
                    ((VerticalSeekBar) aView.findViewById(R.id.sBDirectControlRight)).setProgress((int) (((100 - (double) aPan) / (100 + aPan)) * (double) -aTilt / 71 * 100) + 100);
                } else if (aPan < 0) {
                    ((VerticalSeekBar) aView.findViewById(R.id.sBDirectControlLeft)).setProgress((int) (((100 + (double) aPan) / (100 - aPan)) * (double) -aTilt / 71 * 100) + 100);
                    ((VerticalSeekBar) aView.findViewById(R.id.sBDirectControlRight)).setProgress((int) ((double) -aTilt / 71 * 100) + 100);
                } else {
                    ((VerticalSeekBar) aView.findViewById(R.id.sBDirectControlLeft)).setProgress((int) ((double) -aTilt / 71 * 100) + 100);
                    ((VerticalSeekBar) aView.findViewById(R.id.sBDirectControlRight)).setProgress((int) ((double) -aTilt / 71 * 100) + 100);
                }

            }

            @Override
            public void OnReleased() {

            }

            @Override
            public void OnReturnedToCenter() {
                ((VerticalSeekBar) aView.findViewById(R.id.sBDirectControlRight)).setProgress(100);
                ((VerticalSeekBar) aView.findViewById(R.id.sBDirectControlLeft)).setProgress(100);
            }
        });

        SeekBar vSeekBarLeft = (SeekBar) aView.findViewById(R.id.sBDirectControlLeft);
        vSeekBarLeft.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar aSeekBar, int aProgress, boolean aFromUser) {
                ((TextView) aView.findViewById(R.id.tVDirectControlLeftValue)).setText(String.format(Locale.getDefault(),"%d",(aProgress-100)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar aSeekBar) {
                ((TextView) aView.findViewById(R.id.tVDirectControlLeftValue)).setText(String.format(Locale.getDefault(),"%d",(aSeekBar.getProgress()-100)));

            }

            @Override
            public void onStopTrackingTouch(SeekBar aSeekBar) {
                ((TextView) aView.findViewById(R.id.tVDirectControlLeftValue)).setText(String.format(Locale.getDefault(),"%d",(aSeekBar.getProgress()-100)));
            }
        });

        SeekBar vSeekBarRight = (SeekBar) aView.findViewById(R.id.sBDirectControlRight);
        vSeekBarRight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar aSeekBar, int aProgress, boolean aFromUser) {
                ((TextView) aView.findViewById(R.id.tVDirectControlRightValue)).setText(String.format(Locale.getDefault(),"%d",(aProgress-100)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar aSeekBar) {
                ((TextView) aView.findViewById(R.id.tVDirectControlRightValue)).setText(String.format(Locale.getDefault(),"%d",(aSeekBar.getProgress()-100)));

            }

            @Override
            public void onStopTrackingTouch(SeekBar aSeekBar) {
                ((TextView) aView.findViewById(R.id.tVDirectControlRightValue)).setText(String.format(Locale.getDefault(),"%d",(aSeekBar.getProgress()-100)));
            }
        });
    }
}