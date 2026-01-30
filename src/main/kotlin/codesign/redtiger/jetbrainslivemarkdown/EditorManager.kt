package codesign.redtiger.jetbrainslivemarkdown

import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.editor.ex.EditorEx

class EditorManager : EditorFactoryListener {
    override fun editorCreated(event: EditorFactoryEvent) {
        val editor = event.editor as? EditorEx ?: return
        val project = editor.project ?: return

        // 1. 해당 에디터가 마크다운 파일인지 확인 (선택 사항이지만 권장)
        // val virtualFile = editor.virtualFile
        // if (virtualFile?.extension != "md") return

        // 2. 관리자 객체 생성 및 에디터에 귀속 (UserData에 저장하면 나중에 꺼내 쓰기 편함)
        val manager = LiveImagePreviewManager(editor)

        editor.addEditorMouseListener(object : com.intellij.openapi.editor.event.EditorMouseListener {
            override fun mouseClicked(e: com.intellij.openapi.editor.event.EditorMouseEvent) {
                // 클릭된 좌표가 Inlay 영역인지 확인하고, 해당 라인으로 커서 강제 이동
                val logicalPosition = editor.xyToLogicalPosition(e.mouseEvent.point)
                editor.caretModel.moveToLogicalPosition(logicalPosition)
            }
        })

        // 사용자가 타이핑할 때마다 이미지를 새로 고침
        editor.document.addDocumentListener(object : com.intellij.openapi.editor.event.DocumentListener {
            override fun documentChanged(event: com.intellij.openapi.editor.event.DocumentEvent) {
                // 성능을 위해 짧은 디바운싱(Debouncing)을 넣는 것이 좋지만, 우선은 직접 호출
                manager.updatePreviews()
            }
        })

        // 4. 초기 화면 렌더링
        manager.updatePreviews()
    }

    override fun editorReleased(event: EditorFactoryEvent) {
        // 에디터가 닫힐 때 필요한 정리 작업이 있다면 여기서 수행
    }
}