import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import de.bezier.guido.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Minesweeper extends PApplet {


public static int NUM_ROWS = 20;
public static int NUM_COLS = 20;
private MSButton[][] buttons;
private ArrayList <MSButton> bombs = new ArrayList <MSButton>();
private boolean gameOver = false;

public void setup ()
{
    size(400, 400);
    textAlign(CENTER,CENTER);
    // make the manager
    
    Interactive.make( this );
    
    // create some buttons

    buttons = new MSButton[NUM_ROWS][NUM_COLS];
    
    for(int r = 0; r < NUM_ROWS; r++)
        for(int c = 0; c < NUM_COLS; c++)   
            buttons[r][c]=new MSButton(r, c);
    setBombs();
}
public void setBombs()
{
    while(bombs.size() < 20)
    {
        int r = (int)(Math.random() * NUM_ROWS);
        int c = (int)(Math.random() * NUM_COLS);
        if(!bombs.contains(buttons[r][c]))
        {
            bombs.add(buttons[r][c]);
        }
    }
}

public void draw ()
{
    background( 0 );
    if(isWon() && !gameOver)
    {
        displayWinningMessage();
        gameOver = true;
    }
}
public boolean isWon()
{
    for(int r = 0; r < NUM_ROWS; r++)
        for(int c = 0; c < NUM_COLS; c++)   
            if(!buttons[r][c].isMarked() && !buttons[r][c].isClicked())
                return false;
    return true;
}
public void displayLosingMessage()
{
    for(int r = 0; r < NUM_ROWS; r++)
        for(int c = 0; c < NUM_COLS; c++)   
            if(bombs.contains(buttons[r][c]) && c != 8 && c != 9)
                buttons[r][c].setLabel("!!");
                                 
    String message = new String("... Really??...");
    for(int i = 0; i < message.length(); i++ )
    {
        int r;
        int k = 0;
        int w = 0;
        if (i > 40) {
             r = 10; 
        }
        else if (i> 20) {
            r = 9;
        }
        else {
            r= 8;
        }
        
        if (r == 8) {
            buttons[r][i].clicked = true;
        if(!bombs.contains(buttons[r][i]))
            bombs.add(buttons[r][i]);
        buttons[r][i].setLabel(message.substring(i,i+1));
             bombs.add(buttons[r][i]);

        }
        if (r==9) {
            buttons[r][k].clicked = true;
        if(!bombs.contains(buttons[r][k]))
            bombs.add(buttons[r][k]);
        buttons[r][i].setLabel(message.substring(i,k+1));
             bombs.add(buttons[r][k]);
             k = k+1;
        }
         if (r== 10) {
            buttons[r][i].clicked = true;
        if(!bombs.contains(buttons[r][i]))
            bombs.add(buttons[r][i]);
        buttons[r][i].setLabel(message.substring(i,i+1));
             bombs.add(buttons[r][w]);
             w = w+1;
        }
       
    }
}
public void displayWinningMessage()
{
    String message = new String("You passed a basic, level one, minesweeper game, are you really proud of yourself?");
    for(int i = 0; i < message.length(); i++ )
    {
        buttons[9][i+6].clicked = true;
        if(!bombs.contains(buttons[9][i]))
            bombs.add(buttons[9][i]);
        buttons[9][i].setLabel(message.substring(i,i+1));
    }
}

public class MSButton
{
    private int r, c;
    private float x,y, width, height;
    private boolean clicked, marked;
    private String label;
    
    public MSButton ( int rr, int cc )
    {
        width = 400/NUM_COLS;
        height = 400/NUM_ROWS;
        r = rr;
        c = cc; 
        x = c*width;
        y = r*height;
        label = "";
        marked = clicked = false;
        Interactive.add( this ); // register it with the manager
    }
    public boolean isMarked()
    {
        return marked;
    }
    public boolean isClicked()
    {
        return clicked;
    }
    // called by manager
    
    public void mousePressed () 
    {
        if(gameOver) return;
        clicked = true;
        if(keyPressed)
            marked = !marked;
        else if(bombs.contains(this))
        {
            displayLosingMessage();
            gameOver = true;
        }
        else if(countBombs(r,c) > 0)
            label = "" + countBombs(r,c);
        else
        {
           if(isValid(r-1,c) && !buttons[r-1][c].clicked)
              buttons[r-1][c].mousePressed();
           if(isValid(r+1,c) && !buttons[r+1][c].clicked)
              buttons[r+1][c].mousePressed();
           if(isValid(r,c-1) && !buttons[r][c-1].clicked)
              buttons[r][c-1].mousePressed();
           if(isValid(r,c+1) && !buttons[r][c+1].clicked)
              buttons[r][c+1].mousePressed();
           if(isValid(r-1,c+1) && !buttons[r-1][c+1].clicked)
              buttons[r-1][c+1].mousePressed();
           if(isValid(r+1,c+1) && !buttons[r+1][c+1].clicked)
              buttons[r+1][c+1].mousePressed();
           if(isValid(r-1,c-1) && !buttons[r-1][c-1].clicked)
              buttons[r-1][c-1].mousePressed();
           if(isValid(r+1,c-1) && !buttons[r+1][c-1].clicked)
              buttons[r+1][c-1].mousePressed();
        }
    }

    public void draw () 
    {    
        if (marked)
            fill(70,204,245);
        else if( clicked && bombs.contains(this) ) 
            fill(245,129,35);
        else if(clicked)
            fill(7,88,26);
        else 
            fill( 243,76,253 );

        rect(x, y, width, height);
        fill(0);
        text(label,x+width/2,y+height/2);
    }
    public void setLabel(String newLabel)
    {
        label = newLabel;
    }
    public boolean isValid(int r, int c)
    {
        return r>=0 && r < NUM_ROWS && c >= 0 && c < NUM_COLS;
    }
    public int countBombs(int row, int col)
    {
        int numBombs = 0;
        if(isValid(row-1,col) && bombs.contains(buttons[row-1][col]))
            numBombs++;
        if(isValid(row+1,col) && bombs.contains(buttons[row+1][col]))
            numBombs++;
        if(isValid(row-1,col-1) && bombs.contains(buttons[row-1][col-1]))
            numBombs++;
        if(isValid(row+1,col-1) && bombs.contains(buttons[row+1][col-1]))
            numBombs++;
        if(isValid(row,col-1) && bombs.contains(buttons[row][col-1]))
            numBombs++;
        if(isValid(row-1,col+1) && bombs.contains(buttons[row-1][col+1]))
            numBombs++;
        if(isValid(row+1,col+1) && bombs.contains(buttons[row+1][col+1]))
            numBombs++;
        if(isValid(row,col+1) && bombs.contains(buttons[row][col+1]))
            numBombs++;
        return numBombs;
    }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Minesweeper" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
