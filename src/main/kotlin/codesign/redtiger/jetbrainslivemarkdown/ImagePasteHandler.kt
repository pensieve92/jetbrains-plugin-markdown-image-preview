package codesign.redtiger.jetbrainslivemarkdown

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.util.ui.ImageUtil
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.io.File
import javax.imageio.ImageIO

object ImagePasteHandler {
    fun handleImagePaste(project: Project, editor: Editor, transferable: Transferable): Boolean {
        // 1. 클립보드에 이미지가 있는지 확인
        if (!transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) return false

        try {
            val image = transferable.getTransferData(DataFlavor.imageFlavor) as? java.awt.Image ?: return false
            val bufferedImage = ImageUtil.toBufferedImage(image)

            // 2. 설정된 저장 경로 가져오기
            val settings = MarkdownPreviewSettings.getInstance(project)
            var saveDir = File(settings.state.imageSavePath)

            // 경로가 설정되지 않았으면 프로젝트 루트 아래 'images' 폴더
            if (settings.state.imageSavePath.isEmpty()) {
                val basePath = project.basePath
                saveDir = if (basePath != null) File(basePath, "images") else File("images")
            }

            if (!saveDir.exists()) saveDir.mkdirs()

            // 3. 파일명 생성 및 저장
            val fileName = ImageNameGenerator.generate()
            val targetFile = File(saveDir, fileName)
            ImageIO.write(bufferedImage, "png", targetFile)

            // 4. 마크다운 태그 삽입
            val markdownTag = "\n![${fileName}](${targetFile.absolutePath})\n"

            WriteCommandAction.runWriteCommandAction(project) {
                val offset = editor.caretModel.offset
                editor.document.insertString(offset, markdownTag)
                editor.caretModel.moveToOffset(offset + markdownTag.length)
            }

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}