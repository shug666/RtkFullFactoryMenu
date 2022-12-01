package com.toptech.tvfactory.api.listener;

import android.media.tv.TvTrackInfo;
import android.net.Uri;
import android.os.Bundle;

oneway interface ICommandCallback {

    void complete(in int result, in Bundle extras);

}