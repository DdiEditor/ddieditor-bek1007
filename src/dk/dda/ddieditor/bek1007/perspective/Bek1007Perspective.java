package dk.dda.ddieditor.bek1007.perspective;

import org.ddialliance.ddieditor.ui.perspective.PerspectiveUtil;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import dk.dda.ddieditor.bek1007.view.SiardkView;
import dk.dda.ddieditor.bek1007.view.TableView;

public class Bek1007Perspective implements IPerspectiveFactory {
	public static final String ID = "dk.dda.ddieditor.bek1007.perspective.Bek1007Perspective";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		PerspectiveUtil.setEditorAreaVisible(layout, false);
		PerspectiveUtil.createLeftFolder(layout, 0.20f, SiardkView.ID);
		PerspectiveUtil.createRightFolder(layout, TableView.ID);
	}
}
