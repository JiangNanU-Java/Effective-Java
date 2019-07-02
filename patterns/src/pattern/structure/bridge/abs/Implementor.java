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
package pattern.structure.bridge.abs;

/**
* @Package：pattern.structure.bridge.abs
* @ClassName：Implementor   
* @Description：   <p> 桥接模式 --- 实现化角色 </p>
* @Author： -  
* @CreatTime：2017年10月26日 下午5:58:48   
* @Modify By：   
* @ModifyTime：  
* @Modify marker：   
* @version    V1.0
 */
public interface Implementor {
	
	/**
	 * @Description: doSomethingA
	 * @Autor: Jason
	 */
    public void doSomethingA();
    
    /**
     * @Description: doSomethingB
     * @Autor: Jason
     */
    public void doSomethingB();
}
