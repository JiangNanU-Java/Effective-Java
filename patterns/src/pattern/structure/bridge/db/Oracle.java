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

import pattern.structure.bridge.db.inf.Driver;

/**     
* @Package：pattern.structure.bridge.db
* @ClassName：Oracle   
* @Description：   <p> Oracle</p>
* @Author： - Jason   
* @CreatTime：2018年6月5日 下午9:32:51   
* @Modify By：   
* @ModifyTime：  2018年6月5日
* @Modify marker：   
* @version    V1.0
*/
public class Oracle implements Driver{

	@Override
	public void connect() {
		System.out.println("I'm Oracle Driver.");
	}

}
