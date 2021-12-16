package com.example.tictactoe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, IView {
    private static final String FIXED_PASSWORD = "12345678";
    private LinearLayout llMainDynamic;
    private IPresenter presenter;
    private EditText etMail;
    private int num=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         initViews();
        createBoard();

        // create presenter
        presenter = new Presenter(this);


    }

    private void initViews()
    {
        llMainDynamic=findViewById(R.id.llDynamic);
        etMail = findViewById(R.id.etMail);

    }

    private void createBoard()
    {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height =metrics.heightPixels;
      //  Toast.makeText(this, "width&height are  " +width + " , " + height, Toast.LENGTH_SHORT).show();

        // we will create the board in the following way:
        //   1/5  margin   3/5 board   1/5 margin
        LinearLayout linearLayoutBoard = new LinearLayout(this);
        linearLayoutBoard.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams boardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        boardParams.setMargins(0,height/10,0,0);

        linearLayoutBoard.setLayoutParams(boardParams );

        //linearLayoutBoard.setGravity(Gravity.CENTER);

        // define layoutparams for each row
        LinearLayout.LayoutParams rowLayout= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rowLayout.setMargins(width/5,1,width/5,1);

        // define rows and cols - can get from user
        int rows = 3;
        int cols=3 ;
        // create a new row  horizontal linear layout
        // define lp for each element in the board
        LinearLayout.LayoutParams elementLayout= new LinearLayout.LayoutParams(width/5, width/5);
        LinearLayout rowInBoard;
        for(int i=0;i<rows;i++)
        {
            rowInBoard = new LinearLayout(this);
            rowInBoard.setLayoutParams(rowLayout);
            rowInBoard.setOrientation(LinearLayout.HORIZONTAL);
            for (int j=0;j<cols;j++)
            {
                Button b = new Button(this);
                b.setLayoutParams(elementLayout);
                b.setTag(i +"" + j);
           //     b.setText(i+ "," + j);
                b.setOnClickListener(this);
                rowInBoard.addView(b);
            }
            //add the row to the layout
            linearLayoutBoard.addView(rowInBoard);
        }
        llMainDynamic.addView(linearLayoutBoard);
    }

    @Override
    public void onClick(View view) {
        String tag = view.getTag().toString();
      //  Toast.makeText(this,tag,Toast.LENGTH_SHORT).show();
        // pass button row and col to presenter
        int row = tag.charAt(0)-'0';
        int col = tag.charAt(1)-'0';
        // disable user click
        ((Button)view).setText(""+'X');
        ((Button)view).setClickable(false);
        this.presenter.userClick(row,col);

    }

    @Override
    public void displayEndGame(String message) {

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            // display the message received from the presenter
            alertDialog.setTitle(message);
            alertDialog.setMessage("Would you like to play again?");
            alertDialog.setIcon(R.drawable.trophy1);

            // wait for user feedback
            alertDialog.setCancelable(false);

            // in case clicked YES
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.this.presenter.restartGame();
                    dialog.dismiss();
                }
            });
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.this.finish();
                    dialog.dismiss();
                }
            });

            AlertDialog dialog= alertDialog.create();
          // Setting dialog transparent and bottom
            Window window = dialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));


            // show dialog
        dialog.show();
        }


    @Override
    public void markButton(int i, int j, char letter)
    {
        View parentView = findViewById( R.id.llDynamic );
        Button button = parentView.findViewWithTag(i+""+j);
        button.setText(""+letter);
        button.setClickable(false);

    }

    @Override
    public void displayMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();

    }
    @Override
    public void clearBoard() {
        ViewGroup parentView = findViewById(R.id.llDynamic);

        // clear all button data and set clickable to true
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                Button button = parentView.findViewWithTag(i+""+j);
                button.setText("");
                button.setClickable(true);
            }
        }
    }

    public void register(View view)
    {
        num = (int)(Math.random()*1000);
        String email = etMail.getText().toString();
        email = num+email;


        String password = FIXED_PASSWORD;
        presenter.userRegister(email,password);
    }

    public void login(View view) {
        String email = etMail.getText().toString();
        email = num+email;
        String password = FIXED_PASSWORD;
        presenter.userLogin(email,password);

    }
}