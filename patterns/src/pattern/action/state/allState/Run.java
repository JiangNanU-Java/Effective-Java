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
package pattern.action.state.allState;

import pattern.action.state.absState.Context;
import pattern.action.state.absState.State;

/**
* @Package：pattern.action.state.allState
* @ClassName：Run   
* @Description：   <p> 状态模式  - 运行状态</p>
* @Author： -  
* @CreatTime：2017年10月26日 下午4:56:57   
* @Modify By：   
* @ModifyTime：  
* @Modify marker：   
* @version    V1.0
 */
public class Run extends State {


	/**
	 * 运行状态不能开门
	 */
    @Override
    public void open() {
        //状态修改 by Json
    	System.out.println("OPEN-Run状态不能开门!");
    }

    /**
     * 运行状态门就是关着的
     */
    @Override
    public void close() {
    	//by Jason
    	System.out.println("CLOSE-Run状态门就是关着的!");
    }

    @Override
    public void run() {
        System.out.println("RUN-电梯正在Run.");
    }

    /**
     * 停止运行
     */
    @Override
    public void stop() {
        context.setNowState(Context.stop); //Run -->Stop 
        context.getNowState().stop();
    }
}
