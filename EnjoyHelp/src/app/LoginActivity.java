package app;

import com.enjoyhelp.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class LoginActivity extends Activity{
	private Button login;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		LinearLayout layout=(LinearLayout) findViewById(R.id.login_out);
		layout.getBackground().setAlpha(60);
		login=(Button)findViewById(R.id.login_but);
		login.setOnClickListener(login_BtnListener);
	}
	private View.OnClickListener login_BtnListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
			startActivity(intent);
		}
		
	};
}
