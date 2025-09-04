# CnUsername | 慈恩又责难(？)

Allow player to use Chinese chars in username of Minecraft

允许玩家使用中文名甚至特殊字符进入服务器

介绍贴: <p>
https://www.mcbbs.net/thread-1449800-1-1.html (已似) <p>
https://www.mcbbs.co/thread-1158-1-1.html

# 目前支持的平台
| 类型                                | 支持的版本 | 兼容方式                                         |
|-----------------------------------|-------|----------------------------------------------|
| Bukkit及其衍生服务端                     | 1.13+ | :heavy_check_mark: 1.18+才可用插件模式，推荐JavaAgent  |
| BungeeCord及其衍生服务端                 | All   | :heavy_check_mark: 插件模式与JavaAgent模式通用，结果一样   |
| Fabric及其衍生产品 (包括各类Hybrid核心、客户端)   | 1.18+ | :heavy_check_mark: 仅作为FabricMod加载; 支持客户端单人游戏 |
| Forge及其衍生产品 (包括各类Hybrid核心、客户端)    | None  | :x: 理论上不支持，并没有人测试，也没人反馈                      |
| NeoForge及其衍生产品 (包括各类Hybrid核心、客户端) | None  | :x: 理论上不支持，并没有人测试，也没人反馈                      |

# 下载

### [稳定版(人工手动发版)](https://github.com/XPPlugins/CnUsername/releases)

### [测试版(Action自动构建)](https://github.com/XPPlugins/CnUsername/actions)

# 插件方式加载教程
<details>

<summary>点击展开</summary>

### 推荐有条件的服主使用[JavaAgent方式](https://github.com/XPPlugins/CnUsername#JavaAgent加载教程)加载，以解锁所有功能

1. 点[我](https://github.com/0XPYEX0/CnUsername#下载)下载<br>
2. 放入`plugins`文件夹 [仅Bukkit|BungeeCord，及其所有分支(如Spigot|Paper|WaterFall等)]<br>
3. 插件方式加载有诸多限制，如:
   <br>    ①原版实体选择器不支持特殊名字玩家. 例如无法使用`/tp`命令，请使用`/tp "<username>"`  其中`<username>`替换为玩家名字
   <br>    ②在1.20.5+，`Paper`及其分支服务端，玩家名字长度不能长于16，否则无法进入服务器. JavaAgent加载方式不受此限制<br>
4. 如需自定义正则，~~请修改 `plugins/CnUsername/pattern.txt`~~ 见[注意事项](https://github.com/XPPlugins/CnUsername#注意事项)

</details>

# JavaAgent加载教程
<details>

<summary> 点击展开 </summary>

1. 点[我](https://github.com/0XPYEX0/CnUsername#下载)下载<br>
2. 放入`服务端根目录`
3. 修改你的启动命令，在`java`后写入`-javaagent:CnUsername-version-all.jar`. 例如:
   <br>    `java -javaagent:CnUsername-1.0.7-all.jar -jar server.jar`
   <br>    **注意，此处仅为举例说明，请根据实际情况编写**
4. JavaAgent加载模式可以解锁所有功能，包括但不限于:
   <br>    ①玩家名字长度可通过修改正则自定义
   <br>    ②能够正常使用原版实体选择器选择特殊名字玩家
5. 如需自定义正则，~~修改前面启动命令为`-javaagent:CnUsername-<version>-all.jar="<正则表达式>"`，例如:
   `-javaagent:CnUsername-1.0.7-all.jar="^[a-zA-Z0-9_]{3,16}|[a-zA-Z0-9_一-龥]{2,10}$"`~~ 见[注意事项](https://github.com/XPPlugins/CnUsername#注意事项)
</details>

# FabricMod加载教程

<details>
<summary> 点击展开 </summary>

1. 点[我](https://github.com/0XPYEX0/CnUsername#下载)下载<br>
2. 把下载后的`.jar`文件放入`mods`文件夹中
3. 启动服务端 | 客户端
4. 若日志成功输出，或正常进入服务器，即生效
5. 如需自定义正则，见[注意事项](https://github.com/XPPlugins/CnUsername#注意事项)

</details>

# 注意事项

1. 在`Paper`及其分支服务端中，需要在配置文件中修改`perform-validate-username`为`false`，否则无法进入服务器；
2. 安装`AuthMe`插件的情况下，需修改`AuthMe`插件的配置文件`config.yml`中的`allowedNicknameCharacters`
   。这代表被允许的玩家名的正则表达式，否则无法进入服务器；
3. 安装`LuckPerms`插件的情况下，需修改`LuckPerms`插件的配置文件`config.yml`中的`allow-invalid-usernames`为`true`
   ，否则无法正常处理权限；
4. 安装`Skript`插件的情况下，需修改`Skript`插件的配置文件`config.sk`中的`player name regex pattern`，此为正则表达式，否则无法正常使用玩家功能.
5. 在Docker等容器环境下，如果出现
   `java.nio.file.InvalidPathException: Malformed input or input contains unmappable characters`
   异常，可通过设置Java环境参数来解决: `JAVA_TOOL_OPTIONS='-Dfile.encoding="UTF-8" -Dsun.jnu.encoding="UTF-8"’`
6. 若为Linux运行，不在容器内，也出现了5所述异常，请修改 `/etc/sysconfig/i18n` 文件，将所有的 `en_US.UTF-8` 改为
   `zh_CN.UTF-8` 后重试
7. 在`CnUsername/pattern.txt`文件中填入你的正则规则，即可自定义用户名的正则规则。修改之后，重启服务器即可。
8. 由于双端的解包器限制，玩家名字长度不能超过16个字符

默认正则规则: `^[a-zA-Z0-9_]{3,16}|[a-zA-Z0-9_一-龥]{2,10}$`
