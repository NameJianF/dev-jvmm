package live.itrip.jvmm.server.handler;

import live.itrip.jvmm.server.CommandResult;

/**
 * @author : ERP --> fengjianfeng4
 * @date : 2021-08-17 14:05
 * description : command listener
 **/
public interface CommandListener {
    /**
     * 操作命令处理
     *
     * @param command command
     * @return CommandResult
     */
    CommandResult handleCommand(String command);
}
