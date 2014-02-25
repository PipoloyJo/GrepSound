package com.grepsound;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;

public class MainActivity extends Activity {

    private static String TAG = "GrepSound";

    Messenger mService = null;
    boolean mBound;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        getFragmentManager().beginTransaction().replace(R.id.main_frame, new SignInFragment()).commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        bindService(new Intent(this, GrepSoundService.class), mCoverServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mCoverServiceConnection);
            mBound = false;
        }
    }

    private ServiceConnection mCoverServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mBound = false;
        }
    };


//
//    THIS IS THE SOUND OF FREEDOM
//
//      clientID="b45b1aa10f1ac2941910a7f0d10f8e28"
//
//    STEP 1
//     -s : silent, no output
//     -L : follow redirects
//    page=$(curl -s -L --user-agent 'Mozilla/5.0' "$url")
//      DO: get all http page content following redirections
//      STATUS: DONE
//
//    STEP 2
//    -v : Selected lines are those not matching any of the specified patterns
//      discard lines containing "small" as they contain data-sc-track with a different id.
//    -o : Prints only the matching part of the lines.
//    -E : Interpret pattern as an extended regular expression (i.e. force grep to behave as egrep).
//    sort : sort lines
//    uniq : filters identical lines
//
//    id=$(echo "$page" | grep -v "small" | grep -oE "data-sc-track=.[0-9]*" | grep -oE "[0-9]*" | sort | uniq)
//      STATUS: DONE
//
//    STEP 3
//    title=$(echo -e "$page" | grep -A1 "<em itemprop=\"name\">" | tail -n1 | sed 's/\\u0026/\&/g' | recode html..u8)
//
//    STEP 4
//    filename=$(echo "$title".mp3 | tr '*/\?"<>|' '+       ' )
//
//    STEP 5
//    songurl=$(curl -s -L --user-agent 'Mozilla/5.0' "https://api.sndcdn.com/i1/tracks/$id/streams?client_id=$clientID" | cut -d '"' -f 4 | sed 's/\\u0026/\&/g')
//
//    STEP 6
//    artist=$(echo "$page" | grep byArtist | sed 's/.*itemprop="name">\([^<]*\)<.*/\1/g' | recode html..u8)
//
//    STEP 7
//    imageurl=$(echo "$page" | tr ">" "\n" | grep -A1 '<div class="artwork-download-link"' | cut -d '"' -f 2 | tr " " "\n" | grep 'http' | sed 's/original/t500x500/g' | sed 's/png/jpg/g' )
//
//    STEP 8
//    genre=$(echo "$page" | tr ">" "\n" | grep -A1 '<span class="genre search-deprecation-notification" data="/tags/' | tr ' ' "\n" | grep '</span' | cut -d "<" -f 1 | recode html..u8)
//
//    DL
//    -#: display progress bar
//    -o: write output in a file instead of stdout
//
//    curl -# -L --user-agent 'Mozilla/5.0' -o "`echo -e "$filename"`" "$songurl";
//
//    function downsong() { #Done!
//        # Grab Info
//        url="$1"
//        echo "[i] Grabbing song page"
//        if $curlinstalled; then
//                page=$(curl -s -L --user-agent 'Mozilla/5.0' "$url")
//        else
//        page=$(wget --max-redirect=1000 --trust-server-names --progress=bar -U -O- 'Mozilla/5.0' "$url")
//        fi
//                id=$(echo "$page" | grep -v "small" | grep -oE "data-sc-track=.[0-9]*" | grep -oE "[0-9]*" | sort | uniq)
//        title=$(echo -e "$page" | grep -A1 "<em itemprop=\"name\">" | tail -n1 | sed 's/\\u0026/\&/g' | recode html..u8)
//        filename=$(echo "$title".mp3 | tr '*//*\?"<>|' '+       ' )
//        songurl=$(curl -s -L --user-agent 'Mozilla/5.0' "https://api.sndcdn.com/i1/tracks/$id/streams?client_id=$clientID" | cut -d '"' -f 4 | sed 's/\\u0026/\&/g')
//        artist=$(echo "$page" | grep byArtist | sed 's/.*itemprop="name">\([^<]*\)<.*//*\1/g' | recode html..u8)
//        imageurl=$(echo "$page" | tr ">" "\n" | grep -A1 '<div class="artwork-download-link"' | cut -d '"' -f 2 | tr " " "\n" | grep 'http' | sed 's/original/t500x500/g' | sed 's/png/jpg/g' )
//        genre=$(echo "$page" | tr ">" "\n" | grep -A1 '<span class="genre search-deprecation-notification" data="/tags/' | tr ' ' "\n" | grep '</span' | cut -d "<" -f 1 | recode html..u8)
//        # DL
//        echo ""
//        if [ -e "$filename" ]; then
//        echo "[!] The song $filename has already been downloaded..."  && exit
//        else
//        echo "[-] Downloading $title..."
//        fi
//        if $curlinstalled; then
//        curl -# -L --user-agent 'Mozilla/5.0' -o "`echo -e "$filename"`" "$songurl";
//        else
//        wget --max-redirect=1000 --trust-server-names -U 'Mozilla/5.0' -O "`echo -e "$filename"`" "$songurl";
//        fi
//        settags "$artist" "$title" "$filename" "$genre" "$imageurl"
//        echo "[i] Downloading of $filename finished"
//        echo ''
//    }


}
