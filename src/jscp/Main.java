package jscp;

import com.jcraft.jsch.*;
import javafx.event.ActionEvent;

import static java.lang.System.exit;

public class Main
{
    static SftpProgressMonitor monitor;

    public static void main(String[] args) throws JSchException {
        if(args.length < 3)
        {
            usage();
            exit(-1);
        }else
        {
            //ChannelSftp currentChannel = createSession("USER", "HOST", "PASS");
            sendCmd(args[0], args[1], args[2], currentChannel);
        }
    }

    private static ChannelSftp createSession(String user, String host, String password) throws JSchException
    {

        JSch jsch  = new JSch();

        Session session;

        Channel channel;

        ChannelSftp secureChannel;

        String port = "22";
        session = jsch.getSession(user, host, Integer.valueOf(port));

        session.setConfig("StrictHostKeyChecking", "no");

        session.setPassword(password);

        session.connect();

        channel = session.openChannel("sftp");


        secureChannel = (ChannelSftp) channel;

        secureChannel.connect();
        System.out.println("==========================================================\n");
        System.out.println("Successfully Connected to " + user + "@" + host + ":" + port);
        System.out.println("\n==========================================================\n");
        return secureChannel;
    }

    private static void sendCmd(String cmd, String localPath, String remotePath, ChannelSftp secureChannel)
    {

        try
        {
            switch(cmd)
            {
                case "get": {
                    secureChannel.get(remotePath, localPath, monitor, ChannelSftp.OVERWRITE);
                    System.out.println("Getting " + remotePath + " to " + localPath);
                    break;
                }
                case "get-append":
                    {
                    secureChannel.get(remotePath, localPath, monitor, ChannelSftp.APPEND);
                    System.out.println("Get Appending " + remotePath + " to " + localPath);
                    break;
                }
                case "put": {
                    secureChannel.put(localPath, remotePath, monitor, ChannelSftp.OVERWRITE);
                    System.out.println("Putting " + localPath + " to " + remotePath);
                    break;
                }
                case "put-append": {
                    secureChannel.put(localPath, remotePath, monitor, ChannelSftp.APPEND);
                    System.out.println("Put Appending " + localPath + " to " + remotePath);
                    break;
                }
                default:
                    System.out.println("Incorrect Command Used");
                    usage();
                    break;
            }
        }catch (SftpException exception)
        {
            Exception exe = new Exception();
            String causeString="";
            exe.initCause(exception);
            if(exe.getCause()!=null && exe.getCause().getCause()!=null)
                causeString=  exe.getCause().getCause().getLocalizedMessage();

            System.out.println(causeString);
            exception.printStackTrace();
        }

    }

    private static void usage()
    {
        System.out.println("\nUsage: java -jar JSCP.jar CMD LOCALPATH REMOTEPATH\n");
        System.out.println("EX:\tjava -jar JSCP.jar put-append C://LocalPath//Stuff.txt /C/Users/RemotePath");
        System.out.println("EX:\tjava -jar JSCP.jar get-append C://LocalPath// /C/Users/RemotePath/Stuff.txt");
        System.out.println("\nCommands:\tget    put    get-append    put-append");
    }
}
