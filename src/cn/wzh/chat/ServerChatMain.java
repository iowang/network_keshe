package cn.wzh.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

//有界面所以需要继承窗体类JFrame
//构造方法中初始化窗体组件
//使用网络编程TCP协议实现通信
//实现发送按钮的监听点击事件
//回车发送数据
public class ServerChatMain extends JFrame implements ActionListener, KeyListener, MouseListener {
    public static void main(String[] args) {
        new ServerChatMain();
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
    private BufferedWriter bw = null;
    //    服务端端口号
    private static int serverPort;


    //    使用静态代码块读取外部配置文件
    static {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("chat.properties"));
            serverPort = Integer.parseInt(properties.getProperty("serverPort"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    构造方法

    public ServerChatMain() {
        Font font = new Font("Serif", 0, 20);
        jta = new JTextArea();
//        设置文本域不可编辑,调整字体大小
        jta.setEditable(false);
        jta.setFont(font);
//        将文本域添加在滚动条中
        jsp = new JScrollPane(jta);

        jp = new JPanel();
//        调整发送窗口的高度大小,字体大小
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

        this.setTitle("聊天服务端");
        this.setSize(660, 500);
        this.setLocation(300, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

//        ************TCP服务端Start**************
//        给发送按钮绑定监听点击事件
        jb.addActionListener(this);
//        给发送文件绑定监听事件
        jb2.addMouseListener(this);
//        给文本框绑定点击事件
        jtf.addKeyListener(this);
        try {
//        创建服务端套接字
            ServerSocket serverSocket = new ServerSocket(serverPort);

//        等待客户端链接
            Socket socket = serverSocket.accept();
//        获取socket通道输入流(输入流实现读取数据，一行一行读取，用BufferReader -->readline()
            InputStream in = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        获取socket通道输出流(实现写出数据，写一行换一行)
//        当用户点击发送时才写出数据
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//        循环读取数据
            String line = null;
            PrintStream ps = new PrintStream(socket.getOutputStream());
            while ((line = br.readLine()) != null) {
                if (line.equals("客户端：我向你发送了文件，请接收")) {
                    String fileName = br.readLine();
                    File dir = new File("Upload");
                    dir.mkdir();
                    File file = new File(dir, fileName);
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] bytes = new byte[1024];
                    int len = 0;
                    while ((len = in.read(bytes, 0, bytes.length)) != -1) {
//                        String str = new String(bytes,"UTF-8");
//                        System.out.println(str);
//                        System.out.println(len);
//                        if (str.equals("It is end of transform the file")) break;
                        fos.write(bytes, 0, len);
                        fos.flush();
                    }

                    fos.close();

                } else {
                    jta.append(line + System.lineSeparator());
                }
//                jta.append(line + System.lineSeparator());
            }
//        关闭socket通道
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
//        ************TCP服务端结束****************
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        sendDataToSocket();
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
//            发送数据到socket中
            sendDataToSocket();
        }
    }

    //    定义方法实现将数据发送到socket通道中
    private void sendDataToSocket() {
        //        获取文本框中发送的内容
        String text = jtf.getText();
//        拼接数据
        text = "服务端：" + text;
//        自己显示
        jta.append(text + System.lineSeparator());
        try {
//            发送
            bw.write(text);
            bw.newLine();
            bw.flush();
//          清空文本框内容
            jtf.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jFileChooser.showDialog(new Label(), "选择文件");
        File file = jFileChooser.getSelectedFile();
        if (file.isDirectory()) {
            System.out.println("文件夹：" + file.getAbsolutePath());
        } else if (file.isFile()) {
            System.out.println("文件：" + file.getAbsolutePath());
        }
        System.out.println(jFileChooser.getSelectedFile().getName());
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