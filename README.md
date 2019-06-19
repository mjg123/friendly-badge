# friendly-badge-api

![FriendlyBadge scores itself](https://friendly-badge.herokuapp.com/mjg123/friendly-badge/badge.svg)

Code for creating a badge (like the one above) that you can put on your repo, indicating how positive your community is. It works by performing sentiment analysis of the comments on the repo.

You provide the name of a GitHub repo and this fetches the last 120 comments, performs sentiment analysis on them 
and gives back a summary, eg for https://github.com/npm/cli you can see the result at https://friendly-badge.herokuapp.com/npm/cli/badge.json:

```
{
    "repo_url": "https://github.com/npm/cli",
    "repo_badge_url": "https://img.shields.io/static/v1.svg?label=FriendlyBadge&message=0.54&color=green",
    "badge_docs_url": "https://github.com/mjg123/friendly-badge#friendly-badge-api",
    "sentiment": {
        "avg_sentiment_by_project_role": {
            "NONE": 0.4615027400446527,
            "CONTRIBUTOR": 0.5628444494739655,
            "MEMBER": 0.54096370190382
        },
        "weighted_avg_sentiment": 0.5358637780868377
    },
    "comment_summary": {
        "count": 120,
        "newest": "2019-06-18T13:02:14Z",
        "oldest": "2019-02-07T00:46:10Z"
    }
}
```

## What is returned?

In that block of JSON there is `avg_sentiment_by_project_role`, which uses GitHub's concept of [Author Association](https://developer.github.com/v4/enum/commentauthorassociation/) to group users by their role on the project, and provides an average sentiment for comments from each type of person. It is expected that Collaborators, Members and Owners carry more responsibility in setting the norms for a community, so their contribution is weighted higher int the `weighted_avg_sentiment` value. This value is then used to construct the `repo_badge_url`.

If you want to see the actual comments, and how they are scored, add `?debug=true` to the end of the URL.

If you want to see just the badge, replace `json` with `svg` at the end of the URL: https://friendly-badge.herokuapp.com/npm/cli/badge.svg (you can add the badge to your repo with this URL).

## Running

You need the following as environment variables:

  - `AZURE_KEY` - for the Azure Text API: https://docs.microsoft.com/en-gb/azure/cognitive-services/text-analytics/overview
  - `GH_CLIENT_ID` and `GH_CLIENT_SECRET` - for GitHub: https://developer.github.com/v3/#oauth2-keysecret

To start a web server for the application, run:

    lein ring server-headless
	
Then browse to http://localhost:3000/{USER}/{REPO}/badge.json

## Todo

  - Actually generating a badge, not just JSON
  - Chokes on repos without any comment activity
  - Ability to remove certain users from the results (ie bots)
  - Remove code blogs before analysis
  - Better text analysis. "Sentiment" is a poor proxy for what we are tyring to measure, which is more like "friendliness" or "politeness"
  
## License

Copyright © 2019 FIXME
