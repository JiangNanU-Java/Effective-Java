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
package pattern.structure.bridge;

import pattern.structure.bridge.abs.Abstraction;
import pattern.structure.bridge.abs.Implementor;

/**
* @Package：pattern.structure.bridge
* @ClassName：RefinedAbstraction   
* @Description：   <p> 桥接模式  --- 具体抽象化角色</p>
* @Author： -  
* @CreatTime：2017年10月26日 下午5:58:08   
* @Modify By：   
* @ModifyTime：  
* @Modify marker：   
* @version    V1.0
 */
public class RefinedAbstraction extends Abstraction {

    public RefinedAbstraction(Implementor implementor) {
        super(implementor);
    }

    /**
     * 抽象类 - dosomething
     */
    @Override
    public void doSomethings() {
        super.doSomethings();
        getImplementor().doSomethingB();
        getImplementor().doSomethingA();
    }
}
