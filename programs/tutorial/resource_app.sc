__config() -> {
    'resources' -> [
        {
            'source' -> 'https://raw.githubusercontent.com/gnembon/fabric-carpet/master/src/main/resources/assets/carpet/icon.png',
            'type' -> 'url',
            'target' -> 'foo/photos.zip/cm.png',
        },
        {
            'source' -> 'survival/README.md',
            'type' -> 'store',
            'target' -> 'survival_readme.md',
            'shared' -> true,
        },
        {
            'source' -> 'carpets.sc',
            'type' -> 'app',
            'target' -> 'apps/flying_carpets.sc',
            'shared' -> true,
        },
    ]
}