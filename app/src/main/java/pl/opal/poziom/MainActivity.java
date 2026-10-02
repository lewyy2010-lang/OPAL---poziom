package pl.opal.poziom;

import android.app.Activity;
import android.hardware.*;
import android.os.Bundle;
import android.widget.*;
import java.util.Locale;

public class MainActivity extends Activity implements SensorEventListener {
 private SensorManager sm; private Sensor acc;
 private TextView angle,status;
 private float zero=0f, filtered=0f; private boolean first=true;

 @Override public void onCreate(Bundle b){
  super.onCreate(b); setContentView(R.layout.activity_main);
  angle=findViewById(R.id.angle); status=findViewById(R.id.status);
  sm=(SensorManager)getSystemService(SENSOR_SERVICE);
  acc=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
  Button z=findViewById(R.id.zeroButton), r=findViewById(R.id.resetButton);
  if(acc==null){ status.setText("Brak akcelerometru w telefonie"); z.setEnabled(false); r.setEnabled(false); }
  z.setOnClickListener(v->{ zero=filtered; render(0f); });
  r.setOnClickListener(v->{ zero=0f; render(filtered); });
 }
 @Override protected void onResume(){ super.onResume(); if(acc!=null) sm.registerListener(this,acc,SensorManager.SENSOR_DELAY_UI); }
 @Override protected void onPause(){ super.onPause(); sm.unregisterListener(this); }
 @Override public void onSensorChanged(SensorEvent e){
  float x=e.values[0], y=e.values[1];
  float a=(float)Math.toDegrees(Math.atan2(x,-y));
  if(a>90) a-=180; if(a<-90) a+=180;
  if(first){ filtered=a; first=false; } else filtered=filtered*.82f+a*.18f;
  render(filtered-zero);
 }
 private void render(float v){
  v=Math.max(-90f,Math.min(90f,v));
  String s=v>.05f?"+":"";
  angle.setText(String.format(Locale.getDefault(),"%s%.1f°",s,v));
  if(Math.abs(v)<=.2f) status.setText("PION");
  else status.setText(v>0?"Odchylenie w prawo":"Odchylenie w lewo");
 }
 @Override public void onAccuracyChanged(Sensor s,int a){}
}
