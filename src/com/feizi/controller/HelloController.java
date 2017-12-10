/**
  * 文件说明
  * @Description:扩展说明
  * @Copyright: 2015 dreamtech.com.cn Inc. All right reserved
  * @Version: V6.0
  */
package com.feizi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**  
 * @Author: feizi
 * @Date: 2015年5月7日 下午4:00:23 
 * @ModifyUser: feizi
 * @ModifyDate: 2015年5月7日 下午4:00:23 
 * @Version:V6.0
 */
//通过@Controller注解标识HelloController这个类是一个控制器
@Controller
public class HelloController {
	
	@RequestMapping(value="/hello",method=RequestMethod.GET)
	public String sayHello(){
		System.out.println("======================Hello World!!!================");
		return "hello";
	}
}
