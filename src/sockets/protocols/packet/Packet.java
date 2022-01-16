package sockets.protocols.packet;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * sockets.protocol
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 1:25 PM
 * Description: A packet for transferring information between client and server
 * @param sessionID the session id of the sender
 * @param sender the username of the sender
 * @param receiver the username of the receiver
 * @param cmd the command type
 * @param state the status of the packet (true: successful, false: failure)
 * @param payload the payload of the message in form of a byte array
 */
public record Packet(UUID sessionID, String sender, String receiver, CommandType cmd, boolean state, byte[] payload) implements Serializable
{
    /**
     * Create a new packet with command type
     * @param cmd the command type
     */
    public Packet(CommandType cmd)
    {
        this(null, null, null, cmd, true, null);
    }

    /**
     * Create a new packet with sender and command type
     * @param sender the sender username (of username)
     * @param cmd the command type
     */
    public Packet(String sender, CommandType cmd)
    {
        this(null, sender, null, cmd, true, null);
    }

    /**
     * Create a new packet with sender, command type and payload (string)
     * @param sender the sender username (of username)
     * @param cmd the command type
     * @param payload the string payload (will be converted to byte array)
     */
    public Packet(String sender, CommandType cmd, String payload)
    {
        this(null, sender, null, cmd, true, payload.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Create a new packet with session id, command type
     * @param sessionID the session id of the sender
     * @param cmd the command type
     */
    public Packet(UUID sessionID, CommandType cmd)
    {
        this(sessionID, null,null, cmd, true, null);
    }

    /**
     * Create a new packet with session id, command type and payload (object)
     * @param sessionID the session id of the sender
     * @param cmd the command type
     * @param obj the payload object implementing Serializable interface
     */
    public Packet(UUID sessionID, CommandType cmd, Serializable obj)
    {
        this(sessionID, null,null, cmd, true, toPayload(obj));
    }

    /**
     * Create a new packet with session id, command type and payload (object)
     * @param sessionID the session id of the sender
     * @param sender the sender of the message
     * @param cmd the command type
     * @param obj the payload object implementing Serializable interface
     */
    public Packet(UUID sessionID, String sender, CommandType cmd, Serializable obj)
    {
        this(sessionID, sender,null, cmd, true, toPayload(obj));
    }

    /**
     * Create a new packet with session id, sender and command type
     * @param sessionID the session id of the sender
     * @param cmd the command type
     * @param sender the username of the sender
     */
    public Packet(UUID sessionID, String sender, CommandType cmd)
    {
        this(sessionID, sender, null, cmd, true, null);
    }

    /**
     * Create a new packet with session id, command type, response state and payload
     * @param sessionID the session id of the sender
     * @param cmd the command type
     * @param state the status response of the packet (true: successful, false: failure)
     * @param obj the string payload (will be converted to byte array)
     */
    public Packet(UUID sessionID, CommandType cmd, boolean state, String obj)
    {
        this(sessionID, null, null, cmd, state, obj.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Create a new packet with session id, command type, response state
     * @param sessionID the session id of the sender
     * @param cmd the command type
     * @param state the status response of the packet (true: successful, false: failure)
     */
    public Packet(UUID sessionID, CommandType cmd, boolean state)
    {
        this(sessionID, null, null, cmd, state, null);
    }

    /**
     * Create a new packet with session id, sender, receiver, command type and payload (string)
     * @param sessionID the session id of the sender
     * @param sender the username of the sender
     * @param receiver the username of the receiver
     * @param cmd the command type
     * @param obj the string payload (will be converted to byte array)
     */
    public Packet(UUID sessionID, String sender, String receiver, CommandType cmd, String obj)
    {
        this(sessionID, sender, receiver, cmd, true, obj.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Create a new packet with session id, sender, receiver, command type and payload (object)
     * @param sessionID the session id of the sender
     * @param sender the username of the sender
     * @param receiver the username of the receiver
     * @param cmd the command type
     * @param obj the payload object implementing Serializable interface
     */
    public Packet(UUID sessionID, String sender, String receiver, CommandType cmd, Serializable obj)
    {

        this(sessionID, sender, receiver, cmd, true, toPayload(obj));
    }

    /**
     * Convert an object to a byte array
     * @param object the converted object
     * @return the byte array result (null if failed to cast)
     */
    public static byte[] toPayload(Serializable object)
    {
        try
        {
            //Use ObjectStream(ByteArrayStream) to convert object to a byte array
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            return arrayOutputStream.toByteArray();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Get the payload as a serializable object
     * @return the object result (null if failed to cast)
     */
    public Serializable getPayloadAsObject()
    {
        try
        {
            ByteArrayInputStream arrayStream = new ByteArrayInputStream(payload());
            ObjectInputStream objStream = new ObjectInputStream(arrayStream);
            Object result = objStream.readObject();
            objStream.close();
            return (Serializable)result;
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
