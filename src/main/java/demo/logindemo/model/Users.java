package demo.logindemo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.Set;

@Entity
public class Users {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native") //id에 시퀀스 자동생성, 어떻게( 생성하는지 설정
    @GenericGenerator(name = "native",strategy = "native")// 디비가 알아서 생성

    @Column(name = "user_id")
    private int id;
    private String email;

    private String name;
    private String mobile;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String pwd;
    private String role;

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    @Column(name = "create_dt")
    private Date createDt;


    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Column(name = "refresh_token")
    private String refreshToken;

    @JsonIgnore // ui로 보내지 않을 항목
    @OneToMany(mappedBy="users",fetch=FetchType.EAGER)
    private Set<Authority> authorities; // 권한을 여러개 가질수 있어서 set으로
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Date getCreateDt() {
        return createDt;
    }

    public void setCreateDt(Date createDt) {
        this.createDt = createDt;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
