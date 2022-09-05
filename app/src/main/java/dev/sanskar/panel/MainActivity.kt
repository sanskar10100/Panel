package dev.sanskar.panel

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (intent.data != null) {

            Toast.makeText(
                this,
                "Your quiz code is: ${intent?.data?.getQueryParameter("code")}, but this feature is not available yet",
                Toast.LENGTH_SHORT)
                .show()
        }
    }
}