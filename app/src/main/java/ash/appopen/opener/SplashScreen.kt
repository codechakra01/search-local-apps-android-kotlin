package ash.appopen.opener

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import ash.appopen.opener.MainActivity

class SplashScreen : AppCompatActivity() {
    /* access modifiers changed from: protected */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, MainActivity::class.java))
    }
}