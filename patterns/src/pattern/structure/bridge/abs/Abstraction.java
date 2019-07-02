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
* @ClassName：Abstraction   
* @Description：   <p> 桥接模式 --- 抽象化角色</p>
* <pre> 桥接Bridge是用于把 [抽象化与实现化]解耦，使得二者可以独立变化 </pre>
* @Author： -  
* @CreatTime：2017年10月26日 下午5:58:30   
* @Modify By：   
* @ModifyTime：  
* @Modify marker：   
* @version    V1.0
 */
public abstract class Abstraction{
	
	/**
	 * Implementor  实现器   - 其实就是 抽象化  和 实现化  解耦 
	 */
    private Implementor implementor;

    public Abstraction(Implementor implementor) {
        this.implementor = implementor;
    }

    public void doSomethings(){
    	System.out.println("Abs 开始搞事情!");
        implementor.doSomethingA();
        implementor.doSomethingB();
        System.out.println("Abs 搞事情完毕!");
    }

    public Implementor getImplementor(){
        return implementor;
    }
}
