package live.itrip.jvmm.server;

/**
 * @author : ERP --> fengjianfeng4
 * @date : 2021-08-17 14:10
 * description : command result
 **/
public class CommandResult {
    private int code;
    private String message;
    private Object data;

    public static CommandResult createError() {
        CommandResult result = new CommandResult();
        result.setCode(-1);
        result.setMessage("error.");
        return result;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
