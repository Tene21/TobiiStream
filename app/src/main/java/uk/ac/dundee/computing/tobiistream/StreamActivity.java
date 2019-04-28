package uk.ac.dundee.computing.tobiistream;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;


import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class StreamActivity  extends Activity implements IVLCVout.Callback    {
    public final static String TAG = "StreamActivity";
    public String studyName;
    public String participantName;

    // display surface
    private SurfaceView mSurface;
    private SurfaceHolder holder;

    // media player
    private LibVLC libvlc;
    private MediaPlayer mMediaPlayer = null;


    private MediaPlayer.EventListener mPlayerListener = new MyPlayerListener(this);

    private String rtspUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);


        //big buck bunny test rtspUrl = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";
        rtspUrl = "rtsp://192.168.71.50:8554/live/scene";
        Log.d(TAG, "Playing " + rtspUrl);

        mSurface = findViewById(R.id.surface);
        holder = mSurface.getHolder();


        ArrayList<String> options = new ArrayList<>();
        options.add("--aout=opensles");
        options.add("--audio-time-stretch"); // time stretching
        options.add("-vvv"); // verbosity
        options.add("--aout=opensles");
        options.add("--avcodec-codec=h264");
        options.add("--file-logging");
        options.add("--logfile=vlc-log.txt");


        libvlc = new LibVLC(getApplicationContext(), options);
        holder.setKeepScreenOn(true);

        // Create media player
        mMediaPlayer = new MediaPlayer(libvlc);
        mMediaPlayer.setEventListener(mPlayerListener);
        //mMediaPlayer.getVLCVout().setWindowSize(-Math.abs(mSurface.getWidth()),-Math.abs(mSurface.getHeight()));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        ViewGroup.LayoutParams videoParams = mSurface.getLayoutParams();
        videoParams.width = displayMetrics.widthPixels;
        videoParams.height = displayMetrics.heightPixels;

        IVLCVout vout = mMediaPlayer.getVLCVout();
        vout.setVideoView(mSurface);
        vout.setWindowSize(videoParams.width,videoParams.height);
        vout.addCallback(this);
        vout.attachViews();

        Media m = new Media(libvlc, Uri.parse(rtspUrl));

        mMediaPlayer.setMedia(m);
        mMediaPlayer.play();

    }



    @Override
    protected void onResume() {
        super.onResume();
        // createPlayer(mFilePath);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {

    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    public void releasePlayer() {
        if (libvlc == null)
            return;
        mMediaPlayer.stop();
        final IVLCVout vout = mMediaPlayer.getVLCVout();
        vout.removeCallback(this);
        vout.detachViews();
        holder = null;
        libvlc.release();
        libvlc = null;
    }
    public void newStudy(final View view){
        hideStudyButtons();
        EditText newStudyName = findViewById(R.id.studyName);
        TextView pickNewStudy = findViewById(R.id.pickNewStudy);
        newStudyName.setVisibility(View.VISIBLE);
        newStudyName.setText("");
        pickNewStudy.setVisibility(View.VISIBLE);
        showStudyNameConfirm();
    }

    public void oldStudy(final View view){
        //pull list of studies from glasses, parse as List, create ArrayAdapter from the list and set the adapter on the spinner
        hideStudyButtons();
        Spinner oldStudyName = findViewById(R.id.oldStudyName);
        TextView pickOldStudy = findViewById(R.id.pickExistingStudy);
        oldStudyName.setVisibility(View.VISIBLE);
        pickOldStudy.setVisibility(View.VISIBLE);
        showStudyNameConfirm();
    }
    public void newParticipant(final View view){
        hideParticipantButtons();
        EditText newParticipantName = findViewById(R.id.participantName);
        TextView pickNewParticipant = findViewById(R.id.pickNewParticipant);
        pickNewParticipant.setVisibility(View.VISIBLE);
        newParticipantName.setVisibility(View.VISIBLE);
        newParticipantName.setText("");
        showParticipantConfirm();
    }
    public void oldParticipant(final View view){
        //pull list of participants from glasses, parse as List, create ArrayAdapter from the list and set the adapter on the spinner
        hideParticipantButtons();
        Spinner oldParticipant = findViewById(R.id.oldParticipantName);
        TextView pickOldParticipant = findViewById(R.id.pickExistingParticipant);
        pickOldParticipant.setVisibility(View.VISIBLE);
        oldParticipant.setVisibility(View.VISIBLE);
        showParticipantConfirm();
    }
    public void showParticipantConfirm(){
        Button participantConfirm = findViewById(R.id.confirmParticipantName);
        participantConfirm.setVisibility(View.VISIBLE);
    }
    public void hideStudyButtons(){
        Button oldStudy = findViewById(R.id.existingStudyButton);
        Button newStudy = findViewById(R.id.newStudyButton);
        TextView pickStudy = findViewById(R.id.pickAStudy);
        oldStudy.setVisibility(View.INVISIBLE);
        newStudy.setVisibility(View.INVISIBLE);
        pickStudy.setVisibility(View.INVISIBLE);
    }
    public void showParticipantButtons(){
        Button oldParticipant = findViewById(R.id.oldParticipantButton);
        Button newParticipant = findViewById(R.id.newParticipantButton);
        TextView pickAParticipant = findViewById(R.id.pickAParticipant);
        pickAParticipant.setVisibility(View.VISIBLE);
        oldParticipant.setVisibility(View.VISIBLE);
        newParticipant.setVisibility(View.VISIBLE);
    }

    public void hideParticipantButtons(){
        Button oldParticipant = findViewById(R.id.oldParticipantButton);
        Button newParticipant = findViewById(R.id.newParticipantButton);
        TextView pickAParticipant = findViewById(R.id.pickAParticipant);
        pickAParticipant.setVisibility(View.INVISIBLE);
        oldParticipant.setVisibility(View.INVISIBLE);
        newParticipant.setVisibility(View.INVISIBLE);
    }
    public void showStudyNameConfirm(){
        Button studyNameConfirm = findViewById(R.id.confirmStudyName);
        studyNameConfirm.setVisibility(View.VISIBLE);
    }

    public void studyConfirm(final View view){
        EditText newStudyName = findViewById(R.id.studyName);
        Spinner oldStudyName = findViewById(R.id.oldStudyName);
        Button studyNameConfirm = findViewById(R.id.confirmStudyName);
        if(newStudyName.getText().toString().isEmpty() && oldStudyName.getSelectedItem().toString().isEmpty()){
            System.out.println("No input provided");
        }
        else if(!newStudyName.getText().toString().isEmpty())
        {
            TextView pickNewStudy = findViewById(R.id.pickNewStudy);
            pickNewStudy.setVisibility(View.INVISIBLE);
            studyName = newStudyName.getText().toString();
            System.out.println("study name: " + studyName);
            newStudyName.setVisibility(View.INVISIBLE);
            studyNameConfirm.setVisibility(View.INVISIBLE);
            //showParticipantButtons();
            //new study means new participants, skip straight to new participant.
            newParticipant(view);
        }
        else if (!oldStudyName.getSelectedItem().toString().isEmpty())
        {
            TextView pickOldStudy = findViewById(R.id.pickExistingStudy);
            pickOldStudy.setVisibility(View.INVISIBLE);
            studyName = oldStudyName.getSelectedItem().toString();
            System.out.println("study name: " + studyName);
            oldStudyName.setVisibility(View.INVISIBLE);
            studyNameConfirm.setVisibility(View.INVISIBLE);
            showParticipantButtons();
        }
    }

    public void participantConfirm(final View view){
        EditText newParticipantName = findViewById(R.id.participantName);
        Spinner oldParticipantName = findViewById(R.id.oldParticipantName);
        Button participantNameConfirm = findViewById(R.id.confirmParticipantName);
        if(newParticipantName.getText().toString().isEmpty() && oldParticipantName.getSelectedItem().toString().isEmpty()){
            System.out.println("No input provided");
        }
        else if(!newParticipantName.getText().toString().isEmpty())
        {
            TextView pickNewParticipant = findViewById(R.id.pickNewParticipant);
            pickNewParticipant.setVisibility(View.INVISIBLE);
            participantName = newParticipantName.getText().toString();
            System.out.println("participant name: " + participantName);
            newParticipantName.setVisibility(View.INVISIBLE);
            participantNameConfirm.setVisibility(View.INVISIBLE);
            //showCalibrateButton();
        }
        else if (!oldParticipantName.getSelectedItem().toString().isEmpty())
        {
            TextView pickOldParticipant = findViewById(R.id.pickExistingParticipant);
            pickOldParticipant.setVisibility(View.INVISIBLE);
            participantName = oldParticipantName.getSelectedItem().toString();
            System.out.println("study name: " + participantName);
            oldParticipantName.setVisibility(View.INVISIBLE);
            participantNameConfirm.setVisibility(View.INVISIBLE);
            //showCalibrateButton();
        }
    }
    public void updateGazeCoords(float xCoords, float yCoords){
        RelativeLayout rlMain = findViewById(R.id.videoArea);
        ImageView reticle = findViewById(R.id.gazeReticle);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSurface.getLayoutParams();
        reticle.setX(xCoords * params.width);
        reticle.setY(yCoords * params.height);
    }

    public void showGaze(){
        //call once tracking is established
        ImageView reticle = findViewById(R.id.gazeReticle);
        reticle.setVisibility(View.VISIBLE);
    }
    public void hideGaze(){
        //call if/when tracking is lost
        ImageView reticle = findViewById(R.id.gazeReticle);
        reticle.setVisibility(View.INVISIBLE);
    }
}