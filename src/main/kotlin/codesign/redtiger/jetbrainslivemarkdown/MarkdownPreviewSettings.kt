package codesign.redtiger.jetbrainslivemarkdown

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

@State(name = "MarkdownPreviewSettings", storages = [Storage("markdown_preview_settings.xml")])
@Service(Service.Level.PROJECT)
class MarkdownPreviewSettings(val project: Project) : PersistentStateComponent<MarkdownPreviewSettings.State> {

    class State {
        var imageSavePath: String = "" // 기본값은 비어있음 (프로젝트 루트 등)
    }

    private var myState = State()

    override fun getState(): State = myState
    override fun loadState(state: State) { myState = state }

    companion object {
        fun getInstance(project: Project): MarkdownPreviewSettings = project.getService(MarkdownPreviewSettings::class.java)
    }
}