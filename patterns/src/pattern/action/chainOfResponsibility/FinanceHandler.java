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
 * @ClassName：FinanceHandler
 * @Description：   <p>  *模式：责任链模式
 *                     备注  公司财务,李财务*</p>
 * @Author： -
 * @CreatTime：2017年10月26日 下午1:41:00
 * @Modify By：
 * @ModifyTime：
 * @Modify marker：
 * @version V1.0
 */
public class FinanceHandler extends Handler {

    private int moneny;

    @Override
    public int getLevel() {
        return this.moneny;
    }

    @Override
    public String getnName() {
        return "Financer";
    }

    public int getMoneny() {
        return moneny;
    }

    public void setMoneny(int moneny) {
        this.moneny = moneny;
    }

    public FinanceHandler(int moneny) {
        super();
        this.moneny = moneny;
    }


}
