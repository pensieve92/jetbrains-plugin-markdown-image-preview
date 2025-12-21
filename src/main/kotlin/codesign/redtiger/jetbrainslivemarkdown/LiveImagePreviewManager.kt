package codesign.redtiger.jetbrainslivemarkdown

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.util.TextRange
import java.util.regex.Pattern

class LiveImagePreviewManager(private val editor: Editor) {
    private val imagePattern = Pattern.compile("!\\[.*?\\]\\((.*?)\\)")
    private val activeInlays = mutableMapOf<Int, com.intellij.openapi.editor.Inlay<*>>()

    fun updatePreviews() {
        val document = editor.document
        val text = document.text
        val matcher = imagePattern.matcher(text)

        // 기존 Inlay 제거 (성능 최적화 시 변경 범위만 갱신하도록 개선 가능)
        activeInlays.values.forEach { it.dispose() }
        activeInlays.clear()

        while (matcher.find()) {
            val group = matcher.group(1) ?: continue
            val startOffset = matcher.start()
            val lineIndex = document.getLineNumber(startOffset)
            
            // 이미지를 보여줄 Inlay 삽입
            val renderer = ImageInlayRenderer(group) // 위에서 만든 렌더러
            val inlay = editor.inlayModel.addBlockElement(
                document.getLineEndOffset(lineIndex),
                true,
                false, // showAbove를 false로 설정하면 텍스트 아래에 이미지가 옵니다.
                0,
                renderer
            )
            
            inlay?.let { activeInlays[lineIndex] = it }
            
            // 커서가 해당 라인에 없다면 텍스트 숨기기 (Folding)
            toggleFolding(lineIndex, editor.caretModel.logicalPosition.line != lineIndex)
        }
    }

    fun toggleFolding(lineIndex: Int, hide: Boolean) {
        val start = editor.document.getLineStartOffset(lineIndex)
        val end = editor.document.getLineEndOffset(lineIndex)
        
        editor.foldingModel.runBatchFoldingOperation {
            val existingRegion = editor.foldingModel.getCollapsedRegionAtOffset(start)
            if (hide && existingRegion == null) {
                // 텍스트 숨기기: 가려진 텍스트 대신 빈 문자열("") 표시
                editor.foldingModel.addFoldRegion(start, end, "")?.apply {
                    isExpanded = false
                }
            } else if (!hide && existingRegion != null) {
                // 커서가 들어오면 텍스트 다시 보이기
                editor.foldingModel.removeFoldRegion(existingRegion)
            }
        }
    }
}