package codesign.redtiger.jetbrainslivemarkdown

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.CustomFoldRegion
import com.intellij.openapi.editor.CustomFoldRegionRenderer
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.ui.JBColor
import java.awt.Font
import java.awt.Graphics2D
import java.awt.geom.Rectangle2D // Rectangle2D 임포트

class HeaderRegionRenderer(
    private val text: String,
    private val level: Int
) : CustomFoldRegionRenderer {

    override fun calcWidthInPixels(region: CustomFoldRegion): Int {
        val font = getHeaderFont(region.editor)
        val metrics = region.editor.component.getFontMetrics(font)
        return metrics.stringWidth(text)
    }

    override fun calcHeightInPixels(region: CustomFoldRegion): Int {
        // 헤더 레벨에 따라 높이를 다르게 설정
        return region.editor.lineHeight + (6 - level) * 2
    }

    // 파라미터 타입을 Rectangle에서 Rectangle2D로 변경
    override fun paint(region: CustomFoldRegion, g: Graphics2D, targetRegion: Rectangle2D, textAttributes: TextAttributes) {
        val editor = region.editor
        val font = getHeaderFont(editor)

        g.font = font
        g.color = JBColor.BLUE

        val metrics = g.fontMetrics
        // Rectangle2D에서 x, y 좌표 추출
        val x = targetRegion.x.toInt()
        val y = (targetRegion.y + metrics.ascent).toInt()

        g.drawString(text, x, y)
    }

    private fun getHeaderFont(editor: Editor): Font {
        // 현재 에디터에 설정된 폰트 패밀리 이름을 가져옵니다.
        val fontName = editor.colorsScheme.editorFontName
        val baseFont = editor.colorsScheme.getFont(com.intellij.openapi.editor.colors.EditorFontType.BOLD)

        val size = baseFont.size2D * (2.0f - (level * 0.15f))

        // 중요: Font 인스턴스를 생성할 때 폰트 이름을 명시적으로 지정하여 한글 폰트를 유지합니다.
        return Font(fontName, Font.BOLD, size.toInt())
    }
}