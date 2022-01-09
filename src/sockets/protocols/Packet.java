package sockets.protocols;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * sockets.protocol
 * Created by NhatLinh - 19127652
 * Date 1/8/2022 - 1:25 PM
 * Description: ...
 */
public record Packet(UUID sessionID, String sender, String receiver, CommandType cmd, boolean state, byte[] payload) implements Serializable {

    public Packet(CommandType cmd)
    {
        this(null, null, null, cmd, true, null);
    }

    public Packet(String sender, CommandType cmd)
    {
        this(null, sender, null, cmd, true, null);
    }

    public Packet(UUID sessionID, CommandType cmd)
    {
        this(sessionID, null,null, cmd, true, null);
    }

    public Packet(UUID sessionID, CommandType cmd, Serializable obj)
    {
        this(sessionID, null,null, cmd, true, toPayload(obj));
    }

    public Packet(UUID sessionID, String sender, CommandType cmd)
    {
        this(sessionID, sender, null, cmd, true, null);
    }

    public Packet(UUID sessionID, CommandType cmd, boolean state, String obj)
    {
        this(sessionID, null, null, cmd, state, obj.getBytes(StandardCharsets.UTF_8));
    }

    public Packet(UUID sessionID, CommandType cmd, boolean state)
    {
        this(sessionID, null, null, cmd, state, null);
    }

    public Packet(UUID sessionID, String sender, String receiver, CommandType cmd, String obj)
    {
        this(sessionID, sender, receiver, cmd, true, obj.getBytes(StandardCharsets.UTF_8));
    }

    public Packet(UUID sessionID, String sender, String receiver, CommandType cmd, Serializable obj)
    {
        this(sessionID, sender, receiver, cmd, true, toPayload(obj));
    }

    public static byte[] toPayload(Serializable object)
    {
        try
        {
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
