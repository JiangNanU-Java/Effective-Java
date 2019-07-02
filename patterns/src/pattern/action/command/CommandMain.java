/**
 * <html>
 * <body>
 *  <P> Copyright 1994 JsonInternational</p>
 *  <p> All rights reserved.</p>
 *  <p> Created on 19941115</p>
 *  <p> Created by Jason</p>
 *  </body>
 * </html>
 */
package pattern.action.command;

import pattern.action.command.command.AddPageCommand;

/**
* @Package：pattern.action.command
* @ClassName：CommandMain   
* @Description：   <p> 命令模式 -- 执行类 </p>
* @Author： -  
* @CreatTime：2017年10月26日 下午5:34:55   
* @Modify By：   
* @ModifyTime：  
* @Modify marker：   
* @version    V1.0
 */
public class CommandMain {

	/**
	 * @Description: Just for test
	 * @Autor: Jason- jasonandy@hotmail.com
	 */
    public static void main(String[] args) {
        Invoker invoker=new Invoker();
        invoker.setCommand(new AddPageCommand());
        invoker.Call();
    }

}
