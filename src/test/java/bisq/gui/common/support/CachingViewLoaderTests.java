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

package bisq.gui.common.support;

import org.junit.Test;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;



import bisq.gui.common.view.AbstractView;
import bisq.gui.common.view.CachingViewLoader;
import bisq.gui.common.view.ViewLoader;

public class CachingViewLoaderTests {

    @Test
    public void test() {
        ViewLoader delegateViewLoader = mock(ViewLoader.class);

        ViewLoader cachingViewLoader = new CachingViewLoader(delegateViewLoader);

        cachingViewLoader.load(TestView1.class);
        cachingViewLoader.load(TestView1.class);
        cachingViewLoader.load(TestView2.class);

        then(delegateViewLoader).should(times(1)).load(TestView1.class);
        then(delegateViewLoader).should(times(1)).load(TestView2.class);
        then(delegateViewLoader).should(times(0)).load(TestView3.class);
    }


    static class TestView1 extends AbstractView {
    }

    static class TestView2 extends AbstractView {
    }

    static class TestView3 extends AbstractView {
    }
}
