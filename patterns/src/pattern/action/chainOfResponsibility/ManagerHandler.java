/**
 * <html>
 * <body>
 * <P> Copyright 1994 JsonInternational</p>
 * <p> All rights reserved.</p>
 * <p> Created on 19941115</p>
 * <p> Created by Jason</p>
 * </body>
 * </html>
 */
package pattern.action.chainOfResponsibility;

import pattern.action.chainOfResponsibility.abs.Handler;

/**
 * @Package：pattern.action.chainOfResponsibility
 * @ClassName：ManagerHandler
 * @Description：   <p> 责任链模式
 *                    备注  你的上司,牛经理</p>
 * @Author： -
 * @CreatTime：2017年10月26日 下午1:41:26
 * @Modify By：
 * @ModifyTime：
 * @Modify marker：
 * @version V1.0
 */
public class ManagerHandler extends Handler {

    private int moneny;

    @Override
    public int getLevel() {
        return this.moneny;
    }

    @Override
    public String getnName() {
        return "Manager";
    }

    public ManagerHandler(int moneny) {
        super();
        this.moneny = moneny;
    }

    public int getMoneny() {
        return moneny;
    }

    public void setMoneny(int moneny) {
        this.moneny = moneny;
    }
}
