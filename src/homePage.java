import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class homePage {
     public static Connection con;

    public homePage() {

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
        //  frame.setVisible(true);

         Font font=new Font("Arial",Font.BOLD,20);

         //labels
         JLabel role=new JLabel("Select you Role");
         role.setBounds(250,100,250,30);
         role.setFont(font);
         frame.add(role);

         //Admin
         JButton Admin=new JButton("Admin Login");
         Admin.setBounds(200,200,250,30);
         Admin.setFont(font);
         frame.add(Admin);

         //User
         JButton User=new JButton("User Login");
         User.setBounds(200,280,250,30);
         User.setFont(font);
         frame.add(User);

         Admin.addActionListener(new ActionListener(){
               public void actionPerformed(ActionEvent e){
                    if(e.getSource()==Admin){
                         new AdminLog();
                    }

               }
         });
         User.addActionListener(new ActionListener(){
               public void actionPerformed(ActionEvent e){
                    if(e.getSource()==User){
                         new UserLog();
                    }

               }
         });

         frame.setVisible(true);
    } 
     public static void main(String[] args) throws Exception{
        
          new homePage();
     }
}
