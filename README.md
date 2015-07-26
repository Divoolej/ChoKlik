### ChoKlik

***ChoKlik*** was written during the Braincode.mobi hackaton in Poznań. It is an Android application which uses [Allegro](http://allegro.pl) API and our original algorithm to fetch and list auctions that have certain typos in their names, which basicly makes them impossible to be found in a normal way.

## Example use case
Let's say we want to check whether there are some hidden auctions for Samsung Galaxy S3.
So we launch ChoKlik and type "Samsung Galaxy S3" in the search bar.
The algorithm will generate possible misspelled words, like: 'samusung galxay s3', 'samusung galaxy s3', 'samsung galxya 3s' and so on (seriously, people do type these kinds of mistakes and anticipate that their items will be found..)
The app will list all the auctions returned by allegro search utility given the misspelled keywords generated.
The user can click any auction and be redirected to the auction page on Allegro.

***Unfortunately ChoKlik doesn't work anymore.*** This is because Allegro changed their API tokens after the hackaton, so we cannot access the auctions anymore.

## Authors
ChocKlik's authors are [Hubert Dworczynski](https://github.com/TrebuhD) &amp; [Wojciech Olejnik](https://github.com/Divoolej)
