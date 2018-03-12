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

package io.bisq.gui.main.dao.proposal.closed;

import io.bisq.core.btc.wallet.BsqWalletService;
import io.bisq.core.dao.DaoPeriodService;
import io.bisq.core.dao.blockchain.BsqBlockChain;
import io.bisq.core.dao.blockchain.BsqBlockChainChangeDispatcher;
import io.bisq.core.dao.proposal.ProposalCollectionsManager;
import io.bisq.gui.common.view.FxmlView;
import io.bisq.gui.main.dao.proposal.BaseProposalView;
import io.bisq.gui.util.BsqFormatter;

import javax.inject.Inject;

@FxmlView
public class ClosedProposalsView extends BaseProposalView {

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor, lifecycle
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    private ClosedProposalsView(ProposalCollectionsManager proposalCollectionsManager,
                                DaoPeriodService daoPeriodService,
                                BsqWalletService bsqWalletService,
                                BsqBlockChain bsqBlockChain,
                                BsqBlockChainChangeDispatcher bsqBlockChainChangeDispatcher,
                                BsqFormatter bsqFormatter) {
        super(proposalCollectionsManager, bsqWalletService, bsqBlockChain, bsqBlockChainChangeDispatcher, daoPeriodService,
                bsqFormatter);
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    protected void activate() {
        super.activate();
    }

    @Override
    protected void deactivate() {
        super.deactivate();
    }

    @Override
    protected void updateList() {
        doUpdateList(proposalCollectionsManager.getClosedProposals());
    }
}

