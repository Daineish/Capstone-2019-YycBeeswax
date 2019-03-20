//package com.example.dainemcniven.yycbeeswaxcapstone;
//
//import android.os.AsyncTask;
//import android.util.Log;
//
//public class ConnectTask extends AsyncTask<String, String, TcpClient>
//{
//    TcpClient m_TcpClient;
//    @Override
//    protected TcpClient doInBackground(String... message)
//    {
//
//        //we create a TCPClient object
//        m_TcpClient = new TcpClient(new TcpClient.OnMessageReceived()
//        {
//            @Override
//            //here the messageReceived method is implemented
//            public void messageReceived(String message)
//            {
//                //this method calls the onProgressUpdate
//                publishProgress(message);
//            }
//        });
//        m_TcpClient.run();
//
//        return null;
//    }
//
//    @Override
//    protected void onProgressUpdate(String... values)
//    {
//        super.onProgressUpdate(values);
//        //response received from server
//        Log.d("test", "response " + values[0]);
//        //process server response here....
//
//    }
//
//    public boolean sendMsg(String str)
//    {
//        if (m_TcpClient != null)
//        {
//            m_TcpClient.sendMessage("testing");
//            return true;
//        }
//        else
//            return false;
//    }
//
//}