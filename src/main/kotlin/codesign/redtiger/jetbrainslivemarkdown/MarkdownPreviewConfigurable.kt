package codesign.redtiger.jetbrainslivemarkdown

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.dsl.builder.AlignX.*
import com.intellij.ui.dsl.builder.columns
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class MarkdownPreviewConfigurable(private val project: Project) : Configurable {
    private val settings = MarkdownPreviewSettings.getInstance(project)
    private val pathField = TextFieldWithBrowseButton()

    override fun getDisplayName(): String = "Markdown Live Preview"

    override fun createComponent(): JComponent {
        // 폴더 선택 창 설정
        pathField.addBrowseFolderListener(
            "Select Image Storage Path",
            null,
            project,
            com.intellij.openapi.fileChooser.FileChooserDescriptorFactory.createSingleFolderDescriptor()
        )

        return panel {
            group("Image Management") { // 그룹으로 묶으면 시각적으로 더 깔끔합니다
                row("Image Save Path:") {
                    cell(pathField)
                        .align(FILL) // 가로로 꽉 채우기
                        .columns(50)       // 입력창 기본 길이를 크게 설정 (숫자가 클수록 길어짐)
                        .comment("이미지가 저장될 절대 경로를 지정하세요. 설정하지 않으면 프로젝트 루트에 저장됩니다.")
                }
            }
        }
    }

    override fun isModified(): Boolean = pathField.text != settings.state.imageSavePath

    override fun apply() {
        settings.state.imageSavePath = pathField.text
    }

    override fun reset() {
        pathField.text = settings.state.imageSavePath
    }
}