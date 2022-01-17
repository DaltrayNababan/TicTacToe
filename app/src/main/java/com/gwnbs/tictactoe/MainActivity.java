package com.gwnbs.tictactoe;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Global Variable
    private TextView playerOneScore, playerTwoScore, playerStatus;
    private final Button[] buttons = new Button[9];
    private Button resetGame;
    private int playerOneScoreCount, playerTwoScoreCount, rountCount;
    private boolean activePlayer;
    private boolean chooseSign = true;
    private String playerOneSign, playerTwoSign;

    //P1 => 0, P2 => 1, empty => 2
    int[] gameState = {2, 2, 2, 2, 2, 2, 2, 2, 2};

    //Posisi menang (mendatar menurun dan diagonal)
    int[][] winningPositions = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inisialiasi id
        playerOneScore = findViewById(R.id.playerOneScore);
        playerTwoScore = findViewById(R.id.playerTwoScore);
        playerStatus = findViewById(R.id.playerStatus);
        resetGame = findViewById(R.id.resetGame);

        if (chooseSign) dialogChooseSide();
        else startGame();

    }

    private void dialogChooseSide() {
        AlertDialog.Builder chooseSideDialog = new AlertDialog.Builder(this);
        chooseSideDialog.setCancelable(false);
        chooseSideDialog.setTitle("Choose your side");
        chooseSideDialog.setPositiveButton("X", (dialogInterface, i) ->
                readyToPlay(dialogInterface, "X", "O"));
        chooseSideDialog.setNegativeButton("O", (dialogInterface, i) ->
                readyToPlay(dialogInterface, "O", "X"));
        chooseSideDialog.create().show();
    }

    private void readyToPlay(DialogInterface dialogInterface, String signOne, String signTwo) {
        dialogInterface.dismiss();
        playerOneSign = signOne;
        playerTwoSign = signTwo;
        chooseSign = false;
        startGame();
    }

    private void startGame() {
        for (int i = 0; i < buttons.length; i++) {
            String buttonID = "btn_" + i;
            int resourceID = getResources().getIdentifier(buttonID, "id", getPackageName());
            buttons[i] = findViewById(resourceID);
            buttons[i].setOnClickListener(this);
        }

        rountCount = 0;
        playerOneScoreCount = 0;
        playerTwoScoreCount = 0;
        activePlayer = true;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        if (!((Button) v).getText().toString().equals("")) {
            return;
        }
        String buttonID = v.getResources().getResourceEntryName(v.getId());
        int gameStatePointer = Integer.parseInt(buttonID.substring(buttonID.length() - 1//buttonID.length()
        ));

        if (activePlayer) {
            ((Button) v).setText(playerOneSign);
            ((Button) v).setTextColor(Color.parseColor("#FFC34A"));
            gameState[gameStatePointer] = 0;
        } else {
            ((Button) v).setText(playerTwoSign);
            ((Button) v).setTextColor(Color.parseColor("#70FFEA"));
            gameState[gameStatePointer] = 1;
        }
        rountCount++;

        if (checkWinner()) {
            if (activePlayer) {
                playerOneScoreCount++;
                updatePlayerScore();
                Toast.makeText(this, "Player One Won!", Toast.LENGTH_SHORT).show();
            } else {
                playerTwoScoreCount++;
                updatePlayerScore();
                Toast.makeText(this, "Player Two Won!", Toast.LENGTH_SHORT).show();
            }
            playAgain();
        } else if (rountCount == 9) {
            playAgain();
            Toast.makeText(this, "No winner!", Toast.LENGTH_SHORT).show();
        } else {
            activePlayer = !activePlayer;
        }

        if (playerOneScoreCount > playerTwoScoreCount) {
            playerStatus.setText("Player One is Winning!");
        } else if (playerTwoScoreCount > playerOneScoreCount) {
            playerStatus.setText("Player Two is Winning!");
        } else {
            playerStatus.setText("");
        }

        resetGame.setOnClickListener(v1 -> {
            playAgain();
            playerOneScoreCount = 0;
            playerTwoScoreCount = 0;
            playerStatus.setText("");
            updatePlayerScore();
            dialogChooseSide();
        });
    }

    public boolean checkWinner() {
        boolean winnerResult = false;
        for (int[] winningPosition : winningPositions) {
            if (gameState[winningPosition[0]] == gameState[winningPosition[1]] &&
                    gameState[winningPosition[1]] == gameState[winningPosition[2]] && gameState[winningPosition[0]] != 2) {
                winnerResult = true;
                break;
            }
        }
        return winnerResult;
    }

    @SuppressLint("SetTextI18n")
    public void updatePlayerScore() {
        playerOneScore.setText(Integer.toString(playerOneScoreCount));
        playerTwoScore.setText(Integer.toString(playerTwoScoreCount));
    }

    public void playAgain() {
        rountCount = 0;
        activePlayer = true;

        for (int i = 0; i < buttons.length; i++) {
            gameState[i] = 2;
            buttons[i].setEnabled(false);
            int i2 = i;
            buttons[i].animate().alpha(0f).setDuration(500).withEndAction(() -> {
                        buttons[i2].setText("");
                        buttons[i2].animate().alpha(1f).setDuration(1000).withEndAction(() ->
                                buttons[i2].setEnabled(true)).start();
                    }
            ).start();
        }
    }
}