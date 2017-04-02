package com.bipoller;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/signup")
public class SignUpResource {
    @POST
    public String signUp(String username, String password) {
        return "";
    }
}
