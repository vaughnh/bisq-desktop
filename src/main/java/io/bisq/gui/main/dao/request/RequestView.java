/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package io.bisq.gui.main.dao.request;

import de.jensd.fx.fontawesome.AwesomeIcon;
import io.bisq.common.locale.Res;
import io.bisq.gui.Navigation;
import io.bisq.gui.common.view.*;
import io.bisq.gui.components.MenuItem;
import io.bisq.gui.main.dao.DaoView;
import io.bisq.gui.main.dao.request.create.CreateRequestView;
import io.bisq.gui.main.dao.request.dashboard.RequestDashboardView;
import io.bisq.gui.main.dao.request.open.OpenForVoteRequestView;
import io.bisq.gui.main.dao.request.past.PastRequestView;
import io.bisq.gui.main.dao.request.proposed.ProposedRequestView;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import javax.inject.Inject;

@FxmlView
public class RequestView extends ActivatableViewAndModel {

    private final ViewLoader viewLoader;
    private final Navigation navigation;

    private MenuItem dashboard, create, proposed, openForVote, past;
    private Navigation.Listener listener;

    @FXML
    private VBox leftVBox;
    @FXML
    private AnchorPane content;

    private Class<? extends View> selectedViewClass;

    @Inject
    private RequestView(CachingViewLoader viewLoader, Navigation navigation) {
        this.viewLoader = viewLoader;
        this.navigation = navigation;
    }

    @Override
    public void initialize() {
        listener = viewPath -> {
            if (viewPath.size() != 4 || viewPath.indexOf(RequestView.class) != 2)
                return;

            selectedViewClass = viewPath.tip();
            loadView(selectedViewClass);
        };

        ToggleGroup toggleGroup = new ToggleGroup();
        dashboard = new MenuItem(navigation, toggleGroup, Res.get("shared.dashboard"), RequestDashboardView.class, AwesomeIcon.DASHBOARD);
        create = new MenuItem(navigation, toggleGroup, Res.get("dao.compensation.menuItem.createRequest"), CreateRequestView.class, AwesomeIcon.EDIT);
        proposed = new MenuItem(navigation, toggleGroup, Res.get("dao.compensation.menuItem.proposedRequests"), ProposedRequestView.class, AwesomeIcon.STACKEXCHANGE);
        openForVote = new MenuItem(navigation, toggleGroup, Res.get("dao.compensation.menuItem.requestsForVote"), OpenForVoteRequestView.class, AwesomeIcon.PENCIL);
        past = new MenuItem(navigation, toggleGroup, Res.get("dao.compensation.menuItem.pastRequests"), PastRequestView.class, AwesomeIcon.LIST);
        leftVBox.getChildren().addAll(dashboard, create, proposed, openForVote, past);
    }

    @Override
    protected void activate() {
        dashboard.activate();
        create.activate();
        proposed.activate();
        openForVote.activate();
        past.activate();

        navigation.addListener(listener);
        ViewPath viewPath = navigation.getCurrentPath();
        if (viewPath.size() == 3 && viewPath.indexOf(RequestView.class) == 2 ||
                viewPath.size() == 2 && viewPath.indexOf(DaoView.class) == 1) {
            if (selectedViewClass == null)
                selectedViewClass = CreateRequestView.class;

            loadView(selectedViewClass);

        } else if (viewPath.size() == 4 && viewPath.indexOf(RequestView.class) == 2) {
            selectedViewClass = viewPath.get(3);
            loadView(selectedViewClass);
        }
    }

    @Override
    protected void deactivate() {
        navigation.removeListener(listener);

        dashboard.deactivate();
        create.deactivate();
        proposed.deactivate();
        openForVote.deactivate();
        past.deactivate();
    }

    private void loadView(Class<? extends View> viewClass) {
        View view = viewLoader.load(viewClass);
        content.getChildren().setAll(view.getRoot());

        if (view instanceof RequestDashboardView) dashboard.setSelected(true);
        else if (view instanceof CreateRequestView) create.setSelected(true);
        else if (view instanceof ProposedRequestView) proposed.setSelected(true);
        else if (view instanceof OpenForVoteRequestView) openForVote.setSelected(true);
        else if (view instanceof PastRequestView) past.setSelected(true);
    }

    public Class<? extends View> getSelectedViewClass() {
        return selectedViewClass;
    }
}


