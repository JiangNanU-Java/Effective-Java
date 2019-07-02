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
package pattern.structure.proxy;

import pattern.structure.proxy.dynamicProxy.RealSujectImpl;
import pattern.structure.proxy.dynamicProxy.Subject;
import pattern.structure.proxy.dynamicProxy.SubjectInvocationHandler;
import pattern.structure.proxy.staticProxy.Proxy;
import pattern.structure.proxy.staticProxy.RealSuject;

/**
* @Package：pattern.structure.proxy
* @ClassName：ProxyMain   
* @Description：   <p> 代理模式运行类</p>
* @Author： -  
* @CreatTime：2017年10月26日 下午1:45:08   
* @Modify By：   
* @ModifyTime：  
* @Modify marker：   
* @version    V1.0
 */
public class ProxyMain {

    public static void main(String[] args) {
    	//静态代理
    	staticProxy();
    	//动态代理
        dynamicProxy();
    }

    /**
     * 静态代理
     */
    private static void staticProxy() {
        RealSuject realSuject = new RealSuject();
        Proxy proxy = new Proxy(realSuject);
        proxy.request();
    }

   /**
    * 动态代理
    */
    private static void dynamicProxy(){
        RealSujectImpl realSubject=new RealSujectImpl();
        SubjectInvocationHandler handler=new SubjectInvocationHandler(realSubject);
        Subject subject= (Subject) handler.getProxy();
        subject.request();
    }
}