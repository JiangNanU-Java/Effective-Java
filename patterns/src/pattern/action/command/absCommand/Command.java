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
package pattern.action.command.absCommand;

import pattern.action.command.Code;
import pattern.action.command.Product;
import pattern.action.command.Projecter;
import pattern.action.command.UI;

/**
* @Package：pattern.action.command.absCommand
* @ClassName：Command   
* @Description：   <p> 命令模式  ---  处理命令</p>
* @Author： -  
* @CreatTime：2017年10月26日 下午5:36:46   
* @Modify By：   
* @ModifyTime：  
* @Modify marker：   
* @version    V1.0
 */
public abstract class Command {
	
	/**
	 * 项目经理
	 */
	protected Projecter pm = new Projecter();
	
	/**
	 * 产品经理
	 */
    protected Product product = new Product();
    
    /**
     * UI
     */
    protected UI ui = new UI();
    
    /**
     * 码农实现
     */
    protected Code code =new Code();
    
    
/*    private List<Group> gps;
    
	public List<Group> getGps() {
		return gps;
	}

	public void setGps(List<Group> gps) {
		this.gps = gps;
	}*/


	/**
     * @Description: execute
     * @Autor: Jason - jasonandy@hotmail.com
     */
    public abstract void execute();
}
