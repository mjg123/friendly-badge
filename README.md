# friendly-badge-api

Code for creating a badge that you can put on your Readme on GitHub which indicates how friendly your community is.

You provide the name of a GitHub repo and this fetches the last 120 comments, performs sentiment analysis on them 
and gives back a summary, eg for https://github.com/npm/cli :

```
{
    "sentiment": {
        "summary_by_project_role": {
            "CONTRIBUTOR": 0.5763469114899635,
            "NONE": 0.45486778728663924,
            "MEMBER": 0.54096370190382
        },
        "extremes": [
            {
                "score": 5.431771278381348E-4,
                "body": "Still having this issue on `npm@6.9.1-next.0` :( ",
                "html_url": "https://github.com/npm/cli/pull/40#issuecomment-497448861"
            },
            {
                "score": 0.9999912977218628,
                "body": "@nikoladev Thanks!  That is awesome :D",
                "html_url": "https://github.com/npm/cli/pull/173#issuecomment-486876511"
            }
        ]
    },
    "comment_summary": {
        "count": 120,
        "newest": "2019-06-14T20:40:47Z",
        "oldest": "2019-02-06T12:01:18Z"
    }
}
```

## Running

You need the following as environment variables:

  - `AZURE_KEY` - for the Azure Text API: https://docs.microsoft.com/en-gb/azure/cognitive-services/text-analytics/overview
  - `GH_CLIENT_ID` and `GH_CLIENT_SECRET` - To get a reasonable rate-limit from GitHub: https://developer.github.com/v3/#oauth2-keysecret

To start a web server for the application, run:

    lein ring server-headless
	
Then browse to http://localhost:3000/{USER}/{REPO}/badge.json

## Todo

  - Actually generating a badge, not just JSON
  - Ability to remove certain users from the results (ie bots)
  - Better text analysis. "Sentiment" is a poor proxy for what we are tyring to measure, which is more like "friendliness" or "politeness"
  
## License

Copyright © 2019 FIXME
