package jscp;

import com.jcraft.jsch.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class Controller implements Initializable
{
    JSch jsch  = new JSch();

    Session session;

    String host, user, port;

    SftpProgressMonitor monitor;

    Channel channel;

    ChannelSftp secureChannel;

    boolean darkLight;

    @FXML RadioButton getRadio, getAppendRadio, putRadio, putAppendRadio;

    @FXML TextField localField, remoteField, hostIPField, userNameField;

    @FXML PasswordField passwordField;

    ToggleGroup cmdRadioGroup = new ToggleGroup();

    Set<RadioButton> cmdRadioSet;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        cmdRadioSet = new HashSet<>(Arrays.asList(getRadio, getAppendRadio, putRadio, putAppendRadio));

        for (RadioButton radio : cmdRadioSet)
        {
            radio.setToggleGroup(cmdRadioGroup);
        }
    }

    @FXML
    private void createSession(ActionEvent event) throws JSchException
    {
        user = userNameField.getText();
        host = hostIPField.getText();
        port = "22";
        session = jsch.getSession(user, host, Integer.valueOf(port));

        session.setConfig("StrictHostKeyChecking", "no");

        session.setPassword(passwordField.getText());

        session.connect();

        channel = session.openChannel("sftp");


        secureChannel = (ChannelSftp) channel;

        secureChannel.connect();
        System.out.println("Successfully Connected to " + user + "@" + host + ":" + port);
    }

    @FXML
    private void sendCmd(ActionEvent event)
    {

        try
        {
            if (getRadio.isSelected())
            {
                secureChannel.get( remoteField.getText(), localField.getText(), monitor, ChannelSftp.OVERWRITE);
                System.out.println("Getting " + remoteField.getText() + " to " + localField.getText());
            }
            if (getAppendRadio.isSelected())
            {
                secureChannel.get(remoteField.getText(), localField.getText(), monitor, ChannelSftp.APPEND);
                System.out.println("Get Appending " + remoteField.getText() + " to " + localField.getText());
            }
            if (putRadio.isSelected())
            {
                secureChannel.put( localField.getText(), remoteField.getText(), monitor, ChannelSftp.OVERWRITE);
                System.out.println("Putting " + localField.getText() + " to " + remoteField.getText());
            }
            if (putAppendRadio.isSelected())
            {
                secureChannel.put(localField.getText(), remoteField.getText(), monitor, ChannelSftp.APPEND);
                System.out.println("Put Appending " + localField.getText() + " to " + remoteField.getText());
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

    @FXML
    private void disconnect(ActionEvent event)
    {
        secureChannel.disconnect();
        session.disconnect();
        System.out.println("Disconnected");
    }

    @FXML
    private void darkMode(ActionEvent event)
    {
        Node node = (Node)event.getSource();
        if (!darkLight)
        {

            node.getScene().getRoot().setStyle("-fx-base:black");
            darkLight = true;
        }
        else if(darkLight)
        {
            node.getScene().getRoot().setStyle("");
            darkLight = false;
        }

    }
}
