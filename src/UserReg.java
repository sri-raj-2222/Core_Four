import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class UserReg {
    public static Connection con;

    public UserReg() {
             //DB connection
        String dbURL="jdbc:mysql://localhost:3306/javaproject";
         String dbUser="root";
         String dbPassword="root";

         try {
             con = DriverManager.getConnection(dbURL,dbUser,dbPassword);
            System.out.println("Connected Successfully");
         } catch (Exception e) {
            e.printStackTrace();
         }

         //frame
         JFrame frame=new JFrame("User Registration");
         frame.setSize(600,500);
         frame.setLayout(null);
         frame.setResizable(false);
         frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        //  frame.setVisible(true);

          Font font=new Font("Arial",Font.BOLD,20);

         //labels
         JLabel UserRegistration=new JLabel("User Regstration");
         UserRegistration.setBounds(250,20,250,30);
         UserRegistration.setFont(font);
         frame.add(UserRegistration);
         
          //Password
         JLabel UserName=new JLabel("Name: ");
         UserName.setBounds(100,80,250,30);
         UserName.setFont(font);
         frame.add(UserName);

        JLabel Email=new JLabel("Email: ");
         Email.setBounds(100,130,250,30);
         Email.setFont(font);
         frame.add(Email);

         JLabel Phone=new JLabel("Phone Number: ");
         Phone.setBounds(100,180,250,30);
         Phone.setFont(font);
         frame.add(Phone);

         JLabel Age=new JLabel("Age: ");
         Age.setBounds(100,230,250,30);
         Age.setFont(font);
         frame.add(Age);

         //Password
         JLabel Password=new JLabel("Password: ");
         Password.setBounds(100,280,250,30);
         Password.setFont(font);
         frame.add(Password);

         //Conform pasword
         JLabel ConformPass=new JLabel("Conform Password: ");
         ConformPass.setBounds(100,330,250,30);
         ConformPass.setFont(font);
         frame.add(ConformPass);


         //TextFeilds
         JTextField Namefield=new JTextField();
         Namefield.setBounds(300,80,250,30);
         frame.add(Namefield);

         //email
         JTextField emailfeild=new JTextField();
         emailfeild.setBounds(300,130,250,30);
         frame.add(emailfeild);

         JTextField Phonefeild=new JTextField();
         Phonefeild.setBounds(300,180,250,30);
         frame.add(Phonefeild);

         JTextField agefeild=new JTextField();
         agefeild.setBounds(300,230,250,30);
         frame.add(agefeild);

         //Password
         JTextField Passwordfeild=new JTextField();
         Passwordfeild.setBounds(300,280,250,30);
         frame.add(Passwordfeild);

         //Password
         JTextField ConfPassfeild=new JTextField();
         ConfPassfeild.setBounds(300,330,250,30);
         frame.add(ConfPassfeild);

         //buttons
         JButton Clear=new JButton("Clear");
         Clear.setBounds(150,400,130,30);
        //  Clear.setBackground(Color.blue);
        //  Clear.setForeground(Color.white);
         frame.add(Clear);

         JButton Submit=new JButton("Submit");
         Submit.setBounds(380,400,130,30);
        //  Submit.setBackground(Color.blue);
        //  Submit.setForeground(Color.white);
         frame.add(Submit); 

       // back button
         JButton back=new JButton("<--");
        back.setBounds(10,10,50,20);
        frame.add(back);

         back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                new UserLog();
            }
        });
 
         frame.setVisible(true);
    }
     public static void main(String[] args) throws Exception {
         
        new UserReg();
     }
    
}
