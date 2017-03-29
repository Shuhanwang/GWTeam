package com.example.gwteam.hichat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    Button sendbtn;
    Button selector;
    Button sendFile;
    LinearLayout usrlist;
    TextView chatBoard;
    TextView fileBoard;
    TextView filePath;
    EditText msgIn;
    Socket fromServer;

    public Socket toClient;
    private String myName;
    private int myPort = 0;
    private String tempName;
    private String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myQQ/";
    private String fileSelected = null;
    final static int REMOVE = 0;
    final static int ACCOUNT_EXSIST = 1;
    final static int NO_SELECT_USER = 2;
    final static int COMMUNICATW_WITH = 3;
    final static int CLEAR_INPUT = 4;
    final static int RECEIVE_MSG = 5;
    final static int SEND_FILE_FINISH = 6;
    final static int FILE_RECEIVE_DATE = 7;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case REMOVE: {usrlist.removeAllViews();};break;
                case ACCOUNT_EXSIST:
                    { Toast.makeText(ChatActivity.this, "USER EXISTS", Toast.LENGTH_SHORT).show();};break;
                case NO_SELECT_USER:
                {Toast.makeText(ChatActivity.this, "PLEASE CHOOSE A USER", Toast.LENGTH_SHORT).show();};break;
                case COMMUNICATW_WITH:
                    { chatBoard.setText("");
                        chatBoard.append("START COMMUNICATION WITH " + tempName + "\n");};break;
                case CLEAR_INPUT:{ msgIn.setText("");};break;
                case RECEIVE_MSG:
                    {
                    Bundle bundle = msg.getData();
                    String str = bundle.getString("msg");
                    String[] getMes = str.split("#%");
                    chatBoard.append("RECEIVED MESSAGE FROM " + getMes[1] + "\n");
                    chatBoard.append(getMes[0] + "\n");};break;
                case SEND_FILE_FINISH:
                {
                    Toast.makeText(ChatActivity.this, "FILE HAS BEEN SENT", Toast.LENGTH_SHORT).show();
                }break;
                case FILE_RECEIVE_DATE:
                {
                    Bundle bundle = msg.getData();
                    String date = bundle.getString("date");
                    String[] strs = date.split("\\$");
                    fileBoard.append( "RECEIVED FILE FROM" + strs[1]
                            + "at" + strs[0] + "\n");
                }
            }
        }
    };
    private void sendMessageToHandler(int MESSAGE)
    {
        Message msg = Message.obtain();
        msg.what = MESSAGE;
        mHandler.sendMessage(msg);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myName = SocketSingleInstance.mName;
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_chat);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText("CURRENT USER：" + myName);
        usrlist = (LinearLayout) findViewById(R.id.usrlist);
        sendbtn = (Button)findViewById(R.id.sendbtn);
        selector = (Button)findViewById(R.id.selectfile);
        sendFile = (Button)findViewById(R.id.sendfile);
        chatBoard = (TextView) findViewById(R.id.chatBoard);
        filePath = (TextView)findViewById(R.id.filepath);
        fileBoard = (TextView)findViewById(R.id.fileBoard);
        selector.setOnClickListener(new SelectFile());
        sendFile.setOnClickListener(new SendFile());
        msgIn = (EditText)findViewById(R.id.msg);
        fromServer = SocketSingleInstance.getInstance();
        ReadUsrList usrListListener = new ReadUsrList();


        usrListListener.start();
        sendbtn.setOnClickListener(new SendListener());
        createDir();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            new ObjectOutputStream(fromServer.getOutputStream())
                    .writeObject("DISCONNECTED");
            SocketSingleInstance.setNull();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void createDir()
    {
        File file = new File(root);
        if(!file.exists())
            file.mkdir();
    }
    private class SendFile implements  View.OnClickListener {
        public void onClick(View view) {
            SendFileThread thread = new SendFileThread();
            thread.start();
        }
    }
    private class SendFileThread extends Thread
    {

        @Override
        public void run() {
            super.run();
            File file = new File(fileSelected);
            if(desIp == null)
            {
                Message msg = Message.obtain();
                msg.what = NO_SELECT_USER;
                mHandler.sendMessage(msg);
                return;
            }
            if(file != null)
            {
                try {
                    Socket tempS = null ;
                    tempS = new Socket(desIp, desPort);

                    FileInputStream fis = new FileInputStream(file);
                    DataOutputStream dos = new DataOutputStream(tempS
                            .getOutputStream());
                    System.out.println();
                    // 传输文件名称和长度以及发送端用户名
                    new ObjectOutputStream(tempS.getOutputStream()).writeObject(file.length());
                    dos.writeUTF(file.getName().toString());
                    dos.writeUTF(myName);
                    int fileLength = (int)file.length();
                    int fileSent = 0;
                    // 传输文件
                    byte[] sendBytes = new byte[1024];
                    while (fileSent < fileLength)
                    {
                        if(fileLength - fileSent < 1024)
                        {
                            byte[] sendBytes2 = new byte[fileLength - fileSent];
                            fis.read(sendBytes2);
                            dos.write(sendBytes2);
                            dos.flush();
                            break;
                        }
                        fileSent +=  fis.read(sendBytes);
                        dos.write(sendBytes);
                        dos.flush();
                    }

                    fis.close();
                    dos.close();
                    tempS.close();
                }catch (IOException e)
                {
                    e.printStackTrace();

                }
                sendMessageToHandler(SEND_FILE_FINISH);
            }
        }
    }
    private class SelectFile implements  View.OnClickListener
    {
        public void onClick(View view)
        {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent,1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            fileSelected = UriResolutoin.getPath(ChatActivity.this, data.getData());
            filePath.setText("CURRENT FILE：" + fileSelected);
         }
    }

    private class SendListener implements View.OnClickListener
    {
        public void onClick(View view)
        {
            /* 发送 */
            // 判断消息发送之前是否选择了通信对象
            if (desIp == null) {
                Message msg = Message.obtain();
                msg.what = NO_SELECT_USER;
                mHandler.sendMessage(msg);
                return;
            }
            String mes = msgIn.getText().toString();
            mes = mes + "#%" + myName;
            Message msg = Message.obtain();
            msg.what = CLEAR_INPUT;
            mHandler.sendMessage(msg);

            try {
                    new ObjectOutputStream(toClient.getOutputStream())
                        .writeObject(mes);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    ArrayList<message> loginList;
    private String desIp;
    private int desPort;
    private class ReadUsrList extends Thread{
        @Override
        public void run()
        {
            super.run();
            while (true) {
                try {
                    Object obj = new ObjectInputStream(
                            fromServer.getInputStream()).readObject();
                    // 当服务器端发送的是ArrayList类的实例化时，说明接收到的消息是用户列表
                    if (obj instanceof ArrayList) {
                        loginList = (ArrayList<message>) obj;
                        Message msg = Message.obtain();
                        msg.what = REMOVE;
                        mHandler.sendMessage(msg);
                        for (message mes : loginList) {
                            if (mes.getName().equals(myName)) {
                                if (myPort == 0) {
                                    myPort = mes.getPort();
                                    new Thread(new readInfo()).start();
                                }
                            }
                            final Button btnTemp = new Button(ChatActivity.this);
                            btnTemp.setWidth(100);
                            btnTemp.setWidth(50);
                            btnTemp.setText(mes.getName());

                            btnTemp.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View e) {
                                    tempName = ((Button)e).getText().toString();
                                    Message msg = Message.obtain();
                                    msg.what = COMMUNICATW_WITH;
                                    Bundle bundle = new Bundle();
                                    bundle.putString("name", tempName);
                                    msg.setData(bundle);
                                    mHandler.sendMessage(msg);

                                    for (message m : loginList) {
                                        if (m.getName().equals(tempName)) {
                                            desIp = m.getIp();
                                            desPort = m.getPort();

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try{
                                                            toClient = new Socket(desIp, desPort);
                                                    }catch (IOException e)
                                                    {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }).start();
                                            break;
                                        }
                                    }
                                }

                            });

                           mHandler.post(new Runnable() {
                               @Override
                               public void run() {
                                   usrlist.addView(btnTemp);
                               }
                           });

                        }

                    }
                    // 当接收到的消息是String类的实例化时，说明该用户注册的名称已被占用
                    if (obj instanceof String) {
                        Message msg = Message.obtain();
                        msg.what = ACCOUNT_EXSIST;
                        mHandler.sendMessage(msg);
                        System.exit(1);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    System.out.println("aaoq");
                    break;
                }
            }
        }
    }
    // 读取客户端发送过来的消息
    class readInfo implements Runnable {
        ServerSocket serverSocket1;

        public readInfo() {
        }

        @Override
        public void run() {
            try {
                serverSocket1 = new ServerSocket(myPort);
                while (true) {
                    Socket socket1 = serverSocket1.accept();
                    getInfo gti = new getInfo(socket1);
                    new Thread(gti).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class getInfo implements Runnable {
        private Socket s;

        public getInfo(Socket s) {
            this.s = s;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Object obj = null;
                    try {
                        obj = new ObjectInputStream(s.getInputStream())
                                .readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    // 如果接收到的消息是String类的实例化，则说明接收到的消息是普通文字消息
                    if (obj instanceof String) {
                        String str = (String) obj;
                        Message msg = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putString("msg", str);
                        msg.setData(bundle);
                        msg.what = RECEIVE_MSG;
                        mHandler.sendMessage(msg);

                    }
                    // 如果不是，则说明接收到的是文件
                    else {
                            Long len = (Long) obj;

                            String name = new DataInputStream(s.getInputStream())
                                    .readUTF();
                            String userName = new DataInputStream(
                                    s.getInputStream()).readUTF();
                            FileOutputStream fos = new FileOutputStream(root
                                    + name);
                            byte[] readBytes = new byte[1024];
                            long lenReceived = 0;
                            DataInputStream read = new DataInputStream(
                                    s.getInputStream());
                            while ((lenReceived < len) ) {
                                int rcvn = read.read(readBytes);
                                lenReceived +=rcvn;
                                fos.write(readBytes, 0, rcvn);
                                fos.flush();
                            }
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date d = new Date();
                            String date = df.format(d);

                            String sbundle = date + "$" + userName;
                            Message msg = Message.obtain();
                            Bundle bundle = new Bundle();
                            bundle.putString("date", sbundle);
                            msg.setData(bundle);
                            msg.what = FILE_RECEIVE_DATE;
                            mHandler.sendMessage(msg);

                            read.close();
                            fos.close();
                           /* IOException e = new IOException("SSS");
                            throw e;*/
                    }
                } catch (Exception e) {
                    if (e.getMessage().equals("SSS")) {
                        break;
                    }
                }
            }
        }
    }
}
