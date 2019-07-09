package butterknife.test;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.BindView;
import com.example.butterknife.R;
import com.example.butterknife.R2;

class ButteryActivity extends Activity {

  @BindView(R2.id.title) TextView title;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);

  }

}