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
package pattern.structure.bridge.db;

/**     
* @Package：pattern.structure.bridge.db
* @ClassName：MyBridge   
* @Description：   <p> MyBridge </p>
* @Author： - Jason   
* @CreatTime：2018年6月5日 下午9:36:29   
* @Modify By：   
* @ModifyTime：  2018年6月5日
* @Modify marker：   
* @version    V1.0
*/
public class MyBridge extends Bridge{

	/**  
	* MyBridge.   
	*/  
	public MyBridge() {
		super();
		System.out.println("** MyBridge 桥接器准备完毕 **");
	}

}
