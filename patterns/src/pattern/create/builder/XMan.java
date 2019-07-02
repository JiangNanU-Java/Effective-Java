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
package pattern.create.builder;

/**
* @Package：pattern.create.builder
* @ClassName：XMan   
* @Description：   <p> x战警通用属性</p>
* @Author： -    
* @CreatTime：2018年1月16日 下午2:05:50   
* @Modify By：   
* @ModifyTime：  2018年1月16日
* @Modify marker：   
* @version    V1.0
 */
public class XMan {
	
	private String name;
	 
	private Integer age;
	
    private String xFactor;
    
    private String lover;

	public String getName() {
		return name;
	}

	public XMan setName(String name) {
		this.name = name;
		return this;
	}

	public Integer getAge() {
		return age;
	}

	public XMan setAge(Integer age) {
		this.age = age;
		return this;
	}

	public String getxFactor() {
        return xFactor;
    }

    public XMan setxFactor(String xFactor) {
        this.xFactor = xFactor;
        return this;
    }

    public String getLover() {
        return lover;
    }

    public XMan setLover(String lover) {
        this.lover = lover;
        return this;
    }
}
