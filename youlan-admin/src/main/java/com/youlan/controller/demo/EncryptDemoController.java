package com.youlan.controller.demo;

import com.youlan.controller.base.BaseController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "接口样例")
@RestController
@RequestMapping("/demo/encrypt")
@AllArgsConstructor
public class EncryptDemoController extends BaseController {

}
