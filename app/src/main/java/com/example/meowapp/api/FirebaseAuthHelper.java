package com.example.meowapp.api;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirebaseAuthHelper {

    private static final String FIREBASE_CREDENTIALS_JSON = "{\n" +
            "  \"type\": \"service_account\",\n" +
            "  \"project_id\": \"appngonngu\",\n" +
            "  \"private_key_id\": \"b712778ac39ebba28887a6f3df25a44051c1c893\",\n" +
            "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEugIBADANBgkqhkiG9w0BAQEFAASCBKQwggSgAgEAAoIBAQCnqO7VnWgPavup\\n/1vdwVDIkTNuuT0Pt1TR3hEWRiMrnD/QMQTbj9br976puqyM/FPgPTXkXe29Dh3I\\nk6x4CslVgfEvPUrJf1+9oOvhylbIAlrOoVeQJB+x8cnI581LWiJbKbvdpIUfpCrx\\nxGHmWgaXmREBmmCzzR4tlz8bfh+oaJ2ZgjJClc6LBBvoBH6570bjtNmQ7PLF83B2\\nuCy3Y2RwCB84WqlDyl/PciIbpUl4uyHlqTgp+asriNQRZistztX9brNOAeTlFKKB\\nE2zmb9cRovXgyresV4WWOTRPzw0gl5hwA5xLjfl7TGKg+cXHa+OmHgAHDK0Cqqgn\\nNCizfRCpAgMBAAECgf9qGH0KsJEpMIoRCYcYzTxRr5DcgFwjleAW7m/hqICZGEdJ\\nd7ubNWrL5/q/bWHD1ek9/X4dBVNsT4KJaum5DRpbAd+eBStOV0w+s5aRS3mfF95e\\n47qFfFaSRG9ss0M2a9NR3RcFKxcS7tA+LgfFvTJuvG4GlaqcHTD7qLpGq4ldB8/7\\n+C9d2A4MZ1FjVa5tN9q/21vant1Way9DBJeJYK3/ePTl0DXuBTg9GKoiuER1VUz/\\n3inW0wBo1YooqM6MRzPP2VX1auVN1TZD3h4dpt5gGWiGLXOd6hi82rBURhZo/glf\\nKQs0ntE4zXqDqnFpui6StzAZZMeEBd3rugQ+PikCgYEA3IhDNYJAPyO8pNn1BNlc\\nOKAOOGOf8tL+XP5wbGaGA3ZXjjEfZQUhmMnv7Zx1DzycmMtHMkmO39xMeOKtr4YF\\n+A2zWIDCSIRtS+s52sHxguuDkwmnvUNVD6WYCLV6Si5PTqFtZTSh6O4/IPQoAJc5\\n1f7vVBRXSTCUK2jTcBSYXw8CgYEAwp/QGj61SgrmfbSJC6Mag0DyT3YGVVk5yB93\\nY8kC9oFZuYeYVHQFxpy2gY/2s6Hi3ekg+bKcCJHdnyTsr3nSvjBw2YaG87soGmGT\\nb2c6ZxGhTuRu3kpoDJz0P9suLVKNHPwaTKKDu3eHaK4r6WjQt9hU+axCOUwUTWzY\\n6zyCFMcCgYA5cakIFCCGEdn+muOO2suB2Hc7yv+L8dfeA0znf6Ei372YxUTnPHnu\\n4ZMEsr9vM5ua1UB7Ydu8hhbJWkphsSh236K6x/dTimMp5mDQkbpGHvDL6TTvQM20\\nYK8EfbkX/44a7kOfnCEcmoqJG/Abj0i6bEiM/ytpXiUKVajVxheWkwKBgEcPxRMg\\nHF95lWkGlxdpqU+NUiwTQwK8vm1El/oWNf+9gP5N9R+48Tbwl269Gu3ByS0PZxvk\\nzDYQ1GO8PrNffsQ0ua+i//lUN7NSN1vwzBFADa7Zl2WQs2cPk9sQ805XGvEfV5bt\\nq72ySf29wojJ3ZOS6Iv1CoUTM0KF4LobcL+XAoGAN/kOyir2Nx72Gyujfb7OmOPf\\nsQWZL6W8HioGkd4Zz64rgcGs5zM38suM4hUJ+iDwYIR4NfLlAUp6KHszWXJYMCnr\\nZtmMd9rIz97WFa/L3r+ug8zgy5uHubWR0dq3ynoqf2JSC6N9hOzLjVFvdtBmj2wa\\njEZdtcPBQPzsqjDu4qw=\n-----END PRIVATE KEY-----\\n\",\n" +
            "  \"client_email\": \"firebase-adminsdk-nm08n@appngonngu.iam.gserviceaccount.com\",\n" +
            "  \"client_id\": \"103436568702636641822\",\n" +
            "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
            "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
            "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
            "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-nm08n%40appngonngu.iam.gserviceaccount.com\",\n" +
            "  \"universe_domain\": \"googleapis.com\"\n" +
            "}";

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void getAccessToken(final FirebaseAuthCallback callback) {
        executor.execute(() -> {
            try {
                ByteArrayInputStream serviceAccountStream = new ByteArrayInputStream(FIREBASE_CREDENTIALS_JSON.getBytes());
                GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream)
                        .createScoped("https://www.googleapis.com/auth/firebase.messaging");
                credentials.refreshIfExpired();
                String token = credentials.getAccessToken().getTokenValue();

                // Use handler to post result to the main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    // Gọi callback với kết quả
                    if (callback != null) {
                        callback.onTokenReceived(token);
                    }
                });
            } catch (IOException e) {
                Log.e("FCM", "Lỗi khi lấy access token", e);
            }
        });
    }

    // Định nghĩa callback interface để xử lý kết quả
    public interface FirebaseAuthCallback {
        void onTokenReceived(String token);
    }
}
