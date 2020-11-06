// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.util.text.StringUtil
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.psi.IgnoreFile
import mobi.hsz.idea.gitignore.ui.template.UserTemplateDialog
import mobi.hsz.idea.gitignore.util.CommonDataKeys
import mobi.hsz.idea.gitignore.util.Icons

/**
 * Action that creates new user template with predefined content - i.e. from currently opened file.
 */
class CreateUserTemplateAction : AnAction(
    IgnoreBundle.message("action.createUserTemplate"),
    IgnoreBundle.message("action.createUserTemplate.description"), Icons.IGNORE
) {

    /**
     * Handles an action of adding new template.
     * Ignores action if selected file is not a [IgnoreFile] instance, otherwise shows GeneratorDialog.
     *
     * @param e action event
     */
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT) ?: return
        val file = e.getData(CommonDataKeys.PSI_FILE)
        if (file !is IgnoreFile) {
            return
        }
        var content = file.getText()
        file.getViewProvider().document?.let { document ->
            EditorFactory.getInstance().getEditors(document).first()?.let { editor ->
                val selectedText = editor.selectionModel.selectedText
                if (!StringUtil.isEmpty(selectedText)) {
                    content = selectedText
                }
            }
        }
        UserTemplateDialog(project, content!!).show()
    }

    /**
     * Updates visibility of the action presentation in various actions list.
     * Visible only for [IgnoreFile] context.
     *
     * @param e action event
     */
    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.PSI_FILE)
        if (file !is IgnoreFile) {
            e.presentation.isVisible = false
            return
        }
        templatePresentation.icon = file.getFileType().icon
    }
}