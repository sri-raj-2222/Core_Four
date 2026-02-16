import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class AdminLog {
    public static Connection con;

    AdminLog(){

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
         JLabel Admin=new JLabel("Admin Login");
         Admin.setBounds(250,100,250,30);
         Admin.setFont(font);
         frame.add(Admin);

         //AdminId
         JLabel AdminID=new JLabel("AdminID: ");
         AdminID.setBounds(150,180,250,30);
         AdminID.setFont(font);
         frame.add(AdminID);

         //pasword
         JLabel AdminPass=new JLabel("Password: ");
         AdminPass.setBounds(150,230,250,30);
         AdminPass.setFont(font);
         frame.add(AdminPass);


         //TextFeilds
         //Admin
         JTextField Adminid=new JTextField();
         Adminid.setBounds(300,180,250,30);
         frame.add(Adminid);

         //Password
         JTextField Adminpass=new JTextField();
         Adminpass.setBounds(300,230,250,30);
         frame.add(Adminpass);

         //buttons
         JButton login=new JButton("LogIn");
         login.setBounds(380,330,130,30);
         frame.add(login);
     
        //back button
        JButton back=new JButton("<--");
        back.setBounds(10,10,50,20);
        frame.add(back);
         
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                new homePage();
            }
        });

         frame.setVisible(true);
    }
    public static void main(String[] args) throws Exception{

         new AdminLog();

    }
}
