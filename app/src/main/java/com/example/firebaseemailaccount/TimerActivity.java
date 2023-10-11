package com.example.firebaseemailaccount;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class TimerActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "timer_channel";
    private static final int NOTIFICATION_ID = 1;

    private EditText editTextTimer;
    private Button buttonStart;
    private CountDownTimer countDownTimer;
    private TextView textViewTimer;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        textViewTimer = findViewById(R.id.textViewTimer);
        editTextTimer = findViewById(R.id.editTextTimer);
        buttonStart = findViewById(R.id.buttonStart);

        buttonStart.setOnClickListener(v -> {
            String input = editTextTimer.getText().toString();
            if (input.isEmpty()) {
                editTextTimer.setError("Please enter a time");
                return;
            }

            long millisInput = Long.parseLong(input) * 60000; // Convert minutes to milliseconds

            startTimer(millisInput);
            editTextTimer.setText("");
        });
    }


    private void startTimer(long milliseconds) {
        countDownTimer = new CountDownTimer(milliseconds, 10) { // 10 milliseconds
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 1000 / 60;
                long seconds = millisUntilFinished / 1000 % 60;
                long milliseconds = millisUntilFinished % 1000;

                String timeLeftFormatted = String.format("%02d:%02d:%03d", minutes, seconds, milliseconds);

                textViewTimer.setText(timeLeftFormatted);
                showNotification(timeLeftFormatted);
            }

            @Override
            public void onFinish() {
                textViewTimer.setText("시간이 다 되었습니다.");
                showNotification("시간이 다 되었습니다.");
            }
        }.start();
    }

    private void showNotification(String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Timer")
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Timer Channel";
            String description = "Channel for Timer Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}