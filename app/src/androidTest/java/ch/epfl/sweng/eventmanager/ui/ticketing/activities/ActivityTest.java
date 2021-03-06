package ch.epfl.sweng.eventmanager.ui.ticketing.activities;

import android.app.Activity;
import android.app.Instrumentation;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import androidx.test.espresso.intent.Intents;
import ch.epfl.sweng.eventmanager.test.repository.MockEventsRepository;
import ch.epfl.sweng.eventmanager.test.ticketing.MockTicketingService;
import ch.epfl.sweng.eventmanager.ui.ticketing.ScanningTest;
import ch.epfl.sweng.eventmanager.ui.ticketing.TicketingActivity;
import ch.epfl.sweng.eventmanager.ui.ticketing.TicketingTestRule;

import static androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent;

/**
 * @author Louis Vialar
 */
public abstract class ActivityTest<T extends TicketingActivity> extends ScanningTest {
    @Rule
    public TicketingTestRule<T> mActivityRule;

    private boolean dropIntents = true;

    ActivityTest(int eventId, Class<T> testClass) {
        super(eventId);
        this.mActivityRule = prepareRule(testClass);
    }

    protected TicketingTestRule<T> prepareRule(Class<T> testClass) {
        return new TicketingTestRule<>(testClass, eventId, MockEventsRepository.CONFIG_BY_EVENT.get(eventId));
    }

    @Before
    public void setUp() {
        Intents.init();
        if (dropIntents) {
            Intents.intending(anyIntent()).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
        }
    }

    @After
    public void cleanup() {
        Intents.release();
        mActivityRule.finishActivity();
    }

    @Override
    public MockTicketingService getTicketingService() {
        return getOrCreateTicketingService(mActivityRule.getActivity());
    }
}
