import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;


public class UserDash {
    public static Connection con;
    UserDash(){

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
       //Frame
       JFrame frame=new JFrame("User Dashboard");
       frame.setSize(600, 500);
       frame.setLayout(null);
       frame.setResizable(false);
       frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
       //    frame.setVisible(true);

       Font font=new Font("Arial",Font.BOLD,20);

       //buttons
       JButton Movies=new JButton("Movies");
       Movies.setBounds(200,100,200,30);
       Movies.setFont(font);
       frame.add(Movies);

       //Bookings
       JButton Bookings=new JButton("Bookings");
       Bookings.setBounds(200,180,200,30);
       Bookings.setFont(font);
       frame.add(Bookings);

       //Profile
       JButton Profile=new JButton("Profile");
       Profile.setBounds(200,260,200,30);
       Profile.setFont(font);
       frame.add(Profile);

       //logout
       JButton Logout=new JButton("LogOut");
       Logout.setBounds(200,340,200,30);
       Logout.setFont(font);
       frame.add(Logout);

        //back button
        JButton back=new JButton("<--");
        back.setBounds(10,10,50,20);
        frame.add(back);

        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                new UserLog();
            }
        });

        Movies.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e){
            new UserMovies();
          }
        });

        Bookings.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e){
            new UserBookings();
         }
        });

        Profile.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e){
            new UserProfile();
         }
        });

        Logout.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e){
            new homePage();
         }
        });

       frame.setVisible(true);

    }
    public static void main(String[] args) throws  Exception{

         new UserDash();

    }
}
