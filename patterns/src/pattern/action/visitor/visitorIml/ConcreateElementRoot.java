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
package pattern.action.visitor.visitorIml;

import pattern.action.visitor.visitorAbs.Element;
import pattern.action.visitor.visitorAbs.IVisitor;

import java.util.Random;

/**     
* @Package：pattern.action.visitor.visitorIml
* @ClassName：ConcreateElementRoot   
* @Description：   <p> Root 管理员</p>
* @Author： -    
* @CreatTime：2018年1月16日 上午11:47:40   
* @Modify By：   
* @ModifyTime：  2018年1月16日
* @Modify marker：   
* @version    V1.0
*/
public class ConcreateElementRoot extends Element{

	/**
	 * 定义超级管理员 root-admin 可以做啥.
	 */
	@Override
	public void doVisite() {
		Random random = new Random();
		System.out.println("大家好，我是Root管理员，我倒数"+ random.nextInt(10)+"秒，不要给我搞什么大新闻！");
	}

	/**
	 * 做具体的实现类.impl
	 */
	@Override
	public void accept(IVisitor iVisitor) {
		iVisitor.visit(this);
	}

	@Override
	public void introduce() {
		System.out.println("I'M ROOT.");
	}

}
