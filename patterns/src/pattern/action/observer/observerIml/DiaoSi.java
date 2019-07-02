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
package pattern.action.observer.observerIml;

import pattern.action.observer.observerAbs.Observer;

/**
* @Package：pattern.action.observer.observerIml
* @ClassName：DiaoSi   
* @Description：   <p> 观察者模式  ---  屌丝</p>
* @Author： -  
* @CreatTime：2017年10月26日 下午5:28:58   
* @Modify By：   
* @ModifyTime：  
* @Modify marker：   
* @version    V1.0
 */
public class DiaoSi  implements Observer{
   
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DiaoSi(String name) {
        this.name = name;
    }

    public DiaoSi() {
    }

    @Override
    public void Update() {
        System.out.println(name+"已经知道女神出门,准备跟(wei)踪(sui)");
    }

	@Override
	public void Say() {
		System.out.println(name+"在下想约你可以吗？");
	}


}
