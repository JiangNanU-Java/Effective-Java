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

import pattern.action.visitor.visitorAbs.IVisitor;

/**
* @Package：pattern.action.visitor.visitorIml
* @ClassName：Visitor   
* @Description：   <p> 访问者模式  具体的访问者</p>
* @Author： -  
* @CreatTime：2017年10月26日 下午4:49:26   
* @Modify By：   
* @ModifyTime：  
* @Modify marker：   
* @version    V1.0
 */
public class Visitor implements IVisitor {

    @Override
    public void visit(ConcreateElementUser user) {
    	user.introduce();
        user.doVisite();
    }

    @Override
    public void visit(ConcreateElementAdmin admin) {
    	admin.introduce();
        admin.doVisite();
    }

	@Override
	public void visit(ConcreateElementRoot root) {
		root.introduce();
		root.doVisite();
	}
}
