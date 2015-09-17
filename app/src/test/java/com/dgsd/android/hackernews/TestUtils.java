package com.dgsd.android.hackernews;

import com.dgsd.android.hackernews.module.AppServicesComponent;
import com.dgsd.android.hackernews.module.DaggerAppServicesComponent;
import com.dgsd.android.hackernews.module.HNModule;
import com.dgsd.android.hackernews.mvp.presenter.Presenter;
import com.dgsd.android.hackernews.mvp.view.MvpView;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RuntimeEnvironment;

import rx.functions.Action1;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Common functionality to use in app tests
 */
public class TestUtils {


    private TestUtils() {
        // No instances..
    }

    /**
     * Creates a mock instance of the given MVP view class
     *
     * @param cls The class to create
     * @return A mock instance of the class with some sane defaults
     */
    public static <T extends MvpView> T createView(Class<T> cls) {
        final T view = mock(cls);
        when(view.getContext()).thenReturn(RuntimeEnvironment.application);
        return view;
    }

    /**
     * Create a Mockito {@link Answer} which can be used to mock Dagger injections.
     * <p/>
     * This can be used as:
     * <p/>
     * <pre>
     *      final AppServicesComponent appComponent = mock(AppServicesComponent.class);
     *       doAnswer(TestUtils.createMockAppComponentAnswer(new Action1<MyPresenter>() {
     *           @Override
     *           public void call(MyPresenter presenter) {
     *               presenter.myInjectableField = mock(MyInjectableField.class);
     *           }
     *       })).when(appComponent).inject(any(MyPresenter.class));
     *
     * </pre>
     *
     * @param action The action to perform on the item
     */
    public static <T extends Presenter> Answer createMockAppComponentAnswer(final Action1<T> action) {
        return new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                if (action != null) {
                    action.call((T) invocation.getArguments()[0]);
                }
                return null;
            }
        };
    }

    /**
     * Create an app services component which can provide custom overrides through the given action
     * <p/>
     * This can be used as:
     * <p/>
     * <pre>
     *     AppServicesComponent comp = TestUtils.createAppServicesComponent(MyAwesomePresenter.class, new Action1<MyAwesomePresenter>() {
     *          @Override
     *          public void call(MyAwesomePresenter presenter) {
     *              presenter.myInjectableField = mock(MyInjectableField.class);
     *          }
     *     });
     * </pre>
     *
     * @see #createMockAppComponentAnswer(Action1)
     */
    public static <T extends Presenter> AppServicesComponent createAppServicesComponent(final Class<T> cls, final Action1<T> action) {
        return mock(AppServicesComponent.class, new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final Object arg = invocation.getArguments()[0];
                if (cls.isInstance(arg)) {
                    if (action != null) {
                        action.call(cls.cast(arg));
                    }
                    return null;
                } else {
                    return Mockito.CALLS_REAL_METHODS.answer(invocation);
                }
            }
        });
    }

    /**
     * Create a {@link AppServicesComponent} which can be used to construct presenters
     */
    public static AppServicesComponent createAppComponent() {
        return createAppComponent(new HNModule((HNApp) RuntimeEnvironment.application));
    }

    /**
     * @see {TestUtils#createAppComponent}
     */
    public static AppServicesComponent createAppComponent(HNModule module) {
        return DaggerAppServicesComponent.builder()
                .hNModule(module)
                .build();
    }
}
