# buildIt crawler

## notes

- Currently error responses from loading pages are just reported as empty pages, eg: 404.
Would be better to high light these in some way
Also, it might be useful to retry these requests

- CrawlerParser

handling all the edge cases around anchor tags is quite error prone, would be better to use a library for this.
also, handling more complex html like frames/iframes is not supported

- Javascript/dynamic content

The site could use javascript to render links on the fly.  This will not be picked up.  Perhaps a headless browser like phantomJs could be used to support this case.

## results

The max pages found under https://wiprodigital.com/ is 283.
This was found using a max depth of 8.  Any larger depth doesn't increase the number of pages.

## tests

Ideally TDD would be followed but this wasn't mentioned in the requirements.
Also, writing useful tests would require a dummy http server which is much more work to setup.

## running

Since there are no tests, execution is performed by running the Main class:
com.solubris.buildit.crawler.Main

The easiest way to get it running is to open the project in intellij by opening the pom.xml as a project.
Then running the Main class.
