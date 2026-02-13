import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
public class AdminLog {
    public static Connection con;

    AdminLog(){
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
         JButton signUp=new JButton("SignUp");
         signUp.setBounds(180,330,130,30);
         frame.add(signUp);

         JButton login=new JButton("LogIn");
         login.setBounds(380,330,130,30);
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

         new AdminLog();

    }
}
