package csci4020.shawnbickel.assignment1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import csci4020.shawnbickel.assignment1.blackjack.R;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button PlayGame = (Button) findViewById(R.id.PlayGame);
        Button MoreInformation = (Button) findViewById(R.id.LearnMore);

        assert PlayGame != null;
        PlayGame.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent a  = new Intent (MenuActivity.this, GameActivity.class);
                startActivity(a);
            }
        });

        assert MoreInformation != null;
        MoreInformation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent b  = new Intent (MenuActivity.this, BlackJackDetails.class);
                startActivity(b);
            }
        });
    }
}
