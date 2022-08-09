# W3bstream Android SDK
W3streamKit is the Android SDK of W3bstream. This is an alpha version, which may be changed over time.

## Integration
Import `w3bstream` into your project as a module, and sync you project.


## Usage
TIPS: Android emulator does not support location service, please use a real machine to test.

### Init

```
    private val config by lazy {
        W3bStreamKitConfig(
            SIGN_API,
            listOf(SERVER_API),
        )
    }

    private val w3bStreamKit by lazy {
        W3bStreamKit.Builder(config).build()
    }
```


### Authentication
```
    w3bStreamKit.authenticate(imei, sn, pubKey, signature)
```

### Get Public Key
```
    w3bStreamKit.getPublicKey()
```

### Sign data with private key
```
    w3bStreamKit.signData(data)
```

### Upload data
```
    w3bStreamKit.upload("{"imei":"126378", "latitude":34.09589161, "location":106.42410187}")
```
TIPS: The type of data must be json string

### Other

Set the server for uploading data
```
    w3bStreamKit.addServerApi(api)
    w3bStreamKit.addServerApis(apis)
```
TIPS: Support Https (https://) and WebSocket (wss://)

Remove server
```
    w3bStreamKit.removeServerApi(api)
```
