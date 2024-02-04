package cn.diodecn.MyOKR.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;


@RestController
@CrossOrigin
public class ApiController {

    @GetMapping("/api/connect")
    @CrossOrigin(origins = "*") // 允许来自特定源的跨域请求
    public String connect() {
        return "连接成功";
    }
}
