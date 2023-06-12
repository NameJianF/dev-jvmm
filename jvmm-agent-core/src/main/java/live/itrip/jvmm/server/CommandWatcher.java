package live.itrip.jvmm.server;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import live.itrip.jvmm.logging.AgentLogFactory;
import live.itrip.jvmm.server.handler.CommandListener;
import live.itrip.jvmm.util.GsonUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author : ERP --> fengjianfeng4
 * @date : 2021-08-17 15:23
 * description : 配置信息监控器
 **/
public class CommandWatcher implements CommandListener {
    private static final Logger LOGGER = AgentLogFactory.getLogger(CommandWatcher.class);

    private static final String FLAG_EMPTY_JSON = "{}";

    private static final String COMMAND_KEY_OP = "op";
    private static final String COMMAND_KEY_USER_NAME = "userName";
    private static final String COMMAND_KEY_CONFIG = "config";
    private static final String COMMAND_KEY_MOCK_DATA = "data";


    /**
     * 处理通过http发送到工具的命令
     *
     * @param command command
     * @return result
     */
    @Override
    public CommandResult handleCommand(String command) {
        LOGGER.info(" get http command : " + command);

        CommandResult commandResult = new CommandResult();
        if (Strings.isNullOrEmpty(command)) {
            commandResult.setCode(-1);
            commandResult.setMessage("command is null or empty.");
            return commandResult;
        }

        try {
            JsonObject jsonObject = GsonUtils.fromJson(command, JsonObject.class);
            String userName = null;
            if (jsonObject.has(COMMAND_KEY_USER_NAME)) {
                userName = jsonObject.get(COMMAND_KEY_USER_NAME).getAsString();
            }
            if (Strings.isNullOrEmpty(userName)) {
                commandResult.setCode(-1);
                commandResult.setMessage("user name is null or empty.");
                return commandResult;
            }

            String op = null;
            if (jsonObject.has(COMMAND_KEY_OP)) {
                op = jsonObject.get(COMMAND_KEY_OP).getAsString();
            }

            if (Strings.isNullOrEmpty(op)) {
                commandResult.setCode(-1);
                commandResult.setMessage("op is null or empty.");
                return commandResult;
            }

            JsonObject config = null;
            if (jsonObject.has(COMMAND_KEY_CONFIG)) {
                config = jsonObject.get(COMMAND_KEY_CONFIG).getAsJsonObject();
            }

//            JsonArray arrayData = null;
//            if (jsonObject.has(COMMAND_KEY_MOCK_DATA)) {
//                arrayData = jsonObject.get(COMMAND_KEY_MOCK_DATA).getAsJsonArray();
//            }

//            if (COMMAND_OPEN_MOCK.equalsIgnoreCase(op)) {
//                // 打开方法 mock
//                return this.openMock(userName, config);
//            } else if (COMMAND_CLOSE_MOCK.equalsIgnoreCase(op)) {
//                // 关闭方法 mock
//                return this.closeMock(userName, config);
//            } else {
//                LOGGER.warning("未处理的命令：" + op);
//            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            commandResult.setCode(-1);
            commandResult.setMessage(ex.getMessage());
        }
        return commandResult;
    }
}
