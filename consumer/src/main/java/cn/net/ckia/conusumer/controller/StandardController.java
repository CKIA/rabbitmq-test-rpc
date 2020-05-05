package cn.net.ckia.conusumer.controller;

import cn.net.ckia.conusumer.publish.StandardSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class StandardController {

    @Autowired
    private StandardSender standardSender;

    @GetMapping("standard/send")
    public Object sendRpc(@RequestParam("msg") String message){
        return standardSender.sendRpcMsg(message);
    }

    @GetMapping("standard/sendAsync")
    public void sendAsyncRpcMsg(@RequestParam("msg") String message){
        standardSender.sendAsyncRpcMsg(message);
    }

}
