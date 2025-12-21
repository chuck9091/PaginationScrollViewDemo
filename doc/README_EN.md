# Table of Contents

- [1. Background](#1-background)
- [2. Development Environment](#2-development-environment)
- [3. Launcher3 Review](#3-launcher3-review)
    - [3.1 UI Architecture](#31-ui-architecture)
    - [3.2 Data Architecture](#32-data-architecture)
- [4. Structural Decomposition](#4-structural-decomposition)
    - [4.1 Functional Transformation](#41-functional-transformation)
        - [4.1.1 Data Persistence](#411-data-persistence)
        - [4.1.2 Widget Trimming](#412-widget-trimming)
        - [4.1.3 Folders](#413-folders)
        - [4.1.4 Shortcuts](#414-shortcuts)
        - [4.1.5 Application Status](#415-application-status)
        - [4.1.6 Application Icons](#416-application-icons)
- [5. What Can Be Done](#5-what-can-be-done)
- [6. How to Use](#6-how-to-use)
- [7. Features to be Improved](#7-features-to-be-improved)
- [8. Others](#8-others)
- [9. License](#9-license)
    - [License Description](#license-description)
    - [Copyright Notice](#copyright-notice)

# 1. Background

Smart cockpit desktop systems are typically customized based on Android AOSP Launcher3 source code. However, Launcher3 code is overly complex, resulting in high customization costs. To reduce customization costs, this project is based on Launcher3 code and splits it into several separate modules that can be customized individually.

In the context of the thriving Vibe Coding trend, this customization project might seem redundant or even foolish. In fact, I share this sentiment. However, after importing Launcher3 into Cursor and providing specific requirements, Cursor couldn't effectively reduce development difficulty or understand the overall project context or the logical context of specific functional points.

Thus, with the assistance of the programming tool (TRAE), the initial version of this project was completed.

# 2. Development Environment

Hardware Environment:
- Allwinner H618 development board
- 2GB DDR3 memory
- 16GB eMMC storage

Software Environment:
- Android 12.0

Development Tools:
- Android Studio Otter 2 Feature Drop | 2025.2.2
- Mac Pro Tahoe
- Gradle 8.13
- JVM-openjdk version "17.0.17" 2025-10-21 LTS

# 3. Launcher3 Review

## 3.1 UI Architecture

![](WX20251221-144811@2x.png)
![](WX20251221-144844@2x.png)

All desktop elements are aggregated in a single view, which makes tracking issues with a specific view type typically require excluding other types, including both UI and logic.

## 3.2 Data Architecture

![](WX20251221-144928@2x.png)

Under the MVP architecture, in addition to loading basic item element data in the Model, data such as icon cache is also obtained here. All data is ultimately called back to the Launcher class or Widget-related views for processing.

# 4. Structural Decomposition

The initial idea was to split Launcher3 into several modules, with each module responsible for a specific function, such as application icons, Widgets, etc. However, during the actual splitting process, it was found that Launcher3's code structure is quite complex and difficult to split. For example, both desktop icons and Widgets depend on the DragLayer class. Placing them in one module doesn't reduce the overall complexity.

Therefore, it was split into the following modules:

- item_foundation: Responsible for the basic data structures and logic of item elements, such as icons, text, etc. DragLayer is also in this module as a basic view for carrying the dragging of application icons and Widgets.
- PaginationScrollView: Responsible for the implementation of paginated scrolling views, including page switching, position prediction, and other behaviors.
- pop_exchange: Responsible for Widget view generation, bottom Widget list implementation, and other functions.
- data_persistence: Responsible for data persistence, such as storage and reading of application icons, Widgets, and other data.
- router: Cross-module method calls.
- utils: Responsible for some common utility classes, such as logging, thread pools, etc.

The overall logical architecture is as follows:

![](WX20251221-145034@2x.png)

## 4.1 Functional Transformation

### 4.1.1 Data Persistence

In the original Launcher3, ContentProvider is relied upon for data processing. This remains unchanged here, but the original logic is discarded and reorganized. The data CRUD logic is abstracted and placed in a centralized location to avoid perceptual confusion.

### 4.1.2 Widget Trimming

Widgets involve data reading, preview view generation, drag exclusion, deletion, and other functions. Here, the core functions are extracted and no longer processed together with desktop icons. For general cockpit desktop development, CustomWidgets are not of much concern, so they are deleted here.

### 4.1.3 Folders

The folder function is rarely used in the cockpit projects I've encountered so far, so it's not implemented for now.

### 4.1.4 Shortcuts

For shortcuts, application icons are used directly here, specifying the application startup Activity.

### 4.1.5 Application Status

For the projects I've encountered so far, systems have their own app stores and generally don't interface with the native installer to distribute application status, such as installation, updates, uninstallation, etc. This is directly discarded here, and addition, update, and deletion can be achieved directly through the provided data operation classes.

### 4.1.6 Application Icons

In the original Launcher3, icons are cached through the IconCache DB. Here, they are directly placed in one table for processing.

# 5. What Can Be Done

- Paginated scrolling view
- Widget list

These two functions cover 80% of the use scenarios of cockpit desktops. They can be used as separate application lists, implementing left-right pagination and drag-to-page-switch, or as Widget lists, implementing Widget editing or drag-to-add/delete Widgets.

With the current project, developers only need to focus on data assembly and UI implementation, without needing to worry about the data layer.

# 6. How to Use

In the home page, you can set it up as follows:

```java
// Define the number of rows and columns for icons
final int row = 3;
final int column = 5;

// Get screen resolution
DisplayMetrics displayMetrics = new DisplayMetrics();
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
    getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
} else {
    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
}

// Get screen width and height, then divide by rows and columns respectively to get the width and height of each icon
int screenWidth = displayMetrics.widthPixels;
int screenHeight = displayMetrics.heightPixels;
LogUtils.d(TAG, "device screen size: " + screenWidth + "x" + screenHeight);

PaginationProfile paginationProfile = (PaginationProfile) new PaginationProfile.Builder()
        .setHeightPx(screenHeight)// Set the height of the paginated scroll view to screen height
        .setWidthPx(screenWidth)// Set the width of the paginated scroll view to screen width
        .setCellHeightPx(360)// Set the height of each icon to 360px, view height/number of rows
        .setCellWidthPx(384)// Set the width of each icon to 384px, view width/number of columns
        .setNumColumns(column)// Set the number of columns of the paginated scroll view to 5
        .setNumRows(row)// Set the number of rows of the paginated scroll view to 3
        .setIconDrawablePaddingPx(2)// Set the padding between each icon and text to 2px
        .setDefaultPageSpacingPx(2)// Set the default page spacing of the paginated scroll view to 2px
        .setEdgeMarginPx(2)// Set the edge margin of the paginated scroll view to 2px
        .setIconSizePx(108)// Set the size of each icon to 108px
        .setIconTextSizePx(16)// Set the text size of each icon to 16px
        .setCellTextColor(getColor(R.color.black))// Set the text color of each icon to black
        .setWorkspacePadding(new Rect(0, 0, 0, 0))// Set the workspace padding of the paginated scroll view to 0px
        .setSaveDataInDb(true)// Set whether to save data to DB, default is false
        .build();

setContentView(R.layout.activity_main);
// Get the paginated scroll view instance
PaginationScrollView paginationScrollView = findViewById(R.id.pagination_scroll_view);
// Initialize the Widget list popup view, comment out if not needed
PopUpExchangeView.getInstance().init(this);

// Get the status of whether data is saved to DB from SharedPreferences. If it needs to be saved to DB, determine whether to read cached data
// If it doesn't need to be saved to DB, directly bind the data
SharedPreferences sharedPreferences = getSharedPreferences("PaginationScrollView", MODE_PRIVATE);
boolean dataSaved = sharedPreferences.getBoolean("data_saved", false);
if (dataSaved) {
    paginationScrollView.bindItems();
} else {
    paginationScrollView.bindItems(fruits);
    sharedPreferences.edit().putBoolean("data_saved", true).apply();
}

// Set the click event for the Widget list popup view. Click to show or close the popup. If you don't need the Widget list, comment out this part of the code
FloatingActionButton popupButton = findViewById(R.id.show_pop_window);
popupButton.setOnClickListener(v -> {
    PopUpExchangeView.getInstance().showOrClosePopWindow();
});
```

# 7. Features to be Improved

- Deletion and update of item and Widget data

# 8. Others

- Add data tracking based on item operation records for analyzing user behavior.
- Add AI recommendation features based on behavior data.
- Add AI smart recommendation features based on geofencing.
- Data compliance.

# 9. License

The open source part of this project uses the Apache License 2.0. The core functionality is provided in obfuscated AAR form, following the terms of Apache License 2.0.

## License Description

- Open source code part: Follows the Apache License 2.0, allowing free use, modification, and distribution
- Core AAR modules: Provided in binary form, also following the terms of Apache License 2.0
- Commercial use: Commercial use is permitted without requiring you to open source your modifications

## Copyright Notice

Copyright 2025 [Chuck Chen or Chuck or ChuckChan as the name of the media channel]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.