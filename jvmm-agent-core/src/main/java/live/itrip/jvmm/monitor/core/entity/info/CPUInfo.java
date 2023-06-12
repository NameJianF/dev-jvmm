package live.itrip.jvmm.monitor.core.entity.info;


import live.itrip.jvmm.util.GsonUtils;

/**
 * description: TODO
 * date 14:36 2023/1/31
 *
 * @author fengjianfeng
 */
public class CPUInfo {

    /**
     * CPU核心数
     */
    private int cpuNum;
    /**
     * 系统使用率
     */
    private double sys;
    /**
     * 用户使用率
     */
    private double user;
    /**
     * CPU当前等待率
     */
    private double ioWait;
    /**
     * CPU当前空闲率
     */
    private double idle;

    /**
     * CPU总的使用率
     */
    private double toTal;

    /**
     * CPU型号信息
     */
    private String cpuModel;

    private CPUInfo() {
    }

    public static CPUInfo create() {
        return new CPUInfo();
    }

    @Override
    public String toString() {
        return GsonUtils.toJson(this);
    }

    public double getToTal() {
        return toTal;
    }

    public void setToTal(double toTal) {
        this.toTal = toTal;
    }

    public String getCpuModel() {
        return cpuModel;
    }

    public void setCpuModel(String cpuModel) {
        this.cpuModel = cpuModel;
    }

    public int getCpuNum() {
        return cpuNum;
    }

    public CPUInfo setCpuNum(int cpuNum) {
        this.cpuNum = cpuNum;
        return this;
    }

    public double getSys() {
        return sys;
    }

    public CPUInfo setSys(double sys) {
        this.sys = sys;
        return this;
    }

    public double getUser() {
        return user;
    }

    public CPUInfo setUser(double user) {
        this.user = user;
        return this;
    }

    public double getIoWait() {
        return ioWait;
    }

    public CPUInfo setIoWait(double ioWait) {
        this.ioWait = ioWait;
        return this;
    }

    public double getIdle() {
        return idle;
    }

    public CPUInfo setIdle(double idle) {
        this.idle = idle;
        return this;
    }
}
