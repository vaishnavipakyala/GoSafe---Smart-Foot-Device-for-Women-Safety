package com.example.vaish.womensafetyapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.BeanManager;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.Callback;
import com.punchthrough.bean.sdk.message.DeviceInfo;
import com.punchthrough.bean.sdk.message.ScratchBank;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Bean b1;
    List<Bean> beans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        beans = new ArrayList<>();

        BeanDiscoveryListener listener = new BeanDiscoveryListener() {
            @Override
            public void onBeanDiscovered(Bean bean, int rssi) {
                beans.add(bean);
                Toast.makeText(MainActivity.this, "BEAN ADDED", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDiscoveryComplete() {
                Integer i = 0;
                for (Bean bean : beans) {
                    if (i == 0) {
                        b1 = bean;
                        i++;
                    }
                    System.out.println(bean.getDevice().getName());   // "Bean"              (example)
                    System.out.println(bean.getDevice().getAddress());    // "B4:99:4C:1E:BC:75" (example)
                }
                System.out.println("Discovery complete");
                System.out.println(b1.getDevice().getName());   // "Bean"              (example)
                System.out.println(b1.getDevice().getAddress());
                BeanListener beanListener = new BeanListener() {
                    @Override
                    public void onConnected() {
                        Toast.makeText(MainActivity.this, "Connected to bean", Toast.LENGTH_SHORT).show();
                        b1.readDeviceInfo(new Callback<DeviceInfo>() {
                            @Override
                            public void onResult(DeviceInfo deviceInfo) {
                                System.out.println(deviceInfo.hardwareVersion());
                                System.out.println(deviceInfo.firmwareVersion());
                                System.out.println(deviceInfo.softwareVersion());
                            }
                        });
                    }

                    @Override
                    public void onConnectionFailed() {
                        Toast.makeText(MainActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDisconnected() {
                        Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSerialMessageReceived(byte[] data) {
                        Toast.makeText(MainActivity.this, "Serial Message Received", Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this,""+data, Toast.LENGTH_SHORT).show();
                        sendSMSMessage();
                        try {
                            TimeUnit.MINUTES.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onScratchValueChanged(ScratchBank bank, byte[] value) {
                        Toast.makeText(MainActivity.this, "Scratch Value Changed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(BeanError error) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReadRemoteRssi(int rssi) {
                        Toast.makeText(MainActivity.this, "Read Remote Rssi", Toast.LENGTH_SHORT).show();
                    }
                };
                b1.connect(getApplicationContext(),beanListener);
            }
        };

        BeanManager.getInstance().startDiscovery(listener);

    }
    protected void sendSMSMessage() {
        Log.i("Send SMS", "");
        String phoneNo = new String("9789564268");
        String message = new String("Help me! I am in danger!");
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo,null,"hi",null,null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
