# PreCheck
A plugin for IntelliJ IDEA to print maven dependency tree in a second

# 使用
鼠标光标放在Module对应的pom文件中，右键Maven->Tree，即可快速生成依赖树文件tree.txt

# 注意事项
该插件等同于命令 mvn dependency:tree -Dverbose > tree.txt，暂不支持非verbose选项
