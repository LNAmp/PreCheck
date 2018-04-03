package cn.david.precheck.pomcheck;

import cn.david.precheck.domain.SimpleArtifactNode;
import cn.david.precheck.util.Constants;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.model.MavenArtifactNode;

import java.io.PrintWriter;
import java.util.*;

/**
 * @author david
 * @since 2016年11月03日
 */
public class MavenDependencyUtil {

    private static final Set<String> slf4jDependencySet = new HashSet<String>();

    public static final String SLF4J_KEY = "slf4j_key";

    public static final String ALL_KEY = "all_key";

    static {
        slf4jDependencySet.add("slf4j-log4j12");
        slf4jDependencySet.add("logback-classic");
        slf4jDependencySet.add("log4j-slf4j-impl");
    }

    public static List<MavenArtifactNode> getAndCheckChildrenNode(List<MavenArtifactNode> currentLevelNodes, Map<String, Set<String>> targetMap, CheckerManager checkerManager)
            throws CheckNotPassException {
        if ((currentLevelNodes == null || currentLevelNodes.isEmpty())) {
            return null;
        }

        List<MavenArtifactNode> nodes = new ArrayList<MavenArtifactNode>();
        for (MavenArtifactNode node : nodes) {
            handleNode(node, targetMap);
            checkerManager.fireNodeCheck(targetMap);
            nodes.addAll(node.getDependencies());
        }

        return nodes;
    }

    //层级遍历(广度优先)
    public static void bfsCheck(List<MavenArtifactNode> nodes, CheckerManager checkerManager) throws CheckNotPassException {
        if (nodes == null || nodes.size() == 0) {
            return;
        }

        Queue<MavenArtifactNode> nodeQueue = new LinkedList<MavenArtifactNode>();
        for (MavenArtifactNode node : nodes) {
            nodeQueue.add(node);
        }

        check(nodeQueue, checkerManager);
    }

    private static void check(Queue<MavenArtifactNode> nodes, CheckerManager checkerManager) throws CheckNotPassException {
        int level = 0;
        int nextLevelCount = 0;
        int levelCount = nodes.size();
        Map<String, Set<String>> targetMap = new HashMap<String, Set<String>>();
        while ((level < 6) && (nodes.size() > 0)) {
            if (levelCount == 0) {
                levelCount = nextLevelCount;
                nextLevelCount = 0;
                level++;
                //每一层检测一次
                checkerManager.fireLevelCheck(targetMap);

            }
            MavenArtifactNode curNode = nodes.poll();
            if (curNode != null) {
                levelCount--;
                List<MavenArtifactNode> childrenNodes = curNode.getDependencies();
                if (childrenNodes != null && childrenNodes.size() > 0) {
                    for (MavenArtifactNode childrenNode : childrenNodes) {
                        nodes.add(childrenNode);
                        nextLevelCount++;
                    }
                }
                handleNode(curNode, targetMap);
            }
        }
        //如果只有一层
       checkerManager.fireLevelCheck(targetMap);
    }

    //深度优先
    public static void dfsCheck(List<MavenArtifactNode> nodes, CheckerManager checkerManager) throws CheckNotPassException {
        Map<String, Set<String>> targetMap = new HashMap<String, Set<String>>();
        int level = 0;
        check(targetMap, nodes, checkerManager, level);
    }

    //深度优先,其中level用来限制深度,防止栈溢出
    private static void check(Map<String, Set<String>> targetMap, List<MavenArtifactNode> nodes, CheckerManager checkerManager, int level) throws CheckNotPassException {
        if (nodes == null || nodes.size() == 0) {
            return;
        }
        if (level > 10) {
            return;
        }

        for (MavenArtifactNode node : nodes) {
            handleNode(node, targetMap);
            checkerManager.fireNodeCheck(targetMap);
            check(targetMap, node.getDependencies(), checkerManager, level + 1);
        }
    }

    private static void handleNode(MavenArtifactNode curNode, Map<String, Set<String>> targetMap) {
        String artifactId = curNode.getArtifact().getArtifactId();
        if (slf4jDependencySet.contains(artifactId)) {
            Set<String> slf4jSet = targetMap.get(SLF4J_KEY);
            if (slf4jSet == null) {
                targetMap.put(SLF4J_KEY, new HashSet<String>());
                slf4jSet = targetMap.get(SLF4J_KEY);
            }
            slf4jSet.add(artifactKeyName(curNode.getArtifact()));
        }
        Set<String> allNode = targetMap.get(ALL_KEY);
        if (allNode == null) {
            targetMap.put(ALL_KEY, new HashSet<String>());
            allNode = targetMap.get(ALL_KEY);
        }
        allNode.add(artifactKeyName(curNode.getArtifact()));
    }


    private static String artifactKeyName(MavenArtifact artifact) {
        return artifact.getGroupId() + ":" + artifact.getArtifactId();
    }

    public static void dfsMavenTree(List<SimpleArtifactNode> simpleArtifactNodes, List<MavenArtifactNode> nodes, SimpleArtifactNode parent, int level) {
        if (nodes == null || nodes.size() == 0) {
            return;
        }

        if (level > 16) {
            return;
        }

        for (int i = 0; i < nodes.size(); i++) {
            MavenArtifactNode node = nodes.get(i);

            SimpleArtifactNode simpleArtifactNode = new SimpleArtifactNode();
            simpleArtifactNode.setArtifactId(node.getArtifact().getArtifactId());
            simpleArtifactNode.setParent(parent);
            simpleArtifactNode.setGroupId(node.getArtifact().getGroupId());
            simpleArtifactNode.setVersion(node.getArtifact().getVersion());
            simpleArtifactNode.setLevel(level);
            simpleArtifactNode.setScope(node.getArtifact().getScope());

            if (parent != null) {
                parent.addChild(simpleArtifactNode);
            }
            simpleArtifactNodes.add(simpleArtifactNode);

            //最后一个元素
            if (i == nodes.size() - 1) {

                //小于8只需要占第8位
                if (level <= 8) {

                    byte selfLow = 0;
                    if (parent != null) {
                        selfLow  = (byte) ((byte) ((0x01) << (level - 1)) | (parent.getAncestorsLastFlagLow()));
                    } else {
                        selfLow = ((byte) ((0x01) << (level - 1)));
                    }
                    simpleArtifactNode.setAncestorsLastFlagLow(selfLow);
                } else {
                    byte selfHigh = 0;
                    if (parent != null) {
                        selfHigh = (byte) ((byte) ((0x01) << (level - 9)) | (parent.getAncestorsLastFlagHigh()));
                    } else {
                        selfHigh = (byte) ((0x01) << (level - 9));
                    }
                    simpleArtifactNode.setAncestorsLastFlagHigh(selfHigh);
                }
            } else{
                //小于8只需要占低8位,
                if (level <= 8) {

                    byte selfLow = 0;
                    if (parent != null) {
                        selfLow = parent.getAncestorsLastFlagLow();
                    }
                    simpleArtifactNode.setAncestorsLastFlagLow(selfLow);
                } else {
                    byte selfHigh = 0;
                    byte selfLow = 0;
                    if (parent != null) {
                        selfHigh = parent.getAncestorsLastFlagHigh();
                        selfLow = parent.getAncestorsLastFlagLow();
                    }
                    simpleArtifactNode.setAncestorsLastFlagHigh(selfHigh);
                    simpleArtifactNode.setAncestorsLastFlagLow(selfLow);
                }
            }

            dfsMavenTree(simpleArtifactNodes, node.getDependencies(), simpleArtifactNode, level + 1);
        }
    }

    public static void renderTree(PrintWriter writer, List<SimpleArtifactNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }

        for (SimpleArtifactNode node : nodes) {
            StringBuilder line = new StringBuilder();
            int level = node.getLevel();
            for (int i = 0; i < node.getLevel(); i++) {
                if (level <= 8) {
                    byte mask = (byte) ((byte) (0x01 << i) & node.getAncestorsLastFlagLow());
                    if (mask > 0) {
                        if (i == level - 1) {
                            line.append(Constants.SLASH);
                        } else {
                            //占位
                            line.append(" ");
                        }
                    } else {
                        line.append(Constants.VETICAL_LINE);
                    }

                    if (i == level - 1) {
                        line.append(Constants.BEFORE_SELF);
                    } else {
                        line.append(Constants.FOUR_SPACES);
                    }
                } else {
                    if (i < 8) {
                        byte mask = (byte) ((byte) (0x01 << i) & node.getAncestorsLastFlagLow());
                        if (mask == 0) {
                            line.append(Constants.VETICAL_LINE);
                        } else {
                            line.append(" ");
                        }
                        line.append(Constants.FOUR_SPACES);
                    } else {
                        byte mask = (byte) ((byte) (0x01 << (i - 8)) & node.getAncestorsLastFlagHigh());
                        if (mask > 0) {
                            if (i == level - 1) {
                                line.append(Constants.SLASH);
                            }else {
                                //占位
                                line.append(" ");
                            }
                        } else {
                            line.append(Constants.VETICAL_LINE);
                        }

                        if (i == level - 1) {
                            line.append(Constants.BEFORE_SELF);
                        } else {
                            line.append(Constants.FOUR_SPACES);
                        }
                    }

                }
            }
            line.append(node.getGroupId()).append(":");
            line.append(node.getArtifactId()).append(":");
            line.append(node.getVersion()).append(":");
            line.append(node.getScope());
            writer.write(line.toString());
            writer.println();
            writer.flush();
        }
    }

}
