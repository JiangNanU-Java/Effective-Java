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
package pattern.structure.decorator.decoratorIml;

import pattern.structure.decorator.decoratorAbs.Decorator;
import pattern.structure.decorator.decoratorAbs.SchoolReport;

/**
* @Package：pattern.structure.decorator.decoratorIml
* @ClassName：GoodBoyDecortaor   
* @Description：   <p> 装饰器模式  - - 装饰在学校的表现</p>
* @Author： -  
* @CreatTime：2017年10月26日 下午5:20:40   
* @Modify By：   
* @ModifyTime：  
* @Modify marker：   
* @version    V1.0
 */
public class GoodBoyDecortaor extends Decorator {
	
    public GoodBoyDecortaor(SchoolReport schoolReport) {
        super(schoolReport);
    }

    /**
     * @Description:报告之前 先包装一下数据 
     * @Autor: Jason
     */
    private void goodBoyDecorator(){
        System.out.println("我在学校表现很好,没毛病!");
    }

    /**
     * 1.goodBoyDecorator 
     * 2.成绩报告.
     */
    @Override
    public void report() {
        goodBoyDecorator();
        super.report();

    }

    /**
     * 给出建议
     */
	@Override
	public void discuss(String discuss) {
		System.out.println("表现好就可以骄傲了？我骄傲过吗？");
	}
}
