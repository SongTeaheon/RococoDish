package com.rococodish.front_ui.FCM;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rococodish.front_ui.Notice.NoticeActivity;
import com.rococodish.front_ui.R;
import com.rococodish.front_ui.SubActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String TAG = "TAGMyFirebaseMsgService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //받은 메시지의 값이 있을 때
        if(remoteMessage.getData().size() >0){

            Log.d(TAG, "FCM 메시지 데이터 payload : " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }
        }

        //알림이 왔을 때
        if (remoteMessage.getNotification() != null){
            Log.d(TAG, "알림의 내용입니다 : " + remoteMessage.getNotification().getBody());

            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }

    }
    //오래 걸리는 작업이 들어있는 메세지(예를 들어, 예약메시지, 우리는 사용안할것이니 패스)
    private void scheduleJob() {
        Log.d(TAG, "예약메시지 입니다.");
//        // [START dispatch_job]
//        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
//                .build();
//        WorkManager.getInstance().beginWith(work).enqueue();
//        // [END dispatch_job]
    }
    //10초안에 처리가능한 메세지는 걍 로그만 띄움.
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }


    //새로운 폰에서 킬 때마다 새로운 토큰이 제공되는데 그럴때마다
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        Log.d(TAG, "새로 만들어진 토큰 : " + token);

//        sendRegistrationToServer(token); //토큰을 이제 사용자 데이터베이스에 저장함.
    }
//    private void sendRegistrationToServer(String token) {
//        //받은 토큰을 파이어스토어 사용자 필드에 저장함. 토큰이 갱신될때마다 실행.
//    }

    //어떤 방식으로 메시지를 보낼지 설정
    private void sendNotification(String messageTitle, String messageBody) {
        Intent intent = new Intent(this, NoticeActivity.class);//원래는 알림함으로 이동시킴.(일단 서브액티비티로 지정)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);//채널 아이디는 임의로 만들어줌.
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


}
