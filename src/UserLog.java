import java.awt.*;
import java.sql.*;
import javax.swing.*;
public class UserLog {
     public static Connection con;

     UserLog(){
           //frame
         JFrame frame=new JFrame("User Things");
         frame.setSize(600,500);
         frame.setLayout(null);
         frame.setResizable(false);
         frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        //  frame.setVisible(true);

          Font font=new Font("Arial",Font.BOLD,20);

         //labels
         JLabel User=new JLabel("User Login");
         User.setBounds(250,100,250,30);
         User.setFont(font);
         frame.add(User);

         //AdminId
         JLabel UserID=new JLabel("UserID: ");
         UserID.setBounds(150,180,250,30);
         UserID.setFont(font);
         frame.add(UserID);

         //pasword
         JLabel UserPass=new JLabel("Password: ");
         UserPass.setBounds(150,230,250,30);
         UserPass.setFont(font);
         frame.add(UserPass);


         //TextFeilds
         //Admin
         JTextField Userid=new JTextField();
         Userid.setBounds(300,180,250,30);
         frame.add(Userid);

         //Password
         JTextField Userpass=new JTextField();
         Userpass.setBounds(300,230,250,30);
         frame.add(Userpass);

         //buttons
         JButton signUp=new JButton("SignUp");
         signUp.setBounds(180,330,130,30);
         signUp.setBackground(Color.blue);
         signUp.setForeground(Color.white);
         frame.add(signUp);

         JButton login=new JButton("LogIn");
         login.setBounds(380,330,130,30);
         login.setBackground(Color.blue);
         login.setForeground(Color.white);
         frame.add(login);

         frame.setVisible(true);
     }
     public static void main(String[] args) throws Exception{
          //DB connection
        String dbURL="jdbc:mysql://localhost:3306/javaproject";
         String dbUser="root";
         String dbPassword="root";

         con = DriverManager.getConnection(dbURL,dbUser,dbPassword);
         System.out.println("Connected Successfully");

        new UserLog();

     }
}
