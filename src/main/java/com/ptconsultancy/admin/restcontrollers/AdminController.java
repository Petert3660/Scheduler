package com.ptconsultancy.admin.restcontrollers;

import com.ptconsultancy.admin.adminsupport.BuildVersion;
import com.ptconsultancy.admin.adminsupport.ControllerConstants;
import com.ptconsultancy.admin.security.SecurityToken;
import com.ptconsultancy.messages.MessageHandler;
import com.ptconsultancy.utilities.GenerateRandomKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    private MessageHandler messageHandler;

    private SecurityToken securityToken;

    @Autowired
    public AdminController(MessageHandler messageHandler, SecurityToken securityToken) {
        this.messageHandler = messageHandler;
        this.securityToken = securityToken;
    }

    @RequestMapping(path="/healthcheck", method=RequestMethod.GET)
    public String healthcheck() {
        if (BuildVersion.getProjectTitle() != null) {
            return BuildVersion.getProjectTitle() + " is running OK";
        } else {
            return "Application is running OK";
        }
    }

    @RequestMapping(path="/shutdown/{id}/{pass}/{token}", method=RequestMethod.POST)
    public void shutdown(@PathVariable String id, @PathVariable String pass, @PathVariable String token) {
        if (id.equals(messageHandler.getMessage(ControllerConstants.ID_KEY)) && pass.equals(messageHandler.getMessage(ControllerConstants.PASS_KEY))
            && token.equals(securityToken.getValue())) {
            securityToken.setTokenLock(false);
            System.exit(ControllerConstants.EXIT_STATUS);
        }
    }

    @RequestMapping(path="/securitytoken", method=RequestMethod.GET)
    public String getSecurityToken() {

        if (!securityToken.isTokenLock()) {
            securityToken.setValue(GenerateRandomKeys.generateRandomKey(ControllerConstants.TOKEN_LENGTH, ControllerConstants.TOKEN_MODE));
            securityToken.setTokenLock(true);
            return securityToken.getValue();
        } else {
            return ControllerConstants.NO_TOKEN_MESSAGE;
        }
    }

    @RequestMapping(path="getadminid/{token}", method=RequestMethod.GET)
    public String getAdminId(@PathVariable String token) {
        if (token.equals(securityToken.getValue())) {
            securityToken.setTokenLock(false);
            return messageHandler.getMessage(ControllerConstants.ID_KEY);
        } else {
            return ControllerConstants.NO_TOKEN_MESSAGE;
        }
    }

    @RequestMapping(path="getadminpass/{token}", method=RequestMethod.GET)
    public String getAdminPass(@PathVariable String token) {
        if (token.equals(securityToken.getValue())) {
            securityToken.setTokenLock(false);
            return messageHandler.getMessage(ControllerConstants.PASS_KEY);
        } else {
            return ControllerConstants.NO_TOKEN_MESSAGE;
        }
    }
}
