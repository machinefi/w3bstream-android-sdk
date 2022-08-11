# W3bStream Android SDK
W3bStreamKit is the Android SDK of W3bStream. This is an alpha version, which may be changed over time.

## Quick start
You can use AndroidStudio to open the project, then sync the project, and then run the demo on your Android phone or emulator. 
Notice. Android emulator does not support location service, please use a real machine to test.

## Integration
Import the module `w3bstream` into your project as a module, and add `implementation project(path: ':w3bstream')` in your `build.gradle` file. 
Then sync your project.

## Usage

### Init

```
    private val config by lazy {
        W3bStreamKitConfig(
            SIGN_API,
            mutableListOf(SERVER_API),
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
    w3bStreamKit.sign(data)
```

### Upload data
```
    w3bStreamKit.upload("{"latitude":"29,5640369","longitude":"106,4652020","random":"39647","timestamp":1660052772,"imei":"258897981888933","shakeCount":6}")
```
Notice: The type of data must be json string

### Other

Update server apis
```
    w3bStreamKit.updateServerApis(listOf(api))
```
Notice: Support Https (https://) and WebSocket (wss://)
