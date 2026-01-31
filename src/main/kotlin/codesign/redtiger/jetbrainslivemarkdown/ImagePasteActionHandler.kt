package codesign.redtiger.jetbrainslivemarkdown

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.ide.CopyPasteManager
import java.awt.datatransfer.DataFlavor

/**
 * 기본 "붙여넣기" 액션을 가로채서 이미지 붙여넣기를 특별 처리하는 클래스입니다.
 * 이 핸들러는 plugin.xml에 등록되어 "EditorPaste" 액션보다 먼저 실행됩니다.
 */
class ImagePasteActionHandler(private val originalHandler: EditorActionHandler) : EditorActionHandler() {

    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext) {
        val project = editor.project
        if (project == null) {
            executeOriginalHandler(editor, caret, dataContext)
            return
        }

        // 현재 파일이 마크다운 파일인지 확인
        val file = FileDocumentManager.getInstance().getFile(editor.document)
        if (file?.extension?.lowercase() !in setOf("md", "markdown")) {
            executeOriginalHandler(editor, caret, dataContext)
            return
        }

        // 클립보드 내용 확인
        val transferable = CopyPasteManager.getInstance().contents
        if (transferable == null) {
            executeOriginalHandler(editor, caret, dataContext)
            return
        }

        // 클립보드에 이미지가 있는지 확인하고, 있으면 ImagePasteHandler로 처리 시도
        if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            if (ImagePasteHandler.handleImagePaste(project, editor, transferable)) {
                return // 성공 시, 원래 핸들러를 호출하지 않고 여기서 종료
            } else {
            }
        }

        // 이미지가 아니거나, 이미지 처리 실패 시 원래 핸들러 실행
        executeOriginalHandler(editor, caret, dataContext)
    }

    /**
     * 원래의 붙여넣기 핸들러를 실행합니다.
     */
    private fun executeOriginalHandler(editor: Editor, caret: Caret?, dataContext: DataContext) {
        originalHandler.execute(editor, caret, dataContext)
    }

    /**
     * 현재 컨텍스트에서 액션이 활성화될 수 있는지 여부를 원래 핸들러에 위임합니다.
     */
    override fun isEnabledForCaret(editor: Editor, caret: Caret, dataContext: DataContext?): Boolean {
        return originalHandler.isEnabled(editor, caret, dataContext)
    }
}
