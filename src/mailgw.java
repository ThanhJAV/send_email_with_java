import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class mailgw {

    private String ip;
    private String port;
    private String ssl;
    private String username;
    private String password;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSsl() {
        return ssl;
    }

    public void setSsl(String ssl) {
        this.ssl = ssl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static mailgw loadConfig() {
        try {
            File directory = new File (".");
            Properties properties = new Properties();
//            properties.load(new FileInputStream(directory.getAbsolutePath()+"/conf/mailgw.properties"));
            properties.load(new FileInputStream("../conf/mailgw.properties"));

            if (properties != null){
                mailgw mail = new mailgw();

                mail.setIp(properties.containsKey("SERVER_MAIL_IP") ? properties.getProperty("SERVER_MAIL_IP").trim() : "");
                mail.setPort(properties.containsKey("SERVER_MAIL_PORT") ? properties.getProperty("SERVER_MAIL_PORT").trim() : "");
                mail.setSsl(properties.containsKey("SERVER_MAIL_SSL") ? properties.getProperty("SERVER_MAIL_SSL").trim() : "");
                mail.setUsername(properties.containsKey("SERVER_MAIL_USERNAME") ? properties.getProperty("SERVER_MAIL_USERNAME").trim() : "");
                String password = properties.containsKey("SERVER_MAIL_PASSWORD") ? properties.getProperty("SERVER_MAIL_PASSWORD").trim() : "";
//                if (password != null && !"".equals(password.trim())){
//                    mail.setPassword(com.viettel.security.PassTranformer.decrypt(password));
//                } else {
                    mail.setPassword(password);
//                }

                return mail;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString(){
        return "ip="+this.getIp()+ ";port=" + this.getPort()+ ";ssl=" + this.getSsl()+ ";username=" + this.getUsername();
    }
}
