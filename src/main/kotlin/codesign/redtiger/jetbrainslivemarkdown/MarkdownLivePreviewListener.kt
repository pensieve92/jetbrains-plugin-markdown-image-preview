package codesign.redtiger.jetbrainslivemarkdown

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.ui.JBColor
import java.awt.Font

class MarkdownLivePreviewListener : CaretListener {

    override fun caretPositionChanged(event: CaretEvent) {
        updateAllStyles(event.editor)
    }

    // 스타일 업데이트 로직을 별도 함수로 분리하여 초기 호출 시에도 사용
    fun updateAllStyles(editor: Editor) {
        val project = editor.project ?: return
        val document = editor.document
        val markupModel = editor.markupModel
        val currentLine = editor.caretModel.logicalPosition.line

        // 기존 하이라이터 제거 (우리 레이어만 선택적으로 지우는 게 좋지만 일단 전체 삭제)
        markupModel.removeAllHighlighters()

        for (line in 0 until document.lineCount) {
            val start = document.getLineStartOffset(line)
            val end = document.getLineEndOffset(line)
            val content = document.getText(com.intellij.openapi.util.TextRange(start, end))
            val isCurrentLine = (line == currentLine)

            // 1. 헤더 체크 (# )
            val headerMatch = Regex("^(#+)\\s").find(content)
            if (headerMatch != null) {
                val symbol = headerMatch.groupValues[1]
                val symbolEnd = start + symbol.length + 1

                if (!isCurrentLine) {
                    applyStyle(editor, start, end, JBColor.BLUE, true)
                }
                updateSymbolFolding(editor, start, symbolEnd, isCurrentLine)
            }

            // 2. 태그 체크 (#text)
            else if (Regex("#\\S").containsMatchIn(content)) {
                val jbColor = JBColor(java.awt.Color.decode("#2E7D32"), java.awt.Color.decode("#A5D6A7"))
                applyStyle(editor, start, end, jbColor, false)
            }
        }
    }
    private fun updateSymbolFolding(editor: Editor, start: Int, end: Int, isCurrentLine: Boolean) {
        editor.foldingModel.runBatchFoldingOperation {
            val region = editor.foldingModel.getFoldRegion(start, end)

            if (isCurrentLine) {
                // 커서가 있는 줄: 기호를 보여줌 (Folding 제거 또는 펼침)
                region?.let {
                    it.isExpanded = true
                    // 혹은 아예 삭제: editor.foldingModel.removeFoldRegion(it)
                }
            } else {
                // 커서가 없는 줄: 기호를 숨김 (Folding 생성 및 접기)
                if (region == null) {
                    val newRegion = editor.foldingModel.addFoldRegion(start, end, "")
                    newRegion?.isExpanded = false
                } else {
                    region.isExpanded = false
                }
            }
        }
    }
    private fun applyStyle(editor: Editor, start: Int, end: Int, color: JBColor, isBold: Boolean) {
        val attr = TextAttributes().apply {
            foregroundColor = color
            if (isBold) fontType = Font.BOLD
        }
        // 레이어 값을 6000으로 높여 우선순위 확보
        editor.markupModel.addRangeHighlighter(
            start, end, 6000, attr, HighlighterTargetArea.EXACT_RANGE
        )
    }

}