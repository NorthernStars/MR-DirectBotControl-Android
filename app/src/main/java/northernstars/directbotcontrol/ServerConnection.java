package northernstars.directbotcontrol;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class ServerConnection{
    private boolean mSocketInitialized = false;

    private static int MAX_DATAGRAM_LENGTH = 16384;
    private DatagramPacket mDataPaket;
    private DatagramSocket mToServerSocket = null;

    private boolean mEstablishedServerConnection = false;

    ServerConnection( InetAddress aServerAddress, int aServerPort ) throws IOException
    {

        initialiseDatagram( aServerAddress, aServerPort );

        mToServerSocket = new DatagramSocket();
        mToServerSocket.connect(aServerAddress, aServerPort);

        mSocketInitialized = true;

    }

    private void initialiseDatagram( InetAddress aServerAddress, int aServerPort ) {

        mDataPaket = new DatagramPacket( new byte[ServerConnection.MAX_DATAGRAM_LENGTH], ServerConnection.MAX_DATAGRAM_LENGTH );
        mDataPaket.setAddress(aServerAddress);
        mDataPaket.setPort( aServerPort );

    }

    void connectToServer(  String aName, int aRcId, int aVtId ) throws IOException, XmlPullParserException
    {

        if( mToServerSocket != null && mDataPaket != null){

            sendDatagramm(createConnectionRequest( aName,  aRcId,  aVtId ) );

            String vReply = getDatagramm( 1000 );

            if( vReply.equals("<connect>true</connect>") ){

                sendDatagramm( vReply );

                mEstablishedServerConnection = true;

                getDatagramm( 1000 );

                return;

            }
        }

        mEstablishedServerConnection = false;

    }

    private String createConnectionRequest( String aName, int aRcId, int aVtId ){
        XmlSerializer vSerializer = Xml.newSerializer();
        StringWriter vWriter = new StringWriter();
        try {
            vSerializer.setOutput(vWriter);
            vSerializer.startDocument("UTF-8", true);
            vSerializer.startTag("", "connect");

                vSerializer.startTag("", "type");
                vSerializer.text("Client");
                vSerializer.endTag("", "type");

                vSerializer.startTag("", "protocol_version");
                vSerializer.text("1.0");
                vSerializer.endTag("", "protocol_version");

                vSerializer.startTag("", "nickname");
                vSerializer.text(aName);
                vSerializer.endTag("", "nickname");

                vSerializer.startTag("", "rc_id");
                vSerializer.text(Integer.toString(aRcId));
                vSerializer.endTag("", "rc_id");

                vSerializer.startTag("", "vt_id");
                vSerializer.text(Integer.toString(aVtId));
                vSerializer.endTag("", "vt_id");

            vSerializer.endTag("", "connect");
            vSerializer.endDocument();
            return vWriter.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void sendDatagramm( String aData ) throws IOException
    {

        mDataPaket.setData( aData.getBytes() );
        mToServerSocket.send( mDataPaket );

    }

    private String getDatagramm( int aWaitTime ) throws IOException
    {
        String vData = null;
        DatagramPacket vDatagrammPacketFromServer = new DatagramPacket( new byte[ServerConnection.MAX_DATAGRAM_LENGTH], ServerConnection.MAX_DATAGRAM_LENGTH );

        mToServerSocket.setSoTimeout( aWaitTime );

        mToServerSocket.receive( vDatagrammPacketFromServer );
        if ( vDatagrammPacketFromServer.getAddress().equals( mDataPaket.getAddress() ) ) {
            vData = new String( vDatagrammPacketFromServer.getData(), 0, vDatagrammPacketFromServer.getLength() );
            // Alter Footballserver sendet ein "|" am Ende des XMLStrings und das hier ist der Fix :(
            if( !vData.endsWith( ">" ) ){

                vData = new String( vData.getBytes(), 0, vData.length() - 1 );

            }
        }

        return vData;
    }

    void closeConnection()
    {
        if ( mToServerSocket != null ) {
            mToServerSocket.disconnect();
            mToServerSocket.close();
            mToServerSocket = null;
        }

        mSocketInitialized = false;

    }

    boolean isConnected() {

        return mSocketInitialized && mEstablishedServerConnection;

    }

    public String toString(){
        if ( mToServerSocket != null ) {

            return mToServerSocket.getLocalAddress().toString() + ":" + mToServerSocket.getLocalPort() + " zu " + mToServerSocket.getInetAddress().toString() + ":" + mToServerSocket.getPort();

        } else {

            return "Es besteht keine Serververbindung";

        }

    }
}
