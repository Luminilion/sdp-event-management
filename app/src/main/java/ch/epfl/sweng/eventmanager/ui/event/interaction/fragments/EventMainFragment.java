package ch.epfl.sweng.eventmanager.ui.event.interaction.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ch.epfl.sweng.eventmanager.R;
import ch.epfl.sweng.eventmanager.notifications.JoinedEventFeedbackStrategy;
import ch.epfl.sweng.eventmanager.notifications.JoinedEventStrategy;
import ch.epfl.sweng.eventmanager.notifications.NotificationScheduler;
import ch.epfl.sweng.eventmanager.repository.FeedbackRepository;
import ch.epfl.sweng.eventmanager.ui.event.interaction.EventShowcaseActivity;
import ch.epfl.sweng.eventmanager.ui.tools.ImageLoader;
import dagger.android.support.AndroidSupportInjection;

import javax.inject.Inject;

/**
 * Our main view on the 'visitor' side of the event. Displays a general description of the event.
 */
public class EventMainFragment extends AbstractFeedbackFragment {
    private static final String TAG = "EventMainFragment";

    @Inject
    ImageLoader loader;

    @BindView(R.id.main_fragment_news)
    Button news;
    @BindView(R.id.main_fragment_schedule)
    Button schedule;
    @BindView(R.id.main_fragment_map)
    Button map;
    @BindView(R.id.more_feedback_button)
    Button feedback;
    @BindView(R.id.feedback_ratingBar)
    RatingBar feedbackBar;
    @BindView(R.id.event_description)
    TextView eventDescription;
    @BindView(R.id.event_image)
    ImageView eventImage;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.event_feedback_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.recent_feedback_text)
    TextView textView;
    @BindView(R.id.separation_feedback)
    ImageView imageView;

    private EventShowcaseActivity showcaseActivity;

    private RatingsRecyclerViewAdapter ratingsRecyclerViewAdapter = new RatingsRecyclerViewAdapter();

    public EventMainFragment() {
        // Required empty public constructor
        super(R.layout.fragment_event_main);
    }

    @Override
    public void onResume() {
        super.onResume();

        model.getEvent().observe(this, ev -> {
            if (ev == null) {
                Log.e(TAG, "Got null event");
                return;
            }

            // FIXME handle NullPointerException in setTitle
            // Set window title
            getActivity().setTitle(ev.getName());

            eventDescription.setText(ev.getDescription());
            eventDescription.setVisibility(View.VISIBLE);

            loader.loadImageWithSpinner(ev, getContext(), eventImage, null);
            eventImage.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            feedbackBar.setIsIndicator(true);
            repository.getMeanRating(ev.getId()).observe(this, feedbackBar::setRating);

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) ButterKnife.bind(this, view);

        recyclerView.setAdapter(ratingsRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        model.getEvent().observe(this, ev -> {
            repository.getRatings(ev.getId()).observe(this, ratings -> {
                if (ratings != null && ratings.size() > 0) {
                    //display only the most recent feedback
                    ratingsRecyclerViewAdapter.setContent(ratings.subList(0,1));
                    feedback.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                }else{
                    feedback.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);
                }
            });
        });

        showcaseActivity = (EventShowcaseActivity) getParentActivity();

        // FIXME Handle NullPointerExceptions from the ChangeFragment
        news.setOnClickListener(v -> showcaseActivity.callChangeFragment(
                EventShowcaseActivity.FragmentType.NEWS, true));

        map.setOnClickListener(v -> showcaseActivity.callChangeFragment(
                EventShowcaseActivity.FragmentType.MAP, true));

        schedule.setOnClickListener(v -> showcaseActivity.callChangeFragment(
                EventShowcaseActivity.FragmentType.SCHEDULE, true));

        feedback.setOnClickListener(v -> ((EventShowcaseActivity) getActivity())
                .changeFragment(new EventFeedbackFragment(), true));

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem menuItem = menu.findItem(R.id.menu_showcase_activity_join_id);
        menuItem.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
