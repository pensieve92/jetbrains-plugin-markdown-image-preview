package codesign.redtiger.jetbrainslivemarkdown

import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.fileEditor.FileDocumentManager

class EditorManager : EditorFactoryListener {
    override fun editorCreated(event: EditorFactoryEvent) {
        val editor = event.editor
        editor.project ?: return

        // 에디터와 연결된 가상 파일(VirtualFile)을 가져옵니다.
        val document = editor.document
        val virtualFile = FileDocumentManager.getInstance().getFile(document) ?: return

        // 파일 타입이 'Markdown'인지 확인합니다.
        // build.gradle.kts에 'com.intellij.markdown' 의존성이 추가되어 있어야 합니다.
        if (virtualFile.fileType.name == "Markdown") {
            println("마크다운 파일 감지됨: ${virtualFile.name}")

            // 마크다운 파일일 때만 리스너를 등록합니다.
            editor.caretModel.addCaretListener(MarkdownLivePreviewListener())
        }
    }

    override fun editorReleased(event: EditorFactoryEvent) {
        // 에디터가 닫힐 때 리스너가 메모리 누수를 일으키지 않도록 처리할 수 있지만,
        // CaretListener는 에디터 생명주기에 종속적이므로 보통 자동 해제됩니다.
    }
}