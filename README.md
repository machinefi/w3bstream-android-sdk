# W3bstream Android SDK
`w3bstream-android` is the Android SDK of W3bStream. This is an alpha version, which may be changed over time.

## Integration
Import `w3bstream` into your project, and sync you project.
`implementation 'com.machinefi.w3bstream:w3bstream-android:0.1'`


## Usage

### Init

```
    private val w3bStream by lazy {
        W3bStream.build(HttpService())
    }
```

### Upload data
```
    val response = w3bStream.publishEvent(url, payload, publisherKey, publisherToken)
```
