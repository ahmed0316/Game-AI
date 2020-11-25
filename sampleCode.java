/*SAMPLE CODE FROM CHESS AI PROJECT*/

package chess;

import BasicIO.BasicForm;

/**
 * @author Ahmed Mohamed
 * COSC 3P71 PROJECT. Dec, 2019.
 * The main class. Incorporates the UI.
 */

public class Chess {

    private Board board;              //Depth may be changed in Board Class
    
    /*GUI AND GAME STATE*/
    private BasicForm out;            //GUI Display
    private int button;               //GUI Button
    private boolean youWon, youLost, gameWon, running, isDraw, humanTurn;//bools
    private String[] aiMove;
        
    /*MOVE VALIDATION*/
    private moveValidator moveCheck;
    
    
    private Chess(){
        out = new BasicForm("Play Turn","Restart","Quit"); //initialize gui
        board = new Board();    //create new initial board
        setUpGUI();         //set up GUI
        
        isDraw = false;     //board is not a draw at start
        youWon = false;  //human won
        youLost = false; //AI won
        gameWon = false; //someone won the game
        running = true;
        
        moveValidator v = new moveValidator();
        
        while(running == true){
   
            /* Button 0 = Play Turn
               Button 1 = Restart
               Button 2 = Quit       */
            
            button = out.accept();
            
            /*Play Pressed and No Winner Yet*/
            if (button == 0 && !board.humanWon() && !board.AIwon() ){
                
                moveCheck = new moveValidator();
                
                /*PLAY HUMAN TURN*/
                humanTurn = true;       //its now human's turn
                
                String curr = out.readString("curr");
                String next = out.readString("next");
                curr = cleanUpInput(curr);  //fix capitlization/whitespace
                next = cleanUpInput(next);
                
                /*Play human turn and check if successful*/
                if ( board.playTurn(curr,next,false) ){
                    //System.out.println("running");
                    //Update UI//
                    out.writeString(next,out.readString(curr)); //move 
                    out.clear(curr);                            //delete
                    out.writeString("msg","Game Ongoing");
                    
                    humanTurn = false;  //its now AI's turn
                    
                    checkForWinner();   //check if game won
                    
                    if ( !gameWon ){
                        aiMove = board.playAITurn();
                        String aiCurr = aiMove[0];
                        String aiNext = aiMove[1];
                        //System.out.println("("+aiCurr+", "+aiNext+") .");
                        out.writeString(aiNext, out.readString(aiCurr));
                        out.clear(aiCurr);
                    } //if not, AI can play

                    checkForWinner();   //check again
                    
                    out.clear("curr");
                    out.writeString("curr",next);
                    out.clear("next");
                   

                }
                else{ //IF HUMAN MOVE FAILED
                    notifyInvalidMove();    //notify user of invalid move
                }
            }
            
            if ( button == 0 && isDraw && gameWon==false ) {//Gameover(tie)
                out.clear("msg");
                out.clear("curr");
                out.clear("next");
                out.setEditable("curr", false);
                out.setEditable("next", false);
                out.writeString("msg","No Winner. Play Again?");
            } //Tie: board is full and no one won
            
            if (button == 0 && gameWon == true){  //Game over (someone won)
                out.clear("msg");
                out.clear("curr");
                out.clear("next");
                out.setEditable("curr", false);
                out.setEditable("next", false);
                if (youWon == true){
                    //System.out.println("running");
                    out.writeString("msg","You Won. Play Again?");
                }
                if (youLost == true){
                    out.writeString("msg","You Lost. Play Again?");
                }
            }
            
            if (button == 1){ //Restart pressed
                out.close();
                Chess chess = new Chess(); //new board created
            }
            
            if (button == 2){ //Quit
                out.close();     //close
                running = false; //stop program
            }  
        } //while
    }
    
     private void setUpGUI(){
        /*GUI Setup and Text*/
        out.setTitle("Chess");
        out.addTextField("instructions", "Instructions: Chess. Human is Upper"
                + "case, AI is Lowercase", 0);
        out.setEditable("instructions",false); //make field uneditable to user
        out.addTextField("curr", "Piece Coordinates (ie A4):", 2);
        out.newLine();
        out.addTextField("next", "Move To Coordinates:", 2);
        out.addTextField("msg", "Message: ", 20);
        out.setEditable("msg", false);
        out.writeString("msg","Game Ongoing");
        out.newLine();
        
        //Set up fields (holes) where chess pieces go
        char c;
        String slotName;
        String rowName, colName;
        
        for(int i = 0 ; i < 9 ; i++ ){
            for(int j = 0 ; j < 8 ; j++){
               /*SET UP LEGEND*/
               if ( i == 0 ){
                  /*SET UP ALPHABET LEGEND (A,B,C,D,E,F,G,H ABOVE BOARD)*/
                  rowName = "row";
                  rowName = rowName.concat(Integer.toString(j+1));//row1, row2..
                  c = (char) (j+65);            //Get A, B, C...
                  out.addTextField(rowName,2,33*j+10,127); //create field
                  out.writeChar(rowName,c);         //Write A, B, C...
                  out.setEditable(rowName, false);  //set uneditable
                  
                  /*SET UP NUMERIC LEGEND (1,2,3,4.. BESIDE BOARD)*/
                  colName = "col";
                  colName = colName.concat(Integer.toString(j+1));//col1, col2..
                  out.addTextField(colName,2,310,33*j+200); //create fields
                  out.setEditable(colName, false);          //set uneditable
                  out.writeInt(colName,8-j);                //write 1,2,3...
               }
               /*SET UP BOARD*/
               else{
                  /*CREATE ACTUAL BOARD*/
                  c = (char) (j+65);   //A, B, C, D, E, F...
                  slotName = String.valueOf(c);    //A/B/C/D..
                  slotName = slotName.concat(Integer.toString(9-i));//A1,A2,B3..
                  out.addTextField(slotName,2,33*j+10,167+(i*33)); //label field
                  out.setEditable(slotName, false);  
               //   out.writeString(slotName,slotName);
               }
               
            } //for
        } //for
        
        /*WRITE AI PIECES*/
        for ( Piece p : board.getAllPieces() ){
            out.writeString( p.getLocation(), p.getValue() );
        }
        
    } //setUpGUI
     
     private void checkForWinner(){
        //CHECK FOR A WINNER//
        if ( board.checkIfWon() ){  //if someone won
            if ( board.AIwon() ){   //if AI won
                youWon = false;
                youLost = true;
                gameWon = true;
            }
            if ( board.humanWon() ){ //if human won
                youWon = true;
                youLost = false;
                gameWon = true;
            }

        }
        /*CHECK FOR A STALEMATE*/
        if ( board.checkIfStalemate() ){
            gameWon = false;
            isDraw = true;
            youWon = false;
            youLost = false;
        }
     }
     
     private String cleanUpInput(String str){
         
         if ( str == null ) return str;
         if ( str.isEmpty() ) return str;
         
         str = str.trim();  //remove whitespace
         
        /*If input given is lower case, make upper case*/
        char[] c;
        if ( Character.isLowerCase(str.charAt(0)) ){     //If lower case
            c = str.toCharArray();
            c[0] = Character.toUpperCase(str.charAt(0)); //Make upper case
            c[1] = str.charAt(1);                        
            str = String.valueOf(c);                     //Remake string
        }
        
        return str;
     } //remove whitespace and enforce capitalization on input
    
     private void notifyInvalidMove(){
        out.clear("curr");
        out.clear("next");
        out.writeString("msg","Invalid Move");
     }
    
    public static void main(String[] args) {
        Chess c = new Chess();
//        Test t = new Test();
    }
    
}
