/*******************************************************************************
 * Copyright (C) 2012, Mathias Kinzler <mathias.kinzler@sap.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal.history.command;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.egit.ui.internal.UIText;
import org.eclipse.egit.ui.internal.dialogs.BranchRenameDialog;
import org.eclipse.egit.ui.internal.dialogs.BranchSelectionDialog;
import org.eclipse.egit.ui.internal.history.GitHistoryPage;
import org.eclipse.jface.window.Window;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * Rename a branch pointing to a commit.
 */
public class RenameBranchOnCommitHandler extends AbstractHistoryCommandHandler {
	public Object execute(ExecutionEvent event) throws ExecutionException {
		GitHistoryPage page = getPage();

		final Repository repository = getRepository(page);
		if (repository == null)
			return null;

		List<Ref> branchesOfCommit = getBranchesOfCommit(page);
		// this should have been checked by isEnabled()
		if (branchesOfCommit.isEmpty())
			return null;

		final Shell shell = getPart(event).getSite().getShell();

		final Ref branchToRename;
		if (branchesOfCommit.size() > 1) {
			BranchSelectionDialog<Ref> dlg = new BranchSelectionDialog<Ref>(
					shell,
					branchesOfCommit,
					UIText.RenameBranchOnCommitHandler_SelectBranchDialogTitle,
					UIText.RenameBranchOnCommitHandler_SelectBranchDialogMessage,
					SWT.SINGLE);
			if (dlg.open() != Window.OK)
				return null;
			branchToRename = dlg.getSelectedNode();
		} else
			branchToRename = branchesOfCommit.get(0);

		new BranchRenameDialog(shell, repository, branchToRename).open();
		return null;
	}

	@Override
	public boolean isEnabled() {
		GitHistoryPage page = getPage();
		return getRepository(page) != null
				&& !(getBranchesOfCommit(page).isEmpty());
	}
}
