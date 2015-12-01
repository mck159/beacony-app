package com.example.maciek.beacony;

import com.estimote.sdk.Beacon;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BeaconCacheManager {
    Map<Beacon, Date> beaconCache;

    public BeaconCacheManager() {
        beaconCache = new HashMap<>();
    }

    public void addOrRenewBeacon(Beacon beacon) {
        beaconCache.put(beacon, new Date());
    }

    public Boolean isBeaconInCache(Beacon beacon) {
        return beaconCache.containsKey(beacon);
    }


}
