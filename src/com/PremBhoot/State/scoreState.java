package com.PremBhoot.State;

import com.PremBhoot.TileMap.Background;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;

public class scoreState extends State {
    private Background bg;
    private Color titleColor;
    private Font titleFont;
    private Font font;
    private String username, userID;

    private Connection con;
    private PreparedStatement pst, pst2;
    private ResultSet rs, rs2;

    private long timeLevelOne, bestTime;
    public scoreState(GameStateManager gsm, String username, String userID){
        //set parameters as own attribute values
        this.gsm = gsm;
        this.username = username;
        this.userID = userID;

        this.gsm = gsm;
        try {
            //use background class to get background, get fonts
            bg = new Background("/plain-white-background.jpg");


            InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("Lady Radical.ttf");
            assert stream != null;
            titleFont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(54f);

            titleColor = new Color(255, 0, 120);
            //titleFont = new Font("Century Gothic", Font.PLAIN, 32);

            InputStream stream2 = ClassLoader.getSystemClassLoader().getResourceAsStream("gameovercre1.ttf");
            assert stream2 != null;
            font = Font.createFont(Font.TRUETYPE_FONT, stream2).deriveFont(24f);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void update() {

        try {
            //update every 500s, prevents over querying the database when not needed - could alternately just query in init, but
            //this way the top scores can update as players get better scores (times)
            TimeUnit.MILLISECONDS.sleep(500);

            //connect to database and query the time tabel for the users time on level 1 - can add more levels using levels table
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/loginsystem", "root", "");
            String query = "SELECT time FROM leveluser WHERE userID=?";
            pst = con.prepareStatement(query);
            pst.setString(1, userID);
            rs = pst.executeQuery();
            rs.next();
            //create prepared statement and result set initialised as execution of query, get users time for lvl 1

            //get the time for level one
            timeLevelOne = rs.getInt(1);

            String query2 = "SELECT MIN(time) FROM leveluser WHERE levelid=?";
            //new query to get minimum time from leveluser table for leaderboard - new prepared statement and resultset used.
            pst2 = con.prepareStatement(query2);
            pst2.setInt(1, (int) 1);
            rs2 = pst2.executeQuery();
            rs2.next();
            bestTime = rs2.getInt(1);


        }catch (Exception e){
            e.printStackTrace();
            //try catch - print exception
        }
    }

    @Override
    public void keyPressed(int k) {
            if(k== KeyEvent.VK_ESCAPE){
                gsm.setState(gsm.MENUSTATE);
                //if escape is pressed return to menu
            }
    }

    @Override
    public void keyReleased(int k) { //

    }

    @Override
    public void draw(Graphics2D g) {
        //draw title and then draw the users best time (string) and the overall best time for level 1 in correct positions
        bg.draw(g);
        g.setColor(new Color(255, 0, 120));
        g.setFont(titleFont);
        g.drawString("Scores and Times", 200, 65);
        g.setFont(font);

        g.drawString("Level 1 Best Time:", 80, 125);
        if(timeLevelOne==999999){
            g.drawString("Uncompleted", 400, 125);
        } else{
            g.drawString(String.valueOf(timeLevelOne) + " s", 400, 125);
        }
        g.drawString("Worlds Best Time for Level 1:", 80, 165);
        g.drawString(String.valueOf(bestTime) + " s", 470, 165);

    }
}
