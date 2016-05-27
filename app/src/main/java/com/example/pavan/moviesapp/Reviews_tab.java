package com.example.pavan.moviesapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pavan.moviesapp.NetworkActivity.MovieReviewsResponse;
import com.example.pavan.moviesapp.NetworkActivity.ReviewsData;

import java.util.ArrayList;
import java.util.List;

import Utils.AndroidUtil;
import butterknife.BindView;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;



public class Reviews_tab extends Fragment {

    private final String LOG_TAG = getClass().getSimpleName();

    @BindView(R.id.no_reviews_msg)
    TextView no_reviews_msg;

    @BindView(R.id.reviews_list_view)
    ListView reviews_list_view;
    private long movieID;
    private List<MovieReviewsResponse> movieReviewsResponses;
    private Trailers_tab trailers_tab = new Trailers_tab();
    private MainActivityFragment mainActivityFragment = new MainActivityFragment();
    private MovieReviewsAdapter movieReviewsAdapter;


    private ReviewsData reviewsData = new ReviewsData();
    private AndroidUtil androidUtil;
    private AlertDialog.Builder builder;

    public Reviews_tab() {
        // Required empty public constructor
    }

    public static Reviews_tab newInstance(Long movieID) {
        Reviews_tab fragment = new Reviews_tab();
        Bundle args = new Bundle();
        args.putLong("movieID", movieID);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieID = getArguments().getLong("movieID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reviews_tab, container, false);


        reviews_list_view = (ListView) view.findViewById(R.id.reviews_list_view);
        no_reviews_msg = (TextView) view.findViewById(R.id.no_reviews_msg);


        androidUtil = new AndroidUtil(getContext());

        if (androidUtil.isOnline() != true) {
            no_reviews_msg.setText("Reviews cannot be shown when there is no Internet Connectivity");
            return view;
        }

        movieReviewsAdapter = new MovieReviewsAdapter(getContext());



        return view;
    }

    public void fetchReviewsData() {


        Call<ReviewsData> reviewsDataCall = trailers_tab.getApi().REVIEWS_DATA_CALL(movieID, mainActivityFragment.getAPI_KEY());

        reviewsDataCall.enqueue(new Callback<ReviewsData>() {
            @Override
            public void onResponse(Response<ReviewsData> response, Retrofit retrofit) {


                reviewsData = response.body();
                movieReviewsResponses = reviewsData.getReviewsResponse();

                movieReviewsAdapter.noOfReviews = reviewsData.getReviewsResponse().size();

                if (response.body().getReviewsResponse().size() == 0) {
                    no_reviews_msg.setText("No Reviews Found for this Movie");
                } else
                for (MovieReviewsResponse movieReviewsResponse : movieReviewsResponses) {
                    movieReviewsAdapter.author_name.add(movieReviewsResponse.getAuthor());
                    movieReviewsAdapter.author_review.add(movieReviewsResponse.getContent());
                }
                reviews_list_view.setAdapter(movieReviewsAdapter);
            }

            @Override
            public void onFailure(Throwable t) {
                builder.setMessage("Sorry, We couldn't fetch the movie reviews information. Inconvenience regretted").setCancelable(false)
                        .setPositiveButton("It's Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        outState.putStringArrayList("author_name", (ArrayList<String>) movieReviewsAdapter.author_name);
        outState.putStringArrayList("author_review", (ArrayList<String>) movieReviewsAdapter.author_review);
        outState.putInt("noOfReviews", movieReviewsAdapter.noOfReviews);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            movieReviewsAdapter.author_name = savedInstanceState.getStringArrayList("author_name");
            movieReviewsAdapter.author_review = savedInstanceState.getStringArrayList("author_review");
            movieReviewsAdapter.noOfReviews = savedInstanceState.getInt("noOfReviews");

            if (movieReviewsAdapter.noOfReviews == 0)
                no_reviews_msg.setText("No Reviews Found for this Movie");
            else
                reviews_list_view.setAdapter(movieReviewsAdapter);

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (movieReviewsAdapter.author_name.isEmpty() || movieReviewsAdapter.author_name == null)
            fetchReviewsData();
    }
}
