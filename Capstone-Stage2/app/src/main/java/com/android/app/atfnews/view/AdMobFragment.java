package com.android.app.atfnews.view;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.app.atfnews.R;
import com.android.app.atfnews.utils.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdMobFragment extends Fragment {

    private static final String TAG = "AdMobFragment";
    private PublisherInterstitialAd interstitialAd = null;
    @BindView(R.id.adView)
    AdView mAdView;
    @BindView(R.id.spinner)
    ProgressBar progressBar;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_news, container, false);
        ButterKnife.bind(this, view);
        if (!Utils.isNetworkAvailable(getContext())) performFavNewsDpActivity();
        else {
            interstitialAd = new PublisherInterstitialAd(Objects.requireNonNull(getContext()));
            interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    progressBar.setVisibility(View.VISIBLE);
                    performFavNewsDpActivity();
                    //fetch the next ad in prior
                    //requestNewInterstitial();
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    super.onAdFailedToLoad(errorCode);
                    Log.i(TAG, "Failing");
                    //prefetch the next ad
                    requestNewInterstitial();
                }

                @Override
                public void onAdLoaded() {
                    Log.i(TAG, "Loading");
                    interstitialAd.show();
                    super.onAdLoaded();
                }
            });
            requestNewInterstitial();
            progressBar.setVisibility(View.GONE);
            return view;
        }
        return view;
    }

    private void requestNewInterstitial() {
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        interstitialAd.loadAd(adRequest);
    }

    public void performFavNewsDpActivity() {
        Context context = getActivity();
        Intent i = new Intent(context, FavNewsDpActivity.class);
        context.startActivity(i);
    }


}
