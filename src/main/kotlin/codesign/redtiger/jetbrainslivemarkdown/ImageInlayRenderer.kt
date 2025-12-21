package codesign.redtiger.jetbrainslivemarkdown
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Graphics2D
import java.awt.Image
import java.awt.geom.Rectangle2D
import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon

class ImageInlayRenderer(private val imagePath: String) : EditorCustomElementRenderer {

    // 이미지를 메모리에 캐싱하여 렌더링 성능 최적화
    private val image: Image? by lazy {
        try {
            val file = File(imagePath)
            if (file.exists()) {
                val img = ImageIO.read(file)
                // 에디터 폭에 맞춰 이미지 크기를 조절하고 싶다면 여기서 Scale 로직 추가 가능
                img
            } else null
        } catch (e: Exception) {
            null
        }
    }

    // Inlay가 차지할 너비 (이미지 너비)
    override fun calcWidthInPixels(inlay: Inlay<*>): Int {
        return image?.getWidth(null) ?: 20
    }

    // Inlay가 차지할 높이 (이미지 높이)
    override fun calcHeightInPixels(inlay: Inlay<*>): Int {
        return image?.getHeight(null) ?: 20
    }

    // 실제 에디터 화면에 그리는 로직
    override fun paint(
        inlay: Inlay<*>,
        g: Graphics2D,
        targetRegion: Rectangle2D,
        textAttributes: TextAttributes
    ) {
        image?.let {
            g.drawImage(it, targetRegion.x.toInt(), targetRegion.y.toInt(), null)
        } ?: run {
            g.drawString("Image not found: $imagePath", targetRegion.x.toInt(), targetRegion.y.toInt())
        }
    }
}