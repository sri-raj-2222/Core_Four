import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class UserProfile {
     public static Connection con;

    public UserProfile() {

          try {
         //DB connection
        String dbURL="jdbc:mysql://localhost:3306/javaproject";
         String dbUser="root";
         String dbPassword="root";

         con = DriverManager.getConnection(dbURL,dbUser,dbPassword);
         System.out.println("Connected Successfully"); 

          } catch (Exception e) {

            e.printStackTrace();
          }
           //frame
         JFrame frame=new JFrame("User Things");
         frame.setSize(600,500);
         frame.setLayout(null);
         frame.setResizable(false);
         frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);

          Font font=new Font("Arial",Font.BOLD,20);

         //back button
        JButton back=new JButton("<--");
        back.setBounds(10,10,50,20);
        frame.add(back);

        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                new UserDash();
            }
        });

          frame.setVisible(true);
    }
    public static void main(String[] args) throws Exception{

         new UserMovies();
    }
}
