/******************************************************************
 *
 * MediaServer for CyberLink
 *
 * Copyright (C) Satoshi Konno 2003
 *
 * File : ConnectionManager
 *
 * Revision:
 *
 * 10/22/03 - first revision. 06/19/04 - Added getCurrentConnectionIDs() and
 * getCurrentConnectionInfo(); 12/02/04 - Brian Owens <brian@b-owens.com> -
 * Fixed to initialize conInfoList.
 *
 ******************************************************************/

package org.cybergarage.upnp.std.av.server;


import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.StateVariable;
import org.cybergarage.upnp.control.ActionListener;
import org.cybergarage.upnp.control.QueryListener;
import org.cybergarage.upnp.std.av.server.object.Format;
import org.cybergarage.util.Mutex;


public class ConnectionManager implements ActionListener, QueryListener
{
    ////////////////////////////////////////////////
    // Constants
    ////////////////////////////////////////////////

    public final static String SERVICE_TYPE = "urn:schemas-upnp-org:service:ConnectionManager:1";

    // Browse Action	

    public final static String HTTP_GET = "http-get";

    public final static String SOURCEPROTOCOLINFO = "SourceProtocolInfo";
    public final static String SINKPROTOCOLINFO = "SinkProtocolInfo";
    public final static String CURRENTCONNECTIONIDS = "CurrentConnectionIDs";
    public final static String GETPROTOCOLINFO = "GetProtocolInfo";
    public final static String SOURCE = "Source";
    public final static String SINK = "Sink";
    public final static String PREPAREFORCONNECTION = "PrepareForConnection";
    public final static String REMOTEPROTOCOLINFO = "RemoteProtocolInfo";
    public final static String PEERCONNECTIONMANAGER = "PeerConnectionManager";
    public final static String PEERCONNECTIONID = "PeerConnectionID";
    public final static String DIRECTION = "Direction";
    public final static String CONNECTIONID = "ConnectionID";
    public final static String AVTRANSPORTID = "AVTransportID";
    public final static String RCSID = "RcsID";
    public final static String CONNECTIONCOMPLETE = "ConnectionComplete";
    public final static String GETCURRENTCONNECTIONIDS = "GetCurrentConnectionIDs";
    public final static String CONNECTIONIDS = "ConnectionIDs";
    public final static String GETCURRENTCONNECTIONINFO = "GetCurrentConnectionInfo";
    public final static String PROTOCOLINFO = "ProtocolInfo";
    public final static String STATUS = "Status";

    public final static String OK = "OK";
    public final static String CONTENTFORMATMISMATCH = "ContentFormatMismatch";
    public final static String INSUFFICIENTBANDWIDTH = "InsufficientBandwidth";
    public final static String UNRELIABLECHANNEL = "UnreliableChannel";
    public final static String UNKNOWN = "Unknown";
    public final static String INPUT = "Input";
    public final static String OUTPUT = "Output";

    public final static String SCPD = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
        + "<scpd xmlns=\"urn:schemas-upnp-org:service-1-0\">\n"
        + "   <specVersion>\n"
        + "      <major>1</major>\n"
        + "      <minor>0</minor>\n"
        + "	</specVersion>\n"
        + "	<actionList>\n"
        + "		<action>\n"
        + "         <name>GetCurrentConnectionInfo</name>\n"
        + "         <argumentList>\n"
        + "            <argument>\n"
        + "               <name>ConnectionID</name>\n"
        + "               <direction>in</direction>\n"
        + "               <relatedStateVariable>A_ARG_TYPE_ConnectionID</relatedStateVariable>\n"
        + "            </argument>\n"
        + "            <argument>\n"
        + "               <name>RcsID</name>\n"
        + "               <direction>out</direction>\n"
        + "               <relatedStateVariable>A_ARG_TYPE_RcsID</relatedStateVariable>\n"
        + "            </argument>\n"
        + "            <argument>\n"
        + "               <name>AVTransportID</name>\n"
        + "               <direction>out</direction>\n"
        + "               <relatedStateVariable>A_ARG_TYPE_AVTransportID</relatedStateVariable>\n"
        + "            </argument>\n"
        + "            <argument>\n"
        + "               <name>ProtocolInfo</name>\n"
        + "               <direction>out</direction>\n"
        + "               <relatedStateVariable>A_ARG_TYPE_ProtocolInfo</relatedStateVariable>\n"
        + "            </argument>\n"
        + "            <argument>\n"
        + "               <name>PeerConnectionManager</name>\n"
        + "               <direction>out</direction>\n"
        + "               <relatedStateVariable>A_ARG_TYPE_ConnectionManager</relatedStateVariable>\n"
        + "            </argument>\n"
        + "            <argument>\n"
        + "               <name>PeerConnectionID</name>\n"
        + "               <direction>out</direction>\n"
        + "               <relatedStateVariable>A_ARG_TYPE_ConnectionID</relatedStateVariable>\n"
        + "            </argument>\n"
        + "            <argument>\n"
        + "               <name>Direction</name>\n"
        + "               <direction>out</direction>\n"
        + "               <relatedStateVariable>A_ARG_TYPE_Direction</relatedStateVariable>\n"
        + "            </argument>\n"
        + "            <argument>\n"
        + "               <name>Status</name>\n"
        + "               <direction>out</direction>\n"
        + "               <relatedStateVariable>A_ARG_TYPE_ConnectionStatus</relatedStateVariable>\n"
        + "            </argument>\n"
        + "         </argumentList>\n"
        + "      </action>\n"
        + "      <action>\n"
        + "         <name>GetProtocolInfo</name>\n"
        + "         <argumentList>\n"
        + "            <argument>\n"
        + "               <name>Source</name>\n"
        + "               <direction>out</direction>\n"
        + "               <relatedStateVariable>SourceProtocolInfo</relatedStateVariable>\n"
        + "            </argument>\n"
        + "            <argument>\n"
        + "               <name>Sink</name>\n"
        + "               <direction>out</direction>\n"
        + "               <relatedStateVariable>SinkProtocolInfo</relatedStateVariable>\n"
        + "            </argument>\n"
        + "         </argumentList>\n"
        + "      </action>\n"
        + "      <action>\n"
        + "         <name>GetCurrentConnectionIDs</name>\n"
        + "         <argumentList>\n"
        + "            <argument>\n"
        + "               <name>ConnectionIDs</name>\n"
        + "               <direction>out</direction>\n"
        + "               <relatedStateVariable>CurrentConnectionIDs</relatedStateVariable>\n"
        + "            </argument>\n" + "         </argumentList>\n"
        + "      </action>\n" + "   </actionList>\n" + "   <serviceStateTable>\n"
        + "      <stateVariable sendEvents=\"no\">\n"
        + "         <name>A_ARG_TYPE_ProtocolInfo</name>\n"
        + "         <dataType>string</dataType>\n" + "      </stateVariable>\n"
        + "      <stateVariable sendEvents=\"no\">\n"
        + "         <name>A_ARG_TYPE_ConnectionStatus</name>\n"
        + "         <dataType>string</dataType>\n" + "         <allowedValueList>\n"
        + "            <allowedValue>OK</allowedValue>\n"
        + "            <allowedValue>ContentFormatMismatch</allowedValue>\n"
        + "            <allowedValue>InsufficientBandwidth</allowedValue>\n"
        + "            <allowedValue>UnreliableChannel</allowedValue>\n"
        + "            <allowedValue>Unknown</allowedValue>\n"
        + "         </allowedValueList>\n" + "      </stateVariable>\n"
        + "      <stateVariable sendEvents=\"no\">\n"
        + "         <name>A_ARG_TYPE_AVTransportID</name>\n"
        + "         <dataType>i4</dataType>\n" + "      </stateVariable>\n"
        + "      <stateVariable sendEvents=\"no\">\n"
        + "         <name>A_ARG_TYPE_RcsID</name>\n"
        + "         <dataType>i4</dataType>\n" + "      </stateVariable>\n"
        + "      <stateVariable sendEvents=\"no\">\n"
        + "         <name>A_ARG_TYPE_ConnectionID</name>\n"
        + "         <dataType>i4</dataType>\n" + "      </stateVariable>\n"
        + "      <stateVariable sendEvents=\"no\">\n"
        + "         <name>A_ARG_TYPE_ConnectionManager</name>\n"
        + "         <dataType>string</dataType>\n" + "      </stateVariable>\n"
        + "      <stateVariable sendEvents=\"yes\">\n"
        + "         <name>SourceProtocolInfo</name>\n"
        + "         <dataType>string</dataType>\n" + "      </stateVariable>\n"
        + "      <stateVariable sendEvents=\"yes\">\n"
        + "         <name>SinkProtocolInfo</name>\n"
        + "         <dataType>string</dataType>\n" + "      </stateVariable>\n"
        + "      <stateVariable sendEvents=\"no\">\n"
        + "         <name>A_ARG_TYPE_Direction</name>\n"
        + "         <dataType>string</dataType>\n" + "         <allowedValueList>\n"
        + "            <allowedValue>Input</allowedValue>\n"
        + "            <allowedValue>Output</allowedValue>\n"
        + "         </allowedValueList>\n" + "      </stateVariable>\n"
        + "      <stateVariable sendEvents=\"yes\">\n"
        + "         <name>CurrentConnectionIDs</name>\n"
        + "         <dataType>string</dataType>\n" + "      </stateVariable>\n"
        + "   </serviceStateTable>\n" + "</scpd>";

    ////////////////////////////////////////////////
    // Constructor 
    ////////////////////////////////////////////////
    private MediaServer mediaServer;

    ////////////////////////////////////////////////
    // Media Server
    ////////////////////////////////////////////////
    private Mutex mutex = new Mutex();
    private int maxConnectionID;
// Thanks for Brian Owens (12/02/04)
    private ConnectionInfoList conInfoList = new ConnectionInfoList();

    public ConnectionManager(MediaServer mserver)
    {
        setMediaServer(mserver);
        maxConnectionID = 0;
    }

    ////////////////////////////////////////////////
    // Mutex
    ////////////////////////////////////////////////

    public MediaServer getMediaServer()
    {
        return mediaServer;
    }

    private void setMediaServer(MediaServer mserver)
    {
        mediaServer = mserver;
    }

    public ContentDirectory getContentDirectory()
    {
        return getMediaServer().getContentDirectory();
    }

    ////////////////////////////////////////////////
    // ConnectionID
    ////////////////////////////////////////////////

    public void lock()
    {
        mutex.lock();
    }

    public void unlock()
    {
        mutex.unlock();
    }

    ////////////////////////////////////////////////
    // ConnectionInfoList
    ////////////////////////////////////////////////

        public int getNextConnectionID()
    {
        lock();
        maxConnectionID++;
        unlock();
        return maxConnectionID;
    };

    public ConnectionInfoList getConnectionInfoList()
    {
        return conInfoList;
    }

    public ConnectionInfo getConnectionInfo(int id)
    {
        int size = conInfoList.size();
        for (int n = 0; n < size; n++)
        {
            ConnectionInfo info = conInfoList.getConnectionInfo(n);
            if (info.getID() == id)
                return info;
        }
        return null;
    }

    public void addConnectionInfo(ConnectionInfo info)
    {
        lock();
        conInfoList.add(info);
        unlock();
    }

    public void removeConnectionInfo(int id)
    {
        lock();
        int size = conInfoList.size();
        for (int n = 0; n < size; n++)
        {
            ConnectionInfo info = conInfoList.getConnectionInfo(n);
            if (info.getID() == id)
            {
                conInfoList.remove(info);
                break;
            }
        }
        unlock();
    }

    public void removeConnectionInfo(ConnectionInfo info)
    {
        lock();
        conInfoList.remove(info);
        unlock();
    }

    ////////////////////////////////////////////////
    // ActionListener
    ////////////////////////////////////////////////

    public boolean actionControlReceived(Action action)
    {
        //action.print();

        String actionName = action.getName();

        if (actionName.equals(GETPROTOCOLINFO) == true)
        {
            // Source
            String sourceValue = "";
            int mimeTypeCnt = getContentDirectory().getNFormats();
            for (int n = 0; n < mimeTypeCnt; n++)
            {
                if (0 < n)
                    sourceValue += ",";
                Format format = getContentDirectory().getFormat(n);
                String mimeType = format.getMimeType();
                sourceValue += HTTP_GET + ":*:" + mimeType + ":*";
            }
            action.getArgument(SOURCE).setValue(sourceValue);
            // Sink
            action.getArgument(SINK).setValue("");
            return true;
        }

        if (actionName.equals(PREPAREFORCONNECTION) == true)
        {
            action.getArgument(CONNECTIONID).setValue(-1);
            action.getArgument(AVTRANSPORTID).setValue(-1);
            action.getArgument(RCSID).setValue(-1);
            return true;
        }

        if (actionName.equals(CONNECTIONCOMPLETE) == true)
        {
            return true;
        }

        if (actionName.equals(GETCURRENTCONNECTIONINFO) == true)
            return getCurrentConnectionInfo(action);

        if (actionName.equals(GETCURRENTCONNECTIONIDS) == true)
            return getCurrentConnectionIDs(action);

        return false;
    }

    ////////////////////////////////////////////////
    // GetCurrentConnectionIDs
    ////////////////////////////////////////////////

    private boolean getCurrentConnectionIDs(Action action)
    {
        String conIDs = "";
        lock();
        int size = conInfoList.size();
        for (int n = 0; n < size; n++)
        {
            ConnectionInfo info = conInfoList.getConnectionInfo(n);
            if (0 < n)
                conIDs += ",";
            conIDs += Integer.toString(info.getID());
        }
        action.getArgument(CONNECTIONIDS).setValue(conIDs);
        unlock();
        return true;
    }

    ////////////////////////////////////////////////
    // GetCurrentConnectionInfo
    ////////////////////////////////////////////////

    private boolean getCurrentConnectionInfo(Action action)
    {
        int id = action.getArgument(RCSID).getIntegerValue();
        lock();
        ConnectionInfo info = getConnectionInfo(id);
        if (info != null)
        {
            action.getArgument(RCSID).setValue(info.getRcsID());
            action.getArgument(AVTRANSPORTID).setValue(info.getAVTransportID());
            action.getArgument(PEERCONNECTIONMANAGER).setValue(
                info.getPeerConnectionManager());
            action.getArgument(PEERCONNECTIONID).setValue(info.getPeerConnectionID());
            action.getArgument(DIRECTION).setValue(info.getDirection());
            action.getArgument(STATUS).setValue(info.getStatus());
        }
        else
        {
            action.getArgument(RCSID).setValue(-1);
            action.getArgument(AVTRANSPORTID).setValue(-1);
            action.getArgument(PEERCONNECTIONMANAGER).setValue("");
            action.getArgument(PEERCONNECTIONID).setValue(-1);
            action.getArgument(DIRECTION).setValue(ConnectionInfo.OUTPUT);
            action.getArgument(STATUS).setValue(ConnectionInfo.UNKNOWN);
        }
        unlock();
        return true;
    }

    ////////////////////////////////////////////////
    // QueryListener
    ////////////////////////////////////////////////

    public boolean queryControlReceived(StateVariable stateVar)
    {
        return false;
    }
}
