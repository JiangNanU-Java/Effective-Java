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
package pattern.structure.adapter.adapterAbs;

/**
* @Package：pattern.structure.adapter.adapterAbs
* @ClassName：IUserInfo   
* @Description：   <p> 适配器模式 - -抽象用户类 </p>
* @Author： -  
* @CreatTime：2017年10月26日 下午6:03:19   
* @Modify By：   
* @ModifyTime：  
* @Modify marker：   
* @version    V1.0
 */
public interface IUserInfo {

	//user信息
	String getUserName();
    String getHomeAddress();
    String getMobileNumber();
    String getSex();
    
    //job信息
    String getJobPosition();
    
    
    
}
