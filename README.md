# SG Bus Android

Native Android version of SG Bus, mirroring the SwiftUI app at `alfredang/sgbusapp`.

## Features

- Search common Singapore bus stops by stop code, road, or landmark.
- View live arrivals from LTA DataMall `v3/BusArrival`.
- Compare the next three arrivals for each service.
- Show useful bus load and vehicle labels such as seats, standing, limited, single, double, and bendy.
- Refresh arrivals manually from the selected stop screen.

## Setup

1. Copy the example local configuration:

   ```bash
   cp local.properties.example local.properties
   ```

2. Set your LTA DataMall account key:

   ```properties
   ltaAccountKey=your_lta_datamall_account_key
   ```

3. Build:

   ```bash
   gradle :app:assembleDebug
   ```

The API key is read from `local.properties` into `BuildConfig.LTA_ACCOUNT_KEY`; keep `local.properties` out of version control.
