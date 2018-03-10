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

import io.bisq.common.UserThread;
import io.bisq.common.locale.Res;
import io.bisq.core.btc.wallet.BsqWalletService;
import io.bisq.core.dao.blockchain.BsqBlockChain;
import io.bisq.core.dao.blockchain.BsqBlockChainChangeDispatcher;
import io.bisq.core.dao.blockchain.BsqBlockChainListener;
import io.bisq.core.dao.request.compensation.CompensationRequest;
import io.bisq.core.dao.request.compensation.CompensationRequestManager;
import io.bisq.gui.common.view.ActivatableView;
import io.bisq.gui.common.view.FxmlView;
import io.bisq.gui.util.BsqFormatter;
import io.bisq.gui.util.Layout;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.Subscription;

import javax.inject.Inject;
import java.util.stream.Collectors;

@FxmlView
public abstract class BaseRequestView extends ActivatableView<GridPane, Void> implements BsqBlockChainListener {

    protected final CompensationRequestManager compensationRequestManger;
    protected final BsqBlockChain bsqBlockChain;
    protected final ObservableList<RequestListItem> observableList = FXCollections.observableArrayList();
    protected TableView<RequestListItem> tableView;
    protected final BsqWalletService bsqWalletService;
    protected final BsqBlockChainChangeDispatcher bsqBlockChainChangeDispatcher;
    protected final BsqFormatter bsqFormatter;
    protected SortedList<RequestListItem> sortedList = new SortedList<>(observableList);
    protected Subscription selectedCompensationRequestSubscription;
    protected RequestDisplay requestDisplay;
    protected int gridRow = 0;
    protected GridPane detailsGridPane, gridPane;
    protected SplitPane compensationRequestPane;
    protected RequestListItem selectedCompensationRequest;
    protected ChangeListener<Number> chainHeightChangeListener;
    protected ListChangeListener<CompensationRequest> compensationRequestListChangeListener;


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor, lifecycle
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    protected BaseRequestView(CompensationRequestManager compensationRequestManger,
                              BsqWalletService bsqWalletService,
                              BsqBlockChain bsqBlockChain,
                              BsqBlockChainChangeDispatcher bsqBlockChainChangeDispatcher,
                              BsqFormatter bsqFormatter) {
        this.compensationRequestManger = compensationRequestManger;
        this.bsqWalletService = bsqWalletService;
        this.bsqBlockChain = bsqBlockChain;
        this.bsqBlockChainChangeDispatcher = bsqBlockChainChangeDispatcher;
        this.bsqFormatter = bsqFormatter;
    }

    @Override
    protected void activate() {
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());

        selectedCompensationRequestSubscription = EasyBind.subscribe(tableView.getSelectionModel().selectedItemProperty(), this::onSelectCompensationRequest);

        bsqWalletService.getChainHeightProperty().addListener(chainHeightChangeListener);
        bsqBlockChainChangeDispatcher.addBsqBlockChainListener(this);
        compensationRequestManger.getAllRequests().addListener(compensationRequestListChangeListener);
        updateList();
    }

    @Override
    protected void deactivate() {
        sortedList.comparatorProperty().unbind();

        selectedCompensationRequestSubscription.unsubscribe();

        bsqWalletService.getChainHeightProperty().removeListener(chainHeightChangeListener);
        bsqBlockChainChangeDispatcher.removeBsqBlockChainListener(this);
        compensationRequestManger.getAllRequests().removeListener(compensationRequestListChangeListener);

        observableList.forEach(RequestListItem::cleanup);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onBsqBlockChainChanged() {
        // Need delay otherwise we modify list while dispatching  and cause a ConcurrentModificationException
        UserThread.execute(this::updateList);
    }

    abstract protected void updateList();


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Protected
    ///////////////////////////////////////////////////////////////////////////////////////////

    protected void doUpdateList(FilteredList<CompensationRequest> list) {
        observableList.forEach(RequestListItem::cleanup);

        observableList.setAll(list.stream()
                .map(e -> new RequestListItem(e, bsqWalletService, bsqBlockChain, bsqBlockChainChangeDispatcher, bsqFormatter))
                .collect(Collectors.toSet()));

        if (list.isEmpty() && requestDisplay != null)
            requestDisplay.removeAllFields();
    }

    protected void onSelectCompensationRequest(RequestListItem item) {
        selectedCompensationRequest = item;
        if (item != null) {
            final CompensationRequest compensationRequest = item.getCompensationRequest();
            requestDisplay.removeAllFields();
            requestDisplay.createAllFields(Res.get("dao.compensation.selectedRequest"), Layout.GROUP_DISTANCE);
            requestDisplay.setAllFieldsEditable(false);
            requestDisplay.fillWithData(compensationRequest.getPayload());
        }
    }
}

