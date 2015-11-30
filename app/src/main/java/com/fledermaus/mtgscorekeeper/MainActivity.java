package com.fledermaus.mtgscorekeeper;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends ActionBarActivity {
    private static int INITIAL_TEXT_SIZE       = 250;
    private static int TEXT_SIZE_FACTOR        = 2;
    private static int TEXT_SIZE_DECREMENT     = 10;
    private static int ROLLING_DELAY           = 150;
    private static int ROLLING_DURATION        = 1200;
    private static int ROLLING_RESULT_DURATION = 3000;

    RelativeLayout player1Background;
    RelativeLayout player2Background;
    TextView       player1Life;
    TextView       player2Life;
    TextView       player1Roll;
    TextView       player2Roll;
    boolean        rolling = false;
    int            rollingCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player1Background = (RelativeLayout)findViewById(R.id.player1);
        player2Background = (RelativeLayout)findViewById(R.id.player2);
        player1Life       = (TextView)findViewById(R.id.player1Life);
        player2Life       = (TextView)findViewById(R.id.player2Life);
        player1Roll       = (TextView)findViewById(R.id.player1Roll);
        player2Roll       = (TextView)findViewById(R.id.player2Roll);

        autoAdjustTextSize(player1Background, player1Life);
        autoAdjustTextSize(player2Background, player2Life);
    }

    /* Only works at init */
    private void autoAdjustTextSize(final RelativeLayout background, final TextView life) {
        background.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                background.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                final int bgh = background.getHeight();

                life.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        if (life.getHeight() >= bgh / TEXT_SIZE_FACTOR)
                            life.setTextSize(TypedValue.COMPLEX_UNIT_PX, life.getTextSize() - TEXT_SIZE_DECREMENT);
                        //else
                        //    life.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void adjustLife(View view) {
        int id = view.getId();

        try {
            if (id == R.id.player1Add) {
                Integer life = Integer.parseInt(player1Life.getText().toString()) + 1;
                player1Life.setText(life.toString());
            }
            else if (id == R.id.player1Subtract) {
                Integer life = Integer.parseInt(player1Life.getText().toString()) - 1;
                player1Life.setText(life.toString());
            }
            else if (id == R.id.player2Add) {
                Integer life = Integer.parseInt(player2Life.getText().toString()) + 1;
                player2Life.setText(life.toString());
            }
            else if (id == R.id.player2Subtract) {
                Integer life = Integer.parseInt(player2Life.getText().toString()) - 1;
                player2Life.setText(life.toString());
            }
        }
        catch (NumberFormatException e) {}
    }

    public void reset(View view) {
        Random r = new Random();
        player1Background.setBackgroundColor(Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
        player2Background.setBackgroundColor(Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
        player1Life.setTextSize(TypedValue.COMPLEX_UNIT_PX, INITIAL_TEXT_SIZE);
        player2Life.setTextSize(TypedValue.COMPLEX_UNIT_PX, INITIAL_TEXT_SIZE);
        player1Life.setText("20");
        player2Life.setText("20");
    }

    public void roll(View view) {
        if (!rolling) {
            rolling        = true;
            rollingCounter = 0;
            final Random r = new Random();
            final Handler handler = new Handler();

            player1Roll.setText(Integer.toString(r.nextInt(6) + 1));
            player2Roll.setText(Integer.toString(r.nextInt(6) + 1));
            player1Roll.setTextColor(Color.BLACK);
            player2Roll.setTextColor(Color.BLACK);
            player1Roll.setVisibility(View.VISIBLE);
            player2Roll.setVisibility(View.VISIBLE);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rollingCounter += ROLLING_DELAY;
                    if (rollingCounter < ROLLING_DURATION) {
                        int o1 = Integer.parseInt(player1Roll.getText().toString());
                        int o2 = Integer.parseInt(player2Roll.getText().toString());
                        int n1;
                        int n2;

                        do {
                            n1 = r.nextInt(6) + 1;
                        }
                        while (o1 == n1);
                        do {
                            n2 = r.nextInt(6) + 1;
                        }
                        while (o2 == n2);

                        player1Roll.setText(Integer.toString(n1));
                        player2Roll.setText(Integer.toString(n2));
                        handler.postDelayed(this, ROLLING_DELAY);
                    }
                    else {
                        int d1;
                        int d2;

                        do {
                            d1 = r.nextInt(6) + 1;
                            d2 = r.nextInt(6) + 1;
                        }
                        while (d1 == d2);

                        player1Roll.setText(Integer.toString(d1));
                        player2Roll.setText(Integer.toString(d2));
                        if (d1 > d2) {
                            player1Roll.setTextColor(Color.GREEN);
                            player2Roll.setTextColor(Color.RED);
                        }
                        else {
                            player1Roll.setTextColor(Color.RED);
                            player2Roll.setTextColor(Color.GREEN);
                        }

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                player1Roll.setVisibility(View.INVISIBLE);
                                player2Roll.setVisibility(View.INVISIBLE);
                                rolling = false;
                            }
                        }, ROLLING_RESULT_DURATION);
                    }
               }
            }, ROLLING_DELAY);
        }
    }
}