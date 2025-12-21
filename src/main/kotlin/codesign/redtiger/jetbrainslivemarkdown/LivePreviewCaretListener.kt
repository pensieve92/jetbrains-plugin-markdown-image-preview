package codesign.redtiger.jetbrainslivemarkdown

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener

class LivePreviewCaretListener : CaretListener {
    override fun caretPositionChanged(event: CaretEvent) {
        val editor = event.editor
        val newLinenum = event.newPosition.line
        val oldLinenum = event.oldPosition?.line
        
        // 1. 이전 라인에 이미지가 있다면 다시 Preview 상태로 전환 (텍스트 숨기기)
        // 2. 현재 커서가 있는 라인에 이미지가 있다면 Editing 상태로 전환 (텍스트 보이기)
        updateInlayVisibility(editor, newLinenum, isVisible = true) 
        oldLinenum?.let { updateInlayVisibility(editor, it, isVisible = false) }
    }

    private fun updateInlayVisibility(editor: Editor, line: Int, isVisible: Boolean) {
        // FoldingModel을 사용하여 텍스트를 접거나 펼치는 로직을 여기에 구현합니다.
        // 커서가 해당 라인에 오면 editor.foldingModel.runBatchFoldingOperation { ... } 사용
    }


}