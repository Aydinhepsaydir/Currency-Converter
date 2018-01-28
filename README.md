# Currency-Converter

Currency Converter - Written by Aydin Hepsaydir

My application makes use of the android libraries: https://github.com/koush/ion and https://github.com/midorikocak/currency-picker-android

Ion is a library which downloades data from the web and then formats it into various formats, in this applications case I formatted the data into a string.
Android currency picker is a library which creates a dialog box with lots of currencies and their corresponding flags.

My application gets live exchange rates (updated every hour) through this website: https://currencylayer.com/

Due to the nature of my trial account on this site (free for 1000 queries per month) I am only capable of finding exchange rates from USD to every other currency, while at first this stumped me I quickly realised that I could use those exchange rates to work out the exchange rates of every other currency.

