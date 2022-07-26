# webstream-android-framework

## Integration
Import `PebbleKit` into your project as a module, and sync you project.


## Usage
### Init

```
    private val config by lazy {
        PebbleKitConfig(
            AUTH_HOST,
            HTTPS_UPLOAD_API,
            WEB_SOCKET_UPLOAD_API
        )
    }

    private val pebbleKit by lazy {
        PebbleKit.Builder(config).build()
    }
```


### Create device
```
    private fun create() {
        lifecycleScope.launch {
            val device = pebbleKit.createDevice()
            mTvImei.text = "IMEI:${device.imei}"
            mTvSn.text = "SN:${device.sn}"
        }
    }

```

### Upload data
```
	pebbleKit.startUploading {
	    return@startUploading "{}"
	}
```
TIPS: The type of data must be json string


### Other
Sign the device
```
pebbleKit.sign(imei, sn, pubKey)
```

Set the server for uploading data
```
pebbleKit.httpsServerApi(api)
pebbleKit.socketServerApi(api)
```

Set the interval for uploading data
```
pebbleKit.uploadFrequency(mills)
```
