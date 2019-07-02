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
package pattern.action.chainOfResponsibility;

import pattern.action.chainOfResponsibility.abs.Handler;

/**
* @Package：pattern.action.chainOfResponsibility
* @ClassName：BossHandler   
* @Description：   <p> 责任链模式 </p>
* @Author： -  
* @CreatTime：2017年10月26日 下午1:40:14   
* @Modify By：   
* @ModifyTime：  
* @Modify marker：   
* @version    V1.0
 */
public class BossHandler extends Handler {

	private int moneny;
	
    @Override
    public int getLevel() {
        return this.moneny;
    }

    @Override
    public String getnName() {
        return "BOSS";
    }

	public int getMoneny() {
		return moneny;
	}

	public void setMoneny(int moneny) {
		this.moneny = moneny;
	}

	/**  
	* BossHandler. 
	* @param moneny  
	*/  
	public BossHandler(int moneny) {
		super();
		this.moneny = moneny;
	}
    
}
