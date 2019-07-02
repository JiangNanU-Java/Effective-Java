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
package pattern.action.state.absState;

import pattern.action.state.allState.Close;
import pattern.action.state.allState.Open;
import pattern.action.state.allState.Run;
import pattern.action.state.allState.Stop;

/**
* @Package：pattern.action.state.absState
* @ClassName：Context   
* @Description：   <p> 状态模式   -  上下文 </p>
* @Author： -  
* @CreatTime：2017年10月26日 下午4:54:51   
* @Modify By：   
* @ModifyTime：  
* @Modify marker：   
* @version    V1.0
 */
public class Context {

	/**
	 * 定义出所有的电梯状态  用在setState() 枚举状态.
	 */
    public static final Open open = new Open(); //打开状态
    public static final Close close = new Close();//关闭状态
    public static final Run run = new Run();//运行状态
    public static final Stop stop = new Stop();//停止状态

    /**
     * 当前的状态
     */
    private State nowState;

    public void setNowState(State nowState) {
        this.nowState = nowState;
        /**
         * this --> context   [其实就是nowContext]
         */
        this.nowState.setContext(this);
    }

    public State getNowState() {
        return nowState;
    }

    public void open(){
        nowState.open();
    }

    public void close(){
        nowState.close();
    }

    public void run(){
        nowState.run();
    }

    public void stop(){
        nowState.stop();
    }
}
