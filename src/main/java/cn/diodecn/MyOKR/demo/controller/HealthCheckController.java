package cn.diodecn.MyOKR.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin
public class HealthCheckController {

    @GetMapping("/health-check")
    @CrossOrigin(origins = "*") // 允许来自特定源的跨域请求
    public ResponseEntity<String> healthCheck() {
        // 这里可以添加更多的健康检查逻辑（如数据库连接检查等）
        return new ResponseEntity<>("服务正常运行", HttpStatus.OK);
    }
}
