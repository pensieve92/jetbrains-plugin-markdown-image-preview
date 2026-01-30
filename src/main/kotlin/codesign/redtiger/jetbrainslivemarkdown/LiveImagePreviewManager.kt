package codesign.redtiger.jetbrainslivemarkdown

import com.intellij.openapi.editor.Editor
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
        }
    }
}