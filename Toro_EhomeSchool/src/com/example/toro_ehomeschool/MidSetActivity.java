package com.example.toro_ehomeschool;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MidSetActivity extends Activity implements OnClickListener{

	private Button bt_cancel,bt_con;
	private EditText et_tem;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mid_set);
		
		bt_cancel=(Button)this.findViewById(R.id.bt_cancel);
		bt_con=(Button)this.findViewById(R.id.bt_con);
		bt_con.setOnClickListener(this);
		bt_cancel.setOnClickListener(this);
		et_tem=(EditText)this.findViewById(R.id.et_tem);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mid_set, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bt_cancel:

            Intent home=new Intent(Intent.ACTION_MAIN);  
              home.addCategory(Intent.CATEGORY_HOME);  
             startActivity(home);
			break;
		case R.id.bt_con:
			
			
			String mid=et_tem.getText().toString().trim();
			
			if(null==mid||"".equals(mid)){
				Toast.makeText(MidSetActivity.this, "¿¼ÇÚID ²»ÄÜÎª¿Õ", Toast.LENGTH_SHORT).show();
			}else{
				
				Intent data = new Intent();
		        data.putExtra("mid", mid);
		        setResult(RESULT_OK, data);
		        finish();
				
			}
			
			break;

		default:
			break;
		}
		
	}

}
