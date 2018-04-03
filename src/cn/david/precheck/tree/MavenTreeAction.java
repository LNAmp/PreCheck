package cn.david.precheck.tree;

import cn.david.precheck.domain.SimpleArtifactNode;
import cn.david.precheck.pomcheck.MavenDependencyUtil;
import cn.david.precheck.util.Constants;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 用来将maven tree 命令输入到文件中
 *
 * @author david
 * @since 1.0.0
 */
public class MavenTreeAction extends AnAction {


    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);

        if (file == null || project == null) {
            return;
        }
        String fileName = file.getName();

        if (!fileName.endsWith(Constants.POM_XML)) {
            return;
        }

        String pomFileFullName = file.getPath();
        String treeFileFullName = pomFileFullName.replace(Constants.POM_XML, Constants.MAVEN_TREE_RESULT_FILE);

        File treeFile = new File(treeFileFullName);

        if (!treeFile.exists()) {
            try {
                treeFile.createNewFile();
            } catch (IOException e1) {
                return;
            }
        }

        List<SimpleArtifactNode> simpleArtifactNodes = new ArrayList<>();

        MavenProject mavenProject = MavenActionUtil.getMavenProject(e.getDataContext());
        if (mavenProject != null) {
            MavenDependencyUtil.dfsMavenTree(simpleArtifactNodes, mavenProject.getDependencyTree(), null, 1);

            try(PrintWriter writer = new PrintWriter(new FileOutputStream(treeFile))) {

                MavenDependencyUtil.renderTree(writer, simpleArtifactNodes);

                writer.flush();

            } catch (Exception e1) {
                return;
            }

            //打开文件
            VirtualFile treeVFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(treeFile);
            new OpenFileDescriptor(project, treeVFile).navigate(true);

        }




        //Messages.showMessageDialog(project, projectPath + "," + projectPath2, "title", Messages.getInformationIcon());
    }
}
