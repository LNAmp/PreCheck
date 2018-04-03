package cn.david.precheck.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author david
 * @since 2016年11月08日
 */
public class SimpleArtifactNode implements Serializable {

    private static final long serialVersionUID = -7952619218936441106L;

    private String groupId;

    private String artifactId;

    private String version;

    private String Scope;

    private byte ancestorsLastFlagLow;

    private byte ancestorsLastFlagHigh;

    private int level;

    private SimpleArtifactNode parent;

    private List<SimpleArtifactNode> children = new ArrayList<>(4);

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getScope() {
        return Scope;
    }

    public void setScope(String scope) {
        Scope = scope;
    }

    public SimpleArtifactNode getParent() {
        return parent;
    }

    public void setParent(SimpleArtifactNode parent) {
        this.parent = parent;
    }

    public List<SimpleArtifactNode> getChildren() {
        return children;
    }

    public void addChild(SimpleArtifactNode child) {
        this.children.add(child);
    }

    public byte getAncestorsLastFlagLow() {
        return ancestorsLastFlagLow;
    }

    public void setAncestorsLastFlagLow(byte ancestorsLastFlagLow) {
        this.ancestorsLastFlagLow = ancestorsLastFlagLow;
    }

    public byte getAncestorsLastFlagHigh() {
        return ancestorsLastFlagHigh;
    }

    public void setAncestorsLastFlagHigh(byte ancestorsLastFlagHigh) {
        this.ancestorsLastFlagHigh = ancestorsLastFlagHigh;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

}
