package toplab18.runcoffee.Network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelLoginReq {

public String login;

public String password;

    public ModelLoginReq(String login, String password) {
        this.login = login;
        this.password = password;
    }
}