# MechList

Mod used for whitelisting on mechanist server.

## Config

make a file `credentials.json` in ./config/mechlist/

You need to get the keys from google api

```json
{
  "installed": {
    "client_id": "CLIENT ID HERE",
    "project_id": "PROJECT ID HERE",
    "auth_uri": "https://accounts.google.com/o/oauth2/auth",
    "token_uri": "https://oauth2.googleapis.com/token",
    "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
    "client_secret": "CLIENT SECRET HERE",
    "redirect_uris": [
      "urn:ietf:wg:oauth:2.0:oob",
      "http://localhost"
    ]
  }
}
```
