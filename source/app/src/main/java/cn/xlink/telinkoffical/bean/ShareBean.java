package cn.xlink.telinkoffical.bean;

import java.io.Serializable;

/**
 * Created by liucr on 2016/1/18.
 */
public class ShareBean implements Serializable{

    private static final long serialVersionUID = 1L;

    private long expire_date;

    private long visible;

    private boolean userUnvisible;

    private long gen_date;

    private String state;

    private long device_id;

    private String id;

    private String share_mode;

    private long from_id;

    private String from_user;

    private String from_name;

    private String invite_code;

    private String to_user;

    private String to_name;

    private long user_id;

    private boolean fromUnvisible;

    public void setExpire_date(long expire_date) {
        this.expire_date = expire_date;
    }

    public long getExpire_date() {
        return this.expire_date;
    }

    public void setVisible(long visible) {
        this.visible = visible;
    }

    public long getVisible() {
        return this.visible;
    }

    public void setUserUnvisible(boolean userUnvisible) {
        this.userUnvisible = userUnvisible;
    }

    public boolean getUserUnvisible() {
        return this.userUnvisible;
    }

    public void setGen_date(long gen_date) {
        this.gen_date = gen_date;
    }

    public long getGen_date() {
        return this.gen_date;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

    public void setDevice_id(long device_id) {
        this.device_id = device_id;
    }

    public long getDevice_id() {
        return this.device_id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setShare_mode(String share_mode) {
        this.share_mode = share_mode;
    }

    public String getShare_mode() {
        return this.share_mode;
    }

    public void setFrom_id(long from_id) {
        this.from_id = from_id;
    }

    public long getFrom_id() {
        return this.from_id;
    }

    public void setFrom_user(String from_user) {
        this.from_user = from_user;
    }

    public String getFrom_user() {
        return this.from_user;
    }

    public void setInvite_code(String invite_code) {
        this.invite_code = invite_code;
    }

    public String getInvite_code() {
        return this.invite_code;
    }

    public void setTo_user(String to_user) {
        this.to_user = to_user;
    }

    public String getTo_user() {
        return this.to_user;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getUser_id() {
        return this.user_id;
    }

    public void setFromUnvisible(boolean fromUnvisible) {
        this.fromUnvisible = fromUnvisible;
    }

    public boolean getFromUnvisible() {
        return this.fromUnvisible;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getTo_name() {
        return to_name;
    }

    public void setTo_name(String to_name) {
        this.to_name = to_name;
    }
}
