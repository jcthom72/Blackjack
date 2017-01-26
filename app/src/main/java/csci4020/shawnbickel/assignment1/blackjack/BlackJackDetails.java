package csci4020.shawnbickel.assignment1.blackjack;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class BlackJackDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_jack_details);

        Button extraInfo = (Button) findViewById(R.id.ExtraInformation);

        assert extraInfo != null;
        extraInfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Uri uri = Uri.parse("http://www.bicyclecards.com/how-to-play/blackjack/");
                Intent c = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(c);
            }
        });
    }
}
