package cn.wzh.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Properties;

//有界面所以需要继承窗体类JFrame
public class ClientChatMain extends JFrame implements ActionListener, KeyListener, MouseListener {
    public static void main(String[] args) {
        new ClientChatMain();
    }

    //    属性
//    文本域
    private JTextArea jta;
    //    滚动条
    private JScrollPane jsp;
    //    面板
    private JPanel jp;
    //    文本框
    private JTextField jtf;
    //    按钮jb 用来发送消息，jb2用来传输文件
    private JButton jb;
    private JButton jb2;
    //    输出流
    private BufferedWriter bw;
    //    读取文件流
    private FileInputStream fis;
    //    发送数据流

    //    客户端IP地址
    private static String clientIp;

    private static int clientPort;

    private Socket socket;

    static {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("chat.properties"));
            clientIp = properties.getProperty("clientIp");
            clientPort = Integer.parseInt(properties.getProperty("clientPort"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    构造方法

    public ClientChatMain() {
        Font font = new Font("Serif", 0, 20);
        jta = new JTextArea();
//        设置文本域不可编辑,调整字体大小
        jta.setEditable(false);
        jta.setFont(font);
//        将文本域添加在滚动条中
        jsp = new JScrollPane(jta);

        jp = new JPanel();
//        设置文本框的高度，字体大小
        jtf = new JTextField();
        jtf.setPreferredSize(new Dimension(460, 40));
        jtf.setFont(font);
        jb = new JButton("发送");
        jb2 = new JButton("传文件");
        jb.setFont(font);
        jb2.setFont(font);
//        将文本框和按钮添加到面板中
        jp.add(jtf);
        jp.add(jb);
        jp.add(jb2);

//        将滚动条和面板添加到窗体
        this.add(jsp, BorderLayout.CENTER);
        this.add(jp, BorderLayout.SOUTH);
//        设置标题,大小，位置，关闭，是否可见

        this.setTitle("聊天客户端");
        this.setSize(660, 500);
        this.setLocation(900, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
//        ************TCP客户端Start**************
//         给发送按钮绑定监听事件
        jb.addActionListener(this);
        jtf.addKeyListener(this);
        jb2.addMouseListener(this);
        try {
//        创建客户端套接字(尝试链接)
            socket = new Socket(clientIp, clientPort);

//        获取socket通道输入流
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        获取socket通道输出流
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

//        循环读取数据，拼接到文本中
            String line = null;
            while ((line = br.readLine()) != null) {
                jta.append(line + System.lineSeparator());
            }
//        关闭socket通道
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


//        ************TCP客户端结束****************

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        sendDataToSocket();
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
            sendDataToSocket();
        }
    }

    private void sendDataToSocket() {
        //        获取文本框发送的内容
        String text = jtf.getText();
//        拼接内容
        text = "客户端：" + text;
//        自己显示
        jta.append(text + System.lineSeparator());
        try {
//        发送
            bw.write(text);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        清空
        jtf.setText("");

    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }

    //      鼠标点击选择并发送文件
    @Override
    public void mouseClicked(MouseEvent e) {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jFileChooser.showDialog(new Label(), "选择文件");
        File file = jFileChooser.getSelectedFile();
        if (file.isDirectory()) {
            JOptionPane.showMessageDialog(null, "不可以传输文件夹哦");
        } else if (file.isFile()) {
//            System.out.println("文件：" + file.getAbsolutePath());
            try {
                fis = new FileInputStream(file);
                PrintStream ps=new PrintStream(socket.getOutputStream());
//                获取文件名和文件长度
                ps.println("客户端：我向你发送了文件，请接收");
                ps.println(file.getName());
                System.out.println(file.getName());

//                开始传输文件
                byte[] bytes = new byte[1024];
                int len = 0;
//                fis.read(b[])方法是把信息读入到这个数组中，单纯的read()方法就是读文件
                while ((len=fis.read(bytes,0,bytes.length))!=-1){
                    ps.write(bytes,0,len);
                    ps.flush();
                }
                String flag=new String("It is end of transform the file".getBytes(),"UTF-8");
                ps.println(flag);
                ps.flush();
                fis.close();
                JOptionPane.showMessageDialog(null,"文件传输成功");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
