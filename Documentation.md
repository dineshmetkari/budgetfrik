# Introduction #

BudgetFrik is your portable's budget control and management tool.

In today's crisis struck world we need a tool that can track all small and large expenses on our daily lives.

**Stick to a budget!**

This application will store information on exactly how much you spend on what and when.

**Accountant in a pocket!**

BudgetFrik is an aid to the control freak that tracks every spending. Control every penny, cent, öre or whatever. The convenience of having your phone at all times gives you the possibility to quickly enter and register expenses. Forget late night budget by going through endless receipts and invoices, just download the report to your PC or Mac and voila! Budget done.

If you know where your salary is going, you know which cuts or sacrifices will be the most effective.

BudgetFrik produces reports on how much are you expending by date and by expense type.

For those frequent travelers BudgetFrik can accept entries in any currency and produce consolidated reports in another. It updates to the latest exchange rated directly from the European Central Bank or the New York Stock Exchange.

**Privacy Control**

Your expenses are yours, share only with who you want. Password protect the reports.


# Getting Started #

## Installation ##

Since I haven't ponied up with the 25 quid, the app is not in the Google Android Store.

Maybe for the 1.0 and then I'll charge a dime a download until I get the investment back.

Meanwhile this is intended for developers only, so you'll have to download the <a href='http://code.google.com/android/download.html'>Android SDK Download</a> and the latest <a href='http://code.google.com/p/budgetfrik/downloads/detail?name=BudgetFrik.apk'> BudgetFrik APK</a> with the application itself and use the tools to upload to your phone/emulator.

**Simple one-liner**
```
adb install <path to the download file>BudgetFrik20081123.apk
```

For more complicated stuff (multiple emulators or mobile phone), see the <a href='http://code.google.com/android/reference/adb.html#move'> adb documentation</a>

Anyway, if you managed to get the application on board, your Android menu should look like the screen-shot below:

<table><tr><td><a href='http://picasaweb.google.com/lh/photo/jAZDN9Rr0vXQHrrug6Vl0g'><img src='http://lh6.ggpht.com/_kwr3zmbEX4M/SSmR1UirmnI/AAAAAAAAAFA/tMfj1QUQRJQ/s400/AndroidMenu.png' /></a></td></tr><tr><td>BudgetFrik Installed</td></tr></table>


**Congrats! You just installed BudgetFrik.**

## Backup Data ##


To backup your data download the following files using the debugger to your PC/Mac:
```

adb pull /data/data/org.avelino.mobile.android.budgetfrik/databases/budgetfrik_1_0.db budgetfrik_1_0.db

adb pull /data/data/org.avelino.mobile.android.budgetfrik/shared_prefs/org.avelino.mobile.android.budgetfrik_preferences.xml budgetfrik_preferences.xml

```

To restore your data upload the following files using the debugger from your PC/Mac:

```

adb push <path_to_file_your>budgetfrik_1_0.db /data/data/org.avelino.mobile.android.budgetfrik/databases/budgetfrik_1_0.db 

adb push <path_to_file_your>budgetfrik_preferences.xml /data/data/org.avelino.mobile.android.budgetfrik/shared_prefs/org.avelino.mobile.android.budgetfrik_preferences.xml

```

The gigantic directory structure is courtesy of Google, Java and myself.

## Remove Application ##

To remove ... er why do you ever wanna do that? Anyway here's how:
```
adb uninstall org.avelino.mobile.android.budgetfrik
```

## Re installation and Upgrade ##

**Simple one-liner**
```
adb install -r <path to the download file>BudgetFrik<whateverversion>.apk
```

For more complicated stuff (multiple emulators or mobile phone), see the <a href='http://code.google.com/android/reference/adb.html#move'> adb documentation</a>

## Staring The Application ##

Just click the icon that reads "BudgetFrik" and you should be on your way.

### For The First Time ###

You are greeted by a fiendly (sic) dialog like this:

<table><tr><td><a href='http://picasaweb.google.com/lh/photo/fZQ5PVDsu4yT-6og3ZqtQw'><img src='http://lh6.ggpht.com/_kwr3zmbEX4M/SSmSD6dla-I/AAAAAAAAAFw/THohXuOOksE/s288/InitialScreen.png' /></a></td></tr><tr><td>Welcome Screen</td></tr></table>

Don't worry your are not pwned. The application would connect to the European Central Bank to download the latest currency exchange rates.  It comes preloaded with some outdated currencies. This is the URL in case you are interested: http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml

If you click **Cancel** you can do that later. Don't worry this nagdialog (nagialog) only pops the first time.

# Entering Expenses #

Welcome to the BudgetFrik experience.  Add a shortcut on your phone (I have no idea how since I don't own one), and taking control of your finances should be seconds away!

Your initial screen looks like this:

<table><tr><td><a href='http://picasaweb.google.com/lh/photo/Anbb4tMYEcQvx55vcO6rRg'><img src='http://lh6.ggpht.com/_kwr3zmbEX4M/SSmSEI60zhI/AAAAAAAAAF4/Rd4JYja-nsk/s400/MainMenu.png' /></a></td></tr><tr><td>Initial Screen</td></tr></table>

Each nice little icon (see the licenses for credits) corresponds to a category of expenses. BudgetFrik comes preloaded with many common (At least in my little world) everyday expenses, and each icon represents (poorly) each one. With the phone's arrows (or emulator's) select them and see the description on top. They are sorted, well, chaotically. They will sort themselves following the _last used first_ principle.

Here they are:
  * Hobbies and Art
  * Bills and Other Expenditures
  * Coffee, Tea and Snacks
  * Games, Lotto, Gambling
  * Newspapers, Books and Magazines
  * DIY and Tools
  * TV, Movies and Entertainment
  * Food and Groceries
  * Restaurants, Delivery and Take Aways
  * Clothing and Fashion
  * Electronics and Gadgets
  * Mobile Top-Ups, Downloads, Ringtones, etc.
  * Charity and Gifts
  * Public Transport and Taxis
  * Car and Fuel
  * Air Travel
  * Rent and Household
  * Gym, Sports and Fitness
  * Accommodation
  * Office

To enter an expense in a particular, just click an icon. Then a dialog like this pops:

<table><tr><td><a href='http://picasaweb.google.com/lh/photo/sOD--ZLs5OhB2ZoZYKrJYw'><img src='http://lh3.ggpht.com/_kwr3zmbEX4M/SSmSDjxfpKI/AAAAAAAAAFo/D3xIgP8osSc/s400/ExpenseEntry.png' /></a></td></tr><tr><td>Entering Expense</td></tr></table>

Don't worry, it is made so you only enter as little info as possible. _Just enter the amount and click **Ok**_

Or if you want to have more control... This is the name of the game.

First, a list of subcategories is available, by default you don't have to select any and right now it makes no difference but it will in the future. The subcategories are arranged following the _last used first_ principle and then alphabetically. Finally some order. See below:

<table><tr><td><a href='http://picasaweb.google.com/lh/photo/WKFsTVafERGEUhPop7fuaw'><img src='http://lh4.ggpht.com/_kwr3zmbEX4M/SSmt_CSiRZI/AAAAAAAAAGQ/U2WXxvjRXls/s144/Subcategories.png' /></a></td></tr><tr><td>Sub-categories</td></tr></table>

Then, the currency is selected by default (you can change this), then you enter the amount.

To enter the amount you can just click the calculator-like buttons or use the keyboard. One day I will add the choice to change the layout to phone-like if you feel more comfortable.

Optionally, you can enter some text. Click OK and you are done.  To erase something just click the **c** button. I added a double zero for those big expenders. Click cancel if you changed your mind.

# Viewing Expenses #

You can see your expenses arranged by date clicking/touching **Calendar** on the main menu.
There are several ways to see your expenses.  By default you are to the **Day View**, clicking the menu button shows up all the different available views:
  * Day View - Where you see the expenses lump-sum on daily basis one week at a time
  * Week View - Where you see the expenses lump-sum on a weekly basis one month at a time
  * Month View - Where you see the expenses lump-sum on a monthly basis one year at a time
  * Year View - Where you see the expenses lump-sum on a year basis five years at a time

All the views work basically in the same.
At the top you have a couple of arrow buttons that will take you into the next or previous period. I.e. if you are in **Day View** the next button will take you to the next week; if you are in **Month View** the next button will take you yo the next year, and so on.
Every row represents a time period corresponding to the view - day, week, month and year. Next to the date you'll find the lump sum of the expenses you've made during that period.
When you click/touch a row on the screen the date expands and shows the individual expenses in their original currency.

To go to a particular date, just touch **Go to Date** after the menu button.
Select the date and touch **Set**
<table><tr><td><a href='http://picasaweb.google.com/lh/photo/XQIFJjNI8Iz0Mn0xS7-F6A?feat=embedwebsite'><img src='http://lh5.ggpht.com/_kwr3zmbEX4M/SfLmOvt44fI/AAAAAAAAAHo/EhtLgEIoVLw/s144/Picture%202.png' /></a></td></tr><tr><td>Go to Date</td></tr></table>


No shortcut yet to change the currency, it uses the default currency which you can change in the preferences screes.
There are few subtleties between the views:


  * Day View - Starts on Sunday
<table><tr><td><a href='http://picasaweb.google.com/lh/photo/rxbCFIK72omwm1ueEEAikA?feat=embedwebsite'><img src='http://lh3.ggpht.com/_kwr3zmbEX4M/SfLlKyJdjCI/AAAAAAAAAHI/h0Me2ZN1WI0/s288/DayView.png' /></a></td></tr><tr><td>Day View - Menu on top</td></tr></table>

  * Week View - Starts on the first week of the month, this may include days from the previous month if the 1st of the month starts, say on a Wednesday

<table><tr><td><a href='http://picasaweb.google.com/lh/photo/SnZk1XjBXYx8vxzs-TDPLw?feat=embedwebsite'><img src='http://lh5.ggpht.com/_kwr3zmbEX4M/SfLlLMN2_5I/AAAAAAAAAHY/RUGcTPBgcJA/s288/WeekView.png' /></a></td></tr><tr><td>Week View - Date Expanded</td></tr></table>

  * Month View - Nothing special
<table><tr><td><a href='http://picasaweb.google.com/lh/photo/yK-hCdN7M074s7U3wyCXuQ?feat=embedwebsite'><img src='http://lh3.ggpht.com/_kwr3zmbEX4M/SfLlK11J_DI/AAAAAAAAAHQ/WFwUQ96Geqc/s288/MonthView.png' /></a></td></tr><tr><td>Month View - Date Expanded</td></tr></table>

  * Year View - By Default shows two years in the past and two years in the future

<table><tr><td><a href='http://picasaweb.google.com/lh/photo/90MssbofRMpECTom7SLBQg?feat=embedwebsite'><img src='http://lh6.ggpht.com/_kwr3zmbEX4M/SfLlLOSvMeI/AAAAAAAAAHg/GCu8hUNS8pc/s288/YearView.png' /></a></td></tr><tr><td>Year View</td></tr></table>


# Modify Expenses #

After you entered an expense the only way - for now - to correct it is to go to the **Calendar** and search for it manually in any of the views.

First expand and touch the row.

The expenses dialog will show-up. You can change the category, amount, notes or currency.

<table><tr><td><a href='http://picasaweb.google.com/lh/photo/M6vI6q5LQdKsoP6J5B00Ew?feat=embedwebsite'><img src='http://lh6.ggpht.com/_kwr3zmbEX4M/SfLm-FAX_RI/AAAAAAAAAHw/ffH4AluCwFw/s144/Picture%203.png' /></a></td></tr><tr><td>Modify Expense</td></tr></table>

If you touch and hold on top of the expense, a context menu appears.
<table><tr><td><a href='http://picasaweb.google.com/lh/photo/NKFO_B15iRRLsL1yoeOwlA?feat=embedwebsite'><img src='http://lh5.ggpht.com/_kwr3zmbEX4M/SfLotGB6iWI/AAAAAAAAAH4/4mcWK4hr2e8/s144/Picture%204.png' /></a></td></tr><tr><td>Modify Expense - Context menu</td></tr></table>

From there you can **Edit** as described above, **Move Category**, **Change Date** or **Delete**

If you select **Move Category** a list with all the categories is shown, if you cancel (with the back button) no change is made.
<table><tr><td><a href='http://picasaweb.google.com/lh/photo/raIE4C-Gi0hnlFIAEVWryw?feat=embedwebsite'><img src='http://lh3.ggpht.com/_kwr3zmbEX4M/SfLot3sddvI/AAAAAAAAAII/n-z9V0YZ0tU/s144/Picture%206.png' /></a></td></tr><tr><td>Category List</td></tr></table>

After you select one category the subcategory list is presented. If you touch **Ok** the category is selected - no subcategory - if you touch a subcategory, that will be set.

<table><tr><td><a href='http://picasaweb.google.com/lh/photo/O3pH-4-suJcHt_Gu8Yd2oA?feat=embedwebsite'><img src='http://lh6.ggpht.com/_kwr3zmbEX4M/SfLot84AsjI/AAAAAAAAAIQ/7dNEyJY1XMk/s144/Picture%207.png' /></a></td></tr><tr><td>SubCategory List</td></tr></table>

Touch **Back** to change the category or **Cancel** to dismiss any changes.

<table><tr><td><a href='http://picasaweb.google.com/lh/photo/XQIFJjNI8Iz0Mn0xS7-F6A?feat=embedwebsite'><img src='http://lh5.ggpht.com/_kwr3zmbEX4M/SfLmOvt44fI/AAAAAAAAAHo/EhtLgEIoVLw/s144/Picture%202.png' /></a></td></tr><tr><td>Change Date</td></tr></table>

If you select **Change Date** a dialog with a date show, simply choose the date and touch **Set** and the changes will be done.
<table><tr><td>
<a href='http://picasaweb.google.com/lh/photo/cN5o6XXVVGoD7ZFCRvwi5g?feat=embedwebsite'><img src='http://lh3.ggpht.com/_kwr3zmbEX4M/SfLouLT0SiI/AAAAAAAAAIY/dMBrIGd7GzU/s144/Picture%208.png' /></a></td></tr><tr><td>Day View, Edited expenses</td></tr></table>

# Viewing Reports #

Currently there is only one report.

## Date Report ##

From the Main Menu, click the **Menu** button on your phone/emulator and select **Reports**.
An invisible progress bar will show you the progress if and only if you are wearing the emperor's new clothes at the same time.
Then you should see a screen like this:

<table><tr><td><a href='http://picasaweb.google.com/lh/photo/2qtZCLJNkMaQHyZEJsWfCA'><img src='http://lh5.ggpht.com/_kwr3zmbEX4M/SSmR2P9bKSI/AAAAAAAAAFY/cTH-wuyyTas/s400/DateReport.png' /></a></td></tr><tr><td>Date Report</td></tr></table>

Depends on the amount of data you have inserted what you will see. These are the headings and values:

  * Today's Expenses
  * Yesterday's Expenses
  * This week's Expenses
  * Last week's Expenses
  * This month's Expenses
  * Last month's Expenses
  * This year's Expenses
  * Last year's Expenses
  * Total Expenses


The values are the lump sum of expenses made on the day/week/month/year/ever.  At the bottom it will display what currency is used.  The good: All expenses are converted to the target currency using the application's exchange (which you can update automatically).  The not so good: The conversion is fixed i.e. not using the rate of date that the expense happened, but all will be converted to the application's exchange (you know you can update they, don't you?)


### Changing Report Currency ###

Easy, just click **Menu** button on your phone/emulator, then **Currency**, select the new currency and then wow! The values are re-calculated using the exiting exchange rate(did I mention about the exchange rate update, didn't I).

Here's how it looks:

<table><tr><td><a href='http://picasaweb.google.com/lh/photo/3Z2LhLC6s7ok2aBgZPucDw'><img src='http://lh5.ggpht.com/_kwr3zmbEX4M/SSmR1YEGfOI/AAAAAAAAAFI/fPCwMDMmZzs/s400/ChangeCurrency.png' /></a></td></tr><tr><td>Change Currency</td></tr></table>

I'm working on precision (for those that use Yen's or Turkish Lira) problems when changing currency in the reports. As a workaround, change the default currency and you should get better results.

### Saving Reports ###

Easy, just click **Menu** button on your phone/emulator, then **Save as CSV**. Type in the name of the file (by default the current date is used) and done.

<table><tr><td><a href='http://picasaweb.google.com/lh/photo/O_lj0u9o9ZtiA3WqxooOIQ'><img src='http://lh4.ggpht.com/_kwr3zmbEX4M/SSmSEXBJDKI/AAAAAAAAAGI/HBqXR8CjrqA/s400/ReportSaveAs.png' /></a></td></tr><tr><td>Save as CSV</td></tr></table>

The reports are saved in the `/data/data/org.avelino.mobile.android.budgetfrik/files` directory.

You can download them from your phone/emulator using adb from the Android SDK (I don't know if it is possible to 'synch' from a real phone or something) with the following command:

```

adb pull /data/data/org.avelino.mobile.android.budgetfrik/files/<your_file>.csv <your_file>.csv

```

You can open them using your favorite (or only) spreadsheet program like OpenOffice or Excel.  In some OSs and spreadsheets (I tried in a Swedish layout Asus PC), MS uses a separator different from the comma (**,**) for the CSV (comma separated values), you can change this in the preferences menu.

The file looks like this:

```

Heading;Value
"Todays Expenses:";$386.00
"Yesterdays Expenses:";$0.00
"This Weeks Expenses:";$0.00
"This Weeks Expenses:";$386.00
"This Months Expenses:";$386.00
"This Months Expenses:";$0.00
"This Years Expenses:";$386.00
"This Years Expenses:";$0.00
"Total Expenses To Date:";$386.00
"Currency:";SEK

```

Easy to work with.

## Category Report ##

Maybe in the next release

# Preferences #

What application would be complete without preferences, settings, properties or configuration?

After all you are a control freak, that's why you have this application on your phone. So you can control the application that controls your expending. Makes sense. '

On the main menu click **Menu** then **Preferences** and this nice beautiful screen will greet you.

<table><tr><td><a href='http://picasaweb.google.com/lh/photo/-QpjDYPZHargI-JripF06g'><img src='http://lh3.ggpht.com/_kwr3zmbEX4M/SSmSEJblOfI/AAAAAAAAAGA/DiBfok8CAHw/s400/Preferences.png' /></a></td></tr><tr><td>Preferences</td></tr></table>

Don't complain is the default Android preferences, I still have to figure how to tweak it so it looks nicer.

To go back to the main menu, click the back button on your phone/emulator.

## Changing Default Currency ##

Easy, just click the **Default Currency** option and a list like this pops. The current default currency is selected, scroll and choose your new default currency.

<table><tr><td><a href='http://picasaweb.google.com/lh/photo/x3NdOXncaa49wSQ-IrSdMg'><img src='http://lh3.ggpht.com/_kwr3zmbEX4M/SSmR1pontrI/AAAAAAAAAFQ/BW-4L1gJnTc/s400/CurrenciesUpdates.png' /></a></td></tr><tr><td>Change Default Currency</td></tr></table>

This will cause that all the entries you make will be done by default (you can change in the entries dialog) in that currency.  Same goes for reports.

## CSV Separator ##

If you (for some alien reason) want to change the CSV separator from the default comma, click **Menu**, then **CSV Separator** on the Preferences screen and change the value. I know, from experience that for in Microsoft Excel in Windows XP Pro Swedish locale, the separator is semi-colon **;**.

## Updating Exchange Rates ##

As of now updating the exchange rate is a good idea if you are connected to Internet and you are doing a report.  It is also a good idea if you want to capture data in other than the supplied currencies (USD/GBP/EUR/MXN/SEK). Otherwise it makes not much sense.

From the **Preferences** screen click **Menu**, then select **Update Currencies**. An invisible progress bar at the top shows you how nicely is updating. Then just a dialog in the middle, dismiss with the **back** button and you are sorted.

- No, you cannot see the rates - for now.

The currencies are updated from the European Central Bank with just what they think are the most important currencies. If your currency is not there complain to them.  Also, the precision is not great, specially for the people that use the Yen and Turkish Lira.

## Deleting Expenses ##

Ok, I included this for testing purposes. Also if you want to clean your tracks, etc.

From the **Preferences** screen click **Menu**, then select **Clear All Expenses**.
As the dialog warning you reads, this will delete all the entries you have done. Perhaps it is a good idea to backup the database before proceeding.

You are your own worst enemy, so click OK on the dialog below if you think you can stop bullets with your teeth.

<table><tr><td><a href='http://picasaweb.google.com/lh/photo/V3UGRr471oOJ0zH3ENHyLg'><img src='http://lh3.ggpht.com/_kwr3zmbEX4M/SSmR2VqwphI/AAAAAAAAAFg/qGfR51b8ZYI/s400/DeleteEntries.png' /></a></td></tr><tr><td>Death Screen</td></tr></table>

# Credits #
  * Avelino
  * My wife for letting me be so close to the PC outside the office
  * GWB and the IMF for giving me the inspiration for this app.
  * Nice people who shares their graphic design on commons.wikimedia.com
  * Nice people from Google who decided to make an exciting open mobile client development platform (breath now).


Disclosure:

I'm an fulltime employee in Sweden and I work on this during my free time.