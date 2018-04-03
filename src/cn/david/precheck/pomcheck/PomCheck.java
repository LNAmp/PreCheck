package cn.david.precheck.pomcheck;

import cn.david.precheck.util.Constants;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.idea.maven.model.MavenArtifactNode;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 用于check pom文件
 *
 * @author david
 * @since 2016年11月01日
 */
public class PomCheck extends AnAction {

    private CheckerManager checkerManager;

    public void actionPerformed(AnActionEvent e) {

        if (checkerManager == null) {
            checkerManager = new CheckerManager();
            checkerManager.addChecker(Checkers.newMultiSlf4jBinderChecker(true));
        }

        Project project = e.getData(CommonDataKeys.PROJECT);
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);

        if (file == null || project == null) {
            return;
        }
        String fileName = file.getName();

        if (!fileName.endsWith(Constants.POM_XML)) {
            return;
        }

        MavenProject mavenProject = MavenActionUtil.getMavenProject(e.getDataContext());

        if (mavenProject != null) {
            MavenProjectsManager projectsManager = MavenActionUtil.getProjectsManager(e.getDataContext());
            List<MavenProject> rootProjects = projectsManager.getProjects();

            if (rootProjects != null && rootProjects.size() > 0) {
                for (MavenProject mavenProject1 : rootProjects) {
                    try {
                        MavenDependencyUtil.bfsCheck(mavenProject1.getDependencyTree(), checkerManager);
                        //MavenDependencyUtil.dfsCheck(mavenProject1.getDependencyTree(), checkerManager);
                    } catch (CheckNotPassException ex) {
                        if (ex instanceof MultiSlf4jBinderException) {
                            String projectName = mavenProject1.getDisplayName();
                            MultiSlf4jBinderException ex1 = (MultiSlf4jBinderException) ex;
                            Messages.showMessageDialog(project, buildLineSplitMessage(
                                            "In project " + projectName + ", there are more than one StaticLoggerBinder, pls fix it!\n",
                                            ex1.getSlf4jBinders()),
                                    "Multi Slf4j Warning", Messages.getInformationIcon());
                        }
                        return;
                    }
                }
                Messages.showMessageDialog(project, "You pass the check!", "Congratulations!", Messages.getInformationIcon());
            } else {
                Messages.showMessageDialog(project, "There is no Maven Project!", "Sorry!", Messages.getInformationIcon());
            }
        }
    }

    private String fullQualifiedName(MavenArtifactNode node) {
        return node.getArtifact().getGroupId() + ":" + node.getArtifact().getArtifactId() + ":" + node.getArtifact().getVersion();
    }


    private String buildLineSplitMessage(Collection<String> messages) {

        return buildLineSplitMessage("", messages);
    }

    private String buildLineSplitMessage(String firstLine, Collection<String> messages) {
        if (messages == null || messages.size() == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder(firstLine);
        for (String name : messages) {
            builder.append(name).append("\n");
        }
        return builder.toString();
    }

    private String buildMessageWithSpillter(Collection<String> messages, String splitter) {
        if (messages == null || messages.size() == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (String name : messages) {
            builder.append(name).append(splitter);
        }
        return builder.toString();
    }

    private String buildDependencyList(MavenProject mavenProject) {
        List<MavenArtifactNode> dependencies = mavenProject.getDependencyTree();
        List<String> dependencyNames = new ArrayList<String>();
        if (dependencies != null && dependencies.size() > 0) {
            for (MavenArtifactNode artifactNode : dependencies) {
                String fullQualifiedName = fullQualifiedName(artifactNode);
                dependencyNames.add(fullQualifiedName);
            }
        }
        return buildLineSplitMessage(dependencyNames);
    }




}
